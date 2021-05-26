package sample;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.util.Callback;

import java.sql.*;

public class DataBaseBuild{
    private final Connection DB;
    private final TableView TVDB;
    private final TextArea TAMsg;
    private ObservableList<ObservableList> DataDB;

    public DataBaseBuild(Connection DB, TableView TVDB, TextArea TAMsg){
        this.DB = DB;
        this.TVDB = TVDB;
        this.TAMsg = TAMsg;
        this.DataDB = FXCollections.observableArrayList();
        Build();
    }

    public void Build() {
            try {
                //Получение данных из таблицы с помощью запроса
                Statement stmt = DB.createStatement();
                ResultSet rs = stmt.executeQuery("select * from userdata");
                ResultSetMetaData rsMetaData = rs.getMetaData();

                int ColumnCount = rsMetaData.getColumnCount();

                //Создание столбцов в таблице
                for (int i = 1; i <= ColumnCount; i++) {
                    final int j = i;

                    //Формирование столбцов с именем для таблицы
                    TableColumn col = new TableColumn(rsMetaData.getColumnName(i));
                    col.setCellValueFactory(
                            (Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param ->
                                    new SimpleStringProperty(param.getValue().get(j - 1).toString()));
                    //Заполнение таблицы полученными значениями
                    Platform.runLater(() -> TVDB.getColumns().addAll(col));
                }

                DataDB.clear();
                //Заполнение таблицы данными из базы данных
                while(rs.next()){
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for(int i=1 ; i<=rsMetaData.getColumnCount(); i++)
                        row.add(rs.getString(i));
                    //Заполнение данных в массив
                    DataDB.add(row);
                }

                //Заполение данными таблицу
                TVDB.setItems(DataDB);

            } catch (Exception e){
                //Вывод сообщения об ошибке пользователю
                TAMsg.appendText("Ошибка: " + e.getMessage());
                try {
                    //Закрытие потока базы данных
                    DB.close();
                } catch (SQLException SQLe) {
                    TAMsg.appendText("Ошибка при отключении: " + SQLe.getMessage());
                }
            }
    }
}
