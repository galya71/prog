package sample;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.Pair;

import java.sql.*;
import java.util.*;

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
                ArrayList<String> ColumnName = new ArrayList<>();
                int ColumnCount = rsMetaData.getColumnCount();

                //Создание столбцов в таблице
                for (int i = 1; i <= ColumnCount; i++) {
                    final int j = i;

                    String Name = rsMetaData.getColumnName(i);
                    ColumnName.add(Name);

                    //Формирование столбцов с именем для таблицы
                    TableColumn col = new TableColumn(Name);
                    col.setCellValueFactory(
                            (Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param ->
                             new SimpleStringProperty(param.getValue().get(j - 1).toString())
                    );

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
                TVDB.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                    AddChange(DataPreparation(ColumnName, newSelection));
                });


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

    public Map<String, String> DataPreparation(ArrayList<String> AllColumnName, Object ObjectValue){
        ArrayList<String> AllValue = new ArrayList<>();
        for (Object o: (ObservableList) ObjectValue)
            AllValue.add(String.valueOf(o));

        if (AllColumnName.size() != AllValue.size()) return null;

        Map<String,String> Data = new HashMap<>();

        for (int i = 0; i < AllColumnName.size();i++)
            Data.put(AllColumnName.get(i),AllValue.get(i));

        return Data;
    }

    public void AddChange(Map<String,String> OtherData){
        Dialog<Map<String,String>> dialog = new Dialog<>();
        dialog.setTitle("Диалоговое окно");
        dialog.setHeaderText("Изменение данных");


        dialog.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Изменить", ButtonBar.ButtonData.OK_DONE),
                new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(1+OtherData.size());
        grid.setPadding(new Insets(20, 100, 10, 10));

        ArrayList<Label> Name = new ArrayList<>();
        ArrayList<TextField> Text = new ArrayList<>();

        for(Map.Entry<String,String> D: OtherData.entrySet()) {
            Name.add(new Label(D.getKey()));
            Text.add(new TextField(D.getValue()));
        }


        for(int i = 0; i < Name.size();i++)
            grid.addRow(i,Name.get(i),Text.get(i));

        dialog.getDialogPane().setContent(grid);


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton.getButtonData() ==  ButtonBar.ButtonData.OK_DONE) {
                Map<String,String> result = new HashMap<>();
                for(int i = 0; i < Name.size();i++)
                    result.put(Name.get(i).getText(),Text.get(i).getText());
                return result;
            }
            return null;
        });

        Optional<Map<String,String>> result = dialog.showAndWait();

        result.ifPresent(System.out::println);
    }


}
