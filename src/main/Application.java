package main;

import java.io.File;
import java.sql.*;
import java.util.Scanner;

public class Application {

    private static Connection c = null;

    public static void main(String[] args) throws SQLException {
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
        test1("pidor");
        c.close();
    }

    private static ResultSet test1(String username) throws SQLException {
        Statement s = c.createStatement();
        String sql = "SELECT *\n" +
                "FROM taxi_service.customer as c\n" +
                "       JOIN taxi_service.rents as r ON c.username = '" + username + "' AND r.customer = c.username\n" +
                "       JOIN taxi_service.car ON car.license_plate LIKE 'AN%' AND car.license_plate = r.car AND car.color = 'red';";
        return s.executeQuery(sql);
    }

}
