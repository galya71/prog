package sample;

import java.sql.Connection;
import java.sql.DriverManager;

public class DataBaseConnection {
    public static Connection Connecting(String url, String login, String password, StringBuilder getMsg){
        try {
            //Подключение к базе данных
            Connection con = DriverManager.getConnection(url,login,password);
            //Очистка и вывод сообщения
            getMsg.delete(0,getMsg.length()).append("Успешное подключение к серверу\n");
            //Возвращение подключения
            return  con;
        }catch(Exception e){
            //Очистка и вывод сообщения ошибки
            getMsg.delete(0, getMsg.length())
                    .append("Не удалось подключиться к серверу\n" + "Причина: "+e.getMessage()+"\n");
            System.out.println(e.getMessage());
            //Вывод
            return null;
        }
    }


}
