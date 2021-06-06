package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

public class DataBaseInput {

    //Связь с базой данных и удаление данных
    public void DeleteData(String ID, Connection DB, TextArea TAMsg){
        try {
            PreparedStatement ps = DB.prepareStatement("DELETE FROM userdata WHERE ID = ?");
            ps.setString(1, ID);
            ps.executeUpdate();
            ps.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
            String msg = "Не удалось подключится к серверу\n Ошибка:"+e.getMessage()+"\n";
            InfoDialog(msg);
            TAMsg.appendText(msg);
        }
    }

    //Связь с базой данных и добавление/изменение данных
    public boolean InputData(Map<String,String> Data, String ID, Connection DB, TextArea TAMsg){
        try {

            for (Map.Entry<String,String> item: Data.entrySet()){
                if (item.getValue() == null || item.getValue().length() == 0){
                    InfoDialog("Вы не заполнили все поля");
                    return false;
                }
            }

            PreparedStatement ps;
            if (ID != null) {
                ps = DB.prepareStatement("Select * from userdata where ID = ?");
                ps.setString(1, ID);
                ResultSet rs = ps.executeQuery();

                if (rs.next()){
                    StringBuilder col = new StringBuilder();
                    for (Map.Entry item : Data.entrySet()){
                        if (col.length() != 0) col.append(',');
                        col.append(item.getKey()).append(" = ?");
                    }

                    ps = DB.prepareStatement("UPDATE userdata SET "+ col +" WHERE ID = ?");

                    int i = 1;
                    for (Map.Entry<String,String> item: Data.entrySet()) {
                        ps.setString(i, item.getValue());
                        i++;
                    }
                    ps.setString(Data.size()+1,ID);

                    ps.executeUpdate();
                    ps.close();
                    return true;
                }

            }

            String col = Data.keySet().toString().replaceAll("[\\[\\]]", "");
            String val = "?".repeat(Data.size()).replaceAll(".(?=.)", "$0,");

            ps = DB.prepareStatement("Insert into userdata ("+col+") values ("+val+")");

            int i = 1;
            for (Map.Entry<String,String> item: Data.entrySet()) {
                ps.setString(i, item.getValue());
                i++;
            }
            ps.execute();
            ps.close();
            return true;

        }catch (Exception e){
            System.out.println(e.getMessage());
            String msg = "Не удалось подключится к серверу\n Ошибка:"+e.getMessage()+"\n";
            InfoDialog(msg);
            TAMsg.appendText(msg);
            return false;
        }
    }

    private void InfoDialog(String Text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(Text);
        alert.showAndWait();
    }

    private static boolean isNumeric(String str) {
        try {
            int number = Integer.parseInt(str);
            return number > 0;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
