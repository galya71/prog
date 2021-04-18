package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        final Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sample.fxml")));

        stage.setScene(new Scene(root));
        stage.setTitle("ProgressBar");
        stage.show();
    }
}