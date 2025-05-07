package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    final String URL = "jdbc:mysql://localhost:3306/nesrine";

    final   String USERNAME = "root";

    final String PWD = "";

    Connection con ;

    public static MyDatabase instance ;
    private MyDatabase (){
        try {
            con = DriverManager.getConnection(URL,USERNAME,PWD);

            System.out.println("connnnnected !!!");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    public   static MyDatabase getInstance(){

        if(instance==null)
            instance = new MyDatabase() ;

        return  instance ;
    }

    public Connection getCon() {
        return con;
    }
}