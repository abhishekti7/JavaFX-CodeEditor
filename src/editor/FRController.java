package editor;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FRController {
    private Model model;
    private String text;
    private TextFile currentTextFile;
    private JFXTextArea textArea;

    @FXML
    private JFXTextField tf_find, tf_replace;
    @FXML
    private JFXButton btn_repl;

    public FRController(Model model, String text, TextFile currentTextFile, JFXTextArea textArea){
        this.model = model;
        this.text = text;
        this.currentTextFile = currentTextFile;
        this.textArea = textArea;
    }
    public void initialize(){
        TextFields.bindAutoCompletion(tf_find, text.split(" "));
    }
    public void onFind(){
        List<Integer> index;
        index = model.search(tf_find.getText(), text, 101, 256 );
        setHighlight(index);
    }
    public void setHighlight(List<Integer> indexes){
        for(int i=0;i<indexes.size()-1;i++){
            textArea.selectRange(indexes.get(i), indexes.get(i+1));
        }
    }
    public void onReplace(){
        TextFile newTextFile;
        newTextFile = model.replace(currentTextFile, tf_find.getText(), tf_replace.getText());
        currentTextFile.setContent(newTextFile.getContent());
        Stage stage = (Stage) btn_repl.getScene().getWindow();
        stage.close();
    }
}
