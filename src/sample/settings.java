package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;

public class settings {

    @FXML
    private TreeView treeView;

    @FXML
    private JFXComboBox font_combo;
    @FXML
    private AnchorPane general_view;
    @FXML
    private JFXButton btn_cancel;

    private final String filepath = "C:\\Users\\Abhishek Tiwari\\IdeaProjects\\Project\\src\\sample\\config.txt";

    public void initialize(){
        initTreeView();
        displayFonts();
        general_view.setVisible(false);
    }
    public void initTreeView(){
        TreeItem rootItem = new TreeItem("Settings");

        TreeItem general = new TreeItem("General");

        rootItem.getChildren().add(general);
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            TreeItem<String> selectedItem = (TreeItem<String>) newValue;
            if (selectedItem.getValue().equals(general.getValue())){
                general_view.setVisible(true);
            }
        });

        treeView.setRoot(rootItem);
    }

    public void displayFonts(){
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        font_combo.getItems().addAll(fonts);
        general_view.setVisible(true);
        try {
            FileReader fileReader = new FileReader(filepath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String font = bufferedReader.readLine();
            font_combo.getSelectionModel().select(font);
//            font_combo.setValue(font);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setFont() {
        try {
            String font = (String) font_combo.getValue();
            FileWriter fileWriter = new FileWriter(filepath);
            fileWriter.write(font + "\n");
            fileWriter.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Font set!");
            alert.showAndWait();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            general_view.setVisible(false);
        }
    }
    public void onClose(){
        Stage stage = (Stage) btn_cancel.getScene().getWindow();
        stage.close();
    }

}
