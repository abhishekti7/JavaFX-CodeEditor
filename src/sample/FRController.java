package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

public class FRController {
    private Model model;
    private String text;
    private TextFile currentTextFile;

    @FXML
    private JFXTextField tf_find, tf_replace;
    @FXML
    private JFXButton btn_repl;

    public FRController(Model model, String text, TextFile currentTextFile){
        this.model = model;
        this.text = text;
        this.currentTextFile = currentTextFile;
    }
    public void initialize(){
        TextFields.bindAutoCompletion(tf_find, text.split(" "));
    }
    public void onFind(){
        model.search(tf_find.getText(), text, 101, 256 );
    }
    public void onReplace(){
        TextFile newTextFile;
        newTextFile = model.replace(currentTextFile, tf_find.getText(), tf_replace.getText());
        currentTextFile.setContent(newTextFile.getContent());
        Stage stage = (Stage) btn_repl.getScene().getWindow();
        stage.close();
    }
}
