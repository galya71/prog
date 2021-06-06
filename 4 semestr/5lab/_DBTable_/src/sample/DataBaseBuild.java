package sample;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.Callback;

import java.sql.*;
import java.text.Collator;
import java.util.*;


public class DataBaseBuild {
    private final Connection DB;
    private final TableView TVDB;
    private final TextArea TAMsg;
    private ObservableList<ObservableList> DataDB;

    public DataBaseBuild(Connection DB, TableView TVDB, TextArea TAMsg) {
        this.DB = DB;
        this.TVDB = TVDB;
        this.TAMsg = TAMsg;
        this.DataDB = FXCollections.observableArrayList();
        Build();
    }

    private static void handle(MouseEvent event) {

    }

    public void Build() {
        try {
            //Получение данных из таблицы с помощью запроса
            TVDB.getItems().clear();
            TVDB.getColumns().clear();
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
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rsMetaData.getColumnCount(); i++)
                    row.add(rs.getString(i));
                //Заполнение данных в массив
                DataDB.add(row);
            }

            //Заполение данными таблицу
            TVDB.setItems(DataDB);

            //Контекстное меню
            ContextMenu cm = new ContextMenu();
            MenuItem mi1 = new MenuItem("Добавить запись");
            MenuItem mi2 = new MenuItem("Изменить запись");
            MenuItem mi3 = new MenuItem("Удалить запись");
            cm.setOnHidden(e -> TVDB.getSelectionModel().clearSelection());

            //При нажатии на ячейку
            TVDB.setOnMousePressed(event -> {

                //Если щелчек мыши был по заголовку таблицы
                if (event.getTarget() instanceof TableColumnHeader) return;

                //Если контекстное меню открыто
                if (cm.isShowing()) {
                    cm.hide();
                    TVDB.getSelectionModel().clearSelection();
                    return;
                }

                //Дейвствие в случае щелчка левой кнопкой мыши по строке
                if (event.getButton() == MouseButton.PRIMARY) {
                    //Если строка пуста иначе если строка не пуста
                    if (TVDB.getSelectionModel().getSelectedItem() == null) {
                        AddChangeData(DataPreparation(ColumnName, null));
                    } else {
                        //
                        AddChangeData(DataPreparation(ColumnName, TVDB.getSelectionModel().selectedItemProperty().getValue()));
                    }
                    TVDB.getSelectionModel().clearSelection();
                    return;
                }


                cm.getItems().clear();
                //Действиея для контекстного меню
                //Дейвствие в случае щелчка правой кнопкой мыши по строке
                if (event.getButton() == MouseButton.SECONDARY){
                    //Если строка пуста иначе если строка не пуста
                    if (TVDB.getSelectionModel().getSelectedItem() == null) {
                        mi1.setOnAction(e -> AddChangeData(DataPreparation(ColumnName, null)));
                        cm.getItems().addAll(mi1);
                    } else {
                        mi1.setOnAction(e -> {
                            AddChangeData(DataPreparation(ColumnName, null));
                            TVDB.getSelectionModel().clearSelection();
                        });
                        mi2.setOnAction(e -> {
                            AddChangeData(DataPreparation(ColumnName, TVDB.getSelectionModel().selectedItemProperty().getValue()));
                            TVDB.getSelectionModel().clearSelection();
                        });
                        mi3.setOnAction(e -> {
                            DeleteData(DataPreparation(ColumnName, TVDB.getSelectionModel().selectedItemProperty().getValue()));
                            TVDB.getSelectionModel().clearSelection();
                        });
                        cm.getItems().addAll(mi1,mi2,mi3);
                    }
                    cm.show(TVDB, event.getScreenX(),event.getScreenY());
                }


            });

        } catch (Exception e) {
            //Вывод сообщения об ошибке пользователю
            TAMsg.appendText("Ошибка: " + e.getMessage());
            if (DB != null) {
                try {
                    //Закрытие потока базы данных
                    DB.close();
                } catch (SQLException SQLe) {
                    TAMsg.appendText("Ошибка при отключении: " + SQLe.getMessage());
                }
            }
        }
    }

    //Обработка и конвертирование данных
    public Map<String, String> DataPreparation(ArrayList<String> AllColumnName, Object ObjectValue) {
        ArrayList<String> AllValue = new ArrayList<>();
        Map<String, String> Data = new HashMap<>();
        if (ObjectValue != null) {
            for (Object o : (ObservableList) ObjectValue)
                AllValue.add(String.valueOf(o));
            if (AllColumnName.size() != AllValue.size()) return null;
            for (int i = 0; i < AllColumnName.size(); i++)
                Data.put(AllColumnName.get(i), AllValue.get(i));
        } else
            for (String s : AllColumnName) Data.put(s, null);

        return Data;
    }

    //Удаление данных
    public void DeleteData(Map<String, String> OtherData){
        for (Map.Entry<String, String> D : OtherData.entrySet())
            if (D.getKey().equals("ID"))
                new DataBaseInput().DeleteData(D.getValue(), DB, TAMsg);
            Build();
    }

    //Добавление изменение данных
    public void AddChangeData(Map<String, String> OtherData) {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Диалоговое окно");
        dialog.setHeaderText("Изменение данных");
        StringBuilder ID = new StringBuilder();

        ButtonType OK_Type = new ButtonType("Изменить/Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType CANCEL_Type = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(OK_Type,CANCEL_Type);

        Button OK_BUT = (Button) dialog.getDialogPane().lookupButton(OK_Type);

        OK_BUT.addEventFilter(ActionEvent.ACTION, event -> {
            if (!new DataBaseInput().InputData(dialog.getResultConverter().call(OK_Type), ID.toString(), DB, TAMsg))
                event.consume();
            Build();
        });;

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(1 + OtherData.size());
        grid.setPadding(new Insets(20, 100, 10, 10));

        ArrayList<Label> Name = new ArrayList<>();
        ArrayList<TextField> Text = new ArrayList<>();

        for (Map.Entry<String, String> D : OtherData.entrySet()) {
            if (!D.getKey().equals("ID")) {
                Name.add(new Label(D.getKey()));
                Text.add(new TextField(D.getValue()));
            }else
                ID.append(D.getValue());

        }


        for (int i = 0; i < Name.size(); i++)
            grid.addRow(i, Name.get(i), Text.get(i));

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                Map<String, String> result = new HashMap<>();
                for (int i = 0; i < Name.size(); i++)
                    result.put(Name.get(i).getText(), Text.get(i).getText());
                return result;
            }
            return null;
        });

        dialog.showAndWait();

    }



}



