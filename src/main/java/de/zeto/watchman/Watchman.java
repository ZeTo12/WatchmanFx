package de.zeto.watchman;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.Callable;

/**
 * @author ZeTo
 */
public class Watchman implements Callable<String> {

    public static final Path DEFAULT_WATCHED_DIRECTORY = Paths.get(".").resolve("configuration");

    private Path watchedDirectory;

    public Watchman() {
        this.watchedDirectory = DEFAULT_WATCHED_DIRECTORY;
    }

    public Watchman(Path watchedDirectory) {
        this.watchedDirectory = watchedDirectory;
    }

    @Override
    public String call() throws Exception {
        // We obtain the file system of the Path
        FileSystem fs = this.watchedDirectory.getFileSystem();

        // We create the new WatchService using the new try() block
        try (WatchService watcher = fs.newWatchService()) {

            // We register the path to the service
            // We watch for creation events
            this.watchedDirectory.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
            this.watchedDirectory.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

            // Start the infinite polling loop
            WatchKey key = null;
            while (true) {
                key = watcher.take();

                // Dequeueing events
                Kind<?> kind = null;
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    // Get the type of the event
                    kind = watchEvent.kind();
                    if (StandardWatchEventKinds.OVERFLOW == kind) {
                        continue; //loop
                    } else if (StandardWatchEventKinds.ENTRY_CREATE == kind) {
                        // A new Path was created 
                        Path newPath = ((WatchEvent<Path>) watchEvent).context();
                        // Output
                        System.out.println("New path created: " + newPath);
                    } else if (StandardWatchEventKinds.ENTRY_MODIFY == kind) {
                        Path modifiedPath = ((WatchEvent<Path>) watchEvent).context();
                        Path absolutePath = modifiedPath.toAbsolutePath();
                        File absFile = absolutePath.toFile();
                        boolean canRead = absFile.canRead();
                        boolean isAbsFile = absFile.isFile();
                        boolean isAbsDirectory = absFile.isDirectory();

                        String content = new String(Files.readAllBytes(absolutePath));
                        System.out.println("content: " + modifiedPath);
                        System.out.println("modified path modified: " + modifiedPath);
                        System.out.println("modified path modified: " + absolutePath);
                        File file = modifiedPath.toFile();

                        boolean isFile = file.isFile();
                        boolean isDirectory = file.isDirectory();
                        if (isFile) {
                            System.out.println("reload configuration");
                        }

                    }
                }

                if (!key.reset()) {
                    break; //loop
                }
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        return "Finished";
    }

}
