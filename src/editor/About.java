package editor;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class About {

    @FXML
    private JFXButton btn;
    @FXML
    private AnchorPane anchor;

    public void initialize() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                anchor.requestFocus();
            }
        });
    }
    public void onClick(){
        try {
            Desktop.getDesktop().browse(new URL("https://github.com").toURI());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    public void onClose() {
        Node source = (Node)btn;
        Window stage = source.getScene().getWindow();
        stage.hide();
    }
}
