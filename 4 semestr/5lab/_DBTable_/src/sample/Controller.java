package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;


import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;



public class Controller implements Initializable {
    Connection DB = null;

    @FXML
    private AnchorPane APMenu;

    @FXML
    private TextArea TAMsg;

    @FXML
    private TableView TVDB;

    //Инициализация при запуске формы
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Переменная для перехвата сообщения
        StringBuilder Msg = new StringBuilder();
        //Подключение и получение потока базы данных для дальнейшего взаимодействия
        DB = DataBaseConnection.Connecting("jdbc:mysql://localhost/Lab5","user","", Msg);
        //Вывод сообщения на текстовую форму пользователя
        TAMsg.appendText(String.valueOf(Msg));
        //Фомирование данных из базы данных в таблицу
        if (DB != null) new DataBaseBuild(DB, TVDB, TAMsg);
    }
}
