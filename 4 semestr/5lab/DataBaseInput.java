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
}
