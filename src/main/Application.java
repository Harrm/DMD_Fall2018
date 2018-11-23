package main;

import java.io.File;
import java.sql.*;
import java.util.Scanner;

public class Application{


    public static void main(String[] args) throws SQLException {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            String password = null;
            try {
                Scanner passwordScanner = new Scanner(new File("password.txt"));
                password = passwordScanner.nextLine();
                if (password == null) throw new Exception("add password.txt file with password");
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
            c = DriverManager
                    .getConnection("jdbc:postgresql://elmer.db.elephantsql.com:5432/lherrbcv",
                            "lherrbcv", password);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        QueriesImplementation queries = new QueriesImplementation(c);
        queries.query3();
        c.close();
    }

}
