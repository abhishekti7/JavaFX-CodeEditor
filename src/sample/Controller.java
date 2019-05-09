package sample;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.TextField;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    @FXML
    private JFXTextArea textArea;
    @FXML
    private TreeView directory_tree;
    @FXML
    private HBox hbox;
    @FXML
    private HBox tool_hbox;
    @FXML
    private JFXTextField errorboard;
    @FXML
    private HBox console;
    @FXML
    private JFXTextArea consoleText;
    @FXML
    private CheckBox punishCheck;
    @FXML
    private JFXTextField tf_find, tf_replace;
    @FXML
    private JFXButton btn_find, btn_repl;

    private TextFile currentTextFile;
    private int count;
    private boolean punish;
    private String consoleMessage;
    private final Model model;
    private final String filepath = "C:\\Users\\Abhishek Tiwari\\IdeaProjects\\Project\\src\\sample\\config.txt";

    public Controller(Model model) {
        this.model = model;
    }

    public void initialize(){
        punish = false;
        currentTextFile = new TextFile(new File(""),"");
        errorboard.setStyle("-fx-text-fill: #E53935; -fx-background-color: #000000; ");
        consoleText.setStyle("-fx-background:#000000; -fx-font-family: Consolas; -fx-highlight-fill: #00ff00; -fx-highlight-text-fill: #000000; -fx-text-fill: #00ff00; ");
        console.setVisible(false);
        console.managedProperty().bind(console.visibleProperty());
        directory_tree.setVisible(false);
        directory_tree.managedProperty().bind(directory_tree.visibleProperty());
        directory_tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && newValue != oldValue){
                directory_tree.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                            if(mouseEvent.getClickCount() == 2){
                               openFromTree(newValue);
                            }
                        }
                    }
                });
            }
        });

        hbox.setPrefWidth(31);
        HBox.setHgrow(tool_hbox, Priority.ALWAYS);
        HBox.setHgrow(consoleText, Priority.ALWAYS);
        setFont();
        Platform.runLater(() -> textArea.requestFocus());
        textArea.textProperty().addListener((observable, oldValue, newValue) -> onTextChange());
        textArea.textProperty().addListener((observable, oldValue, newValue) -> onTextChange());

        openLastSession();
    }

    public void openLastSession(){
        try {
            FileReader fr = new FileReader(filepath);
            BufferedReader br = new BufferedReader(fr);
            String s;
            s = br.readLine();
            s = br.readLine();
            br.close();
            File file = new File(s);
            IOResult<TextFile> io = model.open(file);

            if (io.isOk() && io.hasData()) {
                currentTextFile = io.getData();
                initDirectoryTree(file.getParentFile());
//                System.out.println(file.getParentFile());
                textArea.clear();
                textArea.setText(currentTextFile.getContent());
            } else {
                System.out.println("Failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFont(){
        try {
            FileReader fileReader = new FileReader("C:\\Users\\Abhishek Tiwari\\IdeaProjects\\Project\\src\\sample\\config.txt");
            BufferedReader br = new BufferedReader(fileReader);
            String font = br.readLine();
            textArea.setFont(Font.font(font));
            br.close();
            fileReader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void openFromTree(Object value) {
        String filename = value.toString();
        filename = filename.substring(18, filename.length() - 2);
//        System.out.println(filename.substring(18,filename.length()-1));
        File directory;
        if (currentTextFile.getFile().getPath() != "" && filename.endsWith(".java")) {
            directory = currentTextFile.getFile().getParentFile();
            for (File file : directory.listFiles()) {
                if (file.getName().equals(filename.trim())) {
                    if (file != null) {
                        IOResult<TextFile> io = model.open(file);

                        if (io.isOk() && io.hasData()) {
                            currentTextFile = io.getData();
                            textArea.clear();
                            textArea.setText(currentTextFile.getContent());
                        } else {
                            System.out.println("Failed");
                        }
                    }
                }
            }
        }
    }
    public void onOpen() {
        FileChooser fileChooser = new FileChooser();
        if(currentTextFile.getFile().getPath()!=""){
            fileChooser.setInitialDirectory(new File(currentTextFile.getFile().getParent()));
        }
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("(*.java)", "*.java");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            IOResult<TextFile> io = model.open(selectedFile);

            if (io.isOk() && io.hasData()) {
                currentTextFile = io.getData();
                initDirectoryTree(selectedFile.getParentFile());
                System.out.println(selectedFile.getParentFile());
                textArea.clear();
                textArea.setText(currentTextFile.getContent());
            } else {
                System.out.println("Failed");
            }
        }
    }
    public void onSave(){
        if(currentTextFile.getFile().getPath()==""){
            onSaveAs();
        }else
            model.save(currentTextFile.getFile(),textArea.getText());
    }
    public void onSaveAs() {
        File file;
        if(currentTextFile.getFile().getPath()==""){
            FileChooser fileChooser = new FileChooser();

            //Set extension filter for text files
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("java files (*.java)", "*.java");
            fileChooser.getExtensionFilters().add(extFilter);
            file = fileChooser.showSaveDialog(null);
        }else{
            file = currentTextFile.getFile();
        }
        if(file!=null) {
            model.save(file, textArea.getText());
            IOResult<TextFile> io = model.open(file);
            if (io.isOk() && io.hasData()) {
                currentTextFile = io.getData();
            }
        }
    }
    public void onFindAndReplace() throws IOException {
        TextFile textFile = new TextFile(currentTextFile.getFile(), textArea.getText());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("findandreplace.fxml"));
        loader.setControllerFactory(t -> new FRController(new Model(), textArea.getText(), textFile, textArea));


        Stage stage=new Stage();
        stage.setTitle("Find and Replace");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(loader.load(),600,200);
        stage.setScene(scene);
        stage.showAndWait();
        textArea.setText(textFile.getContent());
    }
    public void onAbout() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("about.fxml"));
        loader.setControllerFactory(t -> new About());

        Stage stage = new Stage();
        stage.setTitle("About the Project");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(loader.load(), 400,300);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.showAndWait();

    }
    public void onSettings() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("settings.fxml"));
        loader.setControllerFactory(t -> new settings());

        Stage stage = new Stage();
        stage.setTitle("Preferences");
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(loader.load(), 600,400);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.showAndWait();
        setFont();
    }
    public void callDirectoryTree(){
        count++;
        File file;
        if(currentTextFile.getFile().getPath()=="") {
            file = new File(System.getProperty("user.dir"));
        }
        else{
            file = new File(currentTextFile.getFile().getPath());
        }
//        System.out.println(count);
        if(count%2==0){
            directory_tree.setVisible(false);
            directory_tree.managedProperty().bind(directory_tree.visibleProperty());
            hbox.setPrefWidth(31);
        }else{
            directory_tree.setVisible(true);
            hbox.setPrefWidth(225);
            directory_tree.managedProperty().bind(directory_tree.visibleProperty());
            initDirectoryTree(file.getParentFile());
        }
    }
    public void initDirectoryTree(File file){
        directory_tree.setVisible(true);
        hbox.setPrefWidth(225);
        directory_tree.managedProperty().bind(directory_tree.visibleProperty());
        directory_tree.setRoot(getNodesForDirectory(file));
    }
    public TreeItem<String> getNodesForDirectory(File directory) { //Returns a TreeItem representation of the specified directory
        TreeItem<String> root = new TreeItem<String>(directory.getName());
        for(File f : directory.listFiles()) {
            //System.out.println("Loading " + f.getName());
            if(f.isDirectory()) { //Then we call the function recursively
                root.getChildren().add(getNodesForDirectory(f));
            } else {
                if(f.getName().endsWith(".java")){
                    root.getChildren().add(new TreeItem<String>(f.getName()));
                }
            }
        }
        return root;
    }
    public void onNew(){
        textArea.setText("");
    }
    public void onClose(){
        model.close(currentTextFile.getFile());
    }
    public void onCut(){
        textArea.cut();
    }
    public void onPaste(){
        textArea.paste();
    }
    public void onCopy(){
        textArea.copy();
    }
    public void onPunishSelect(){
        if(currentTextFile.getFile().getPath()==""){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Save the file to use this feature.");
            alert.setGraphic(new ImageView("file:C:\\Users\\Abhishek Tiwari\\IdeaProjects\\Project\\src\\sample\\icons\\error.png"));
            alert.show();
        }else{
            if(!punishCheck.isSelected()) {
                punishCheck.setSelected(true);
            }
            else {
                punishCheck.setSelected(false);
                errorboard.setText("Hello There!");
            }
            punish = true;
        }
    }
    public void setMessage(List<String> errors){
        if(currentTextFile.getFile().getPath()==""){
            consoleMessage = "Terminal \n";
        }else {
            consoleMessage = "Terminal \n" + currentTextFile.getFile().getPath();
        }
        console.setVisible(true);
        String msg=consoleMessage+"\n\n";
        for(int i=0; i< errors.size() ; i++){
            msg = msg +  errors.get(i) + "\n";
        }
        if(errors.size()==0){
            msg = msg + "\n\n Process compiled with 0 errors.\n";
        }else {
            msg = msg + "\n\n Process terminated with "+errors.size()+" errors.\n";
        }
        consoleText.setText(msg);
        System.out.println(currentTextFile.getFile().getPath().length());
        consoleText.selectRange(9,currentTextFile.getFile().getPath().length()+10);
    }

    public void onConsoleClose(){
        console.setVisible(false);
    }
    public void onTextChange(){
        if(punishCheck.isSelected()) {
            onSave();
            List<String> errors = model.checkSyntax(currentTextFile);System.out.println(errors);
            if(errors.size()==0) {
                errorboard.setText("Hello There!");
                textArea.setStyle("-fx-background-color: BLACK; -fx-text-fill: WHITE;");
            }
            else {
                errorboard.setText(errors.get(0));
                textArea.setStyle("-fx-background-color: WHITE; -fx-text-fill: BLACK;");
            }
        }
    }

    public void onCompile(){

        System.out.println(currentTextFile.getFile());
        if(currentTextFile.getFile().getPath()==""){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Save the file before proceeding.");
            alert.setGraphic(new ImageView("file:C:\\Users\\Abhishek Tiwari\\IdeaProjects\\Project\\src\\sample\\icons\\error.png"));
            alert.show();
        }else{
            model.save(currentTextFile.getFile(), textArea.getText());
            List<String> errors = model.checkSyntax(currentTextFile);
            setMessage(errors);
        }
    }
}
