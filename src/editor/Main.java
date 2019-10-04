package editor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("editor.fxml"));
        loader.setControllerFactory(t -> new Controller(new Model()));

        primaryStage.setOnCloseRequest(e -> {
         e.consume();  //Tells java that we will take care of the event from here
         closeProgram();
         });

        primaryStage.setTitle("Java Code Editor");
        primaryStage.getIcons().add(new Image("file:C:\\Users\\Abhishek Tiwari\\IdeaProjects\\Project\\src\\editor\\icons\\logo.png"));
        Scene scene = new Scene(loader.load(), 600,400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void closeProgram(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.NO, ButtonType.YES);
        alert.setGraphic(new ImageView("file:C:\\Users\\Abhishek Tiwari\\IdeaProjects\\Project\\src\\editor\\icons\\warning.png"));
        alert.setHeaderText("Are you sure you want to exit?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get()==ButtonType.YES){
            System.exit(0);
        }else{
            alert.close();
        }
    }

}
