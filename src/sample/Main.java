package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sample.fxml")));
        root.getStylesheets().add("sample/style.css");
        primaryStage.setTitle("Aplikacija za izposojo vozil");
        primaryStage.getIcons().add(new Image("sample/icon.png"));
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setMinHeight(440);
        primaryStage.setMinWidth(615);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
