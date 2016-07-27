package de.zeto.watchman.ui;

import de.zeto.watchman.Watchman;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class FXMLController implements Initializable {
    
    @FXML
    private Label label;

    @FXML
    private Button button;

    Watchman watchman;

    ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    Future<String> watchmanHandle;

    @FXML
    private void handleButtonAction(ActionEvent event) {

        if (this.watchmanHandle == null || this.watchmanHandle.isDone()) {
            this.watchmanHandle = this.singleThreadExecutor.submit(this.watchman);
            this.button.setText("Stop Watchman");
        } else {
            boolean cancel = this.watchmanHandle.cancel(true);
            this.button.setText("Start Watchman");
        }

        EventType<? extends ActionEvent> eventType = event.getEventType();
        
        System.out.println("Watchman is watching you!");
        label.setText("Watchman is watching you!");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.watchman = new Watchman();
        this.singleThreadExecutor = Executors.newSingleThreadExecutor();
        this.button.setText("Start Watchman");
    } 
}
