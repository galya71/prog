package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Создание основого окна
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("DataTableWork");
        primaryStage.resizableProperty().set(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        //Создание действия при закрытие окна
        primaryStage.setOnCloseRequest(we -> {
            System.exit(0);
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
