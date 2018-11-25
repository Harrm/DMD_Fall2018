package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner passwordScanner = new Scanner(new File("password.txt"));
        String password = passwordScanner.nextLine();
        try {
            Connection c = DriverManager
                    .getConnection("jdbc:postgresql://elmer.db.elephantsql.com:5432/lherrbcv",
                            "lherrbcv", password);
            DataGenerator dg = new DataGenerator(c);
            dg.generateTestData();

            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void testConnection() throws SQLException {
        Connection c = null;
        try {
            Scanner passwordScanner = new Scanner(new File("password.txt"));
            String password = passwordScanner.nextLine();
            c = DriverManager
                    .getConnection("jdbc:postgresql://elmer.db.elephantsql.com:5432/lherrbcv",
                            "lherrbcv", password);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        Statement s = c.createStatement();
        String sql = "SELECT *\n" +
                "FROM taxi_service.customer as c\n" +
                "       JOIN taxi_service.rent_car as r ON c.username = 'INSERT' AND r.customer = c.username\n" +
                "       JOIN taxi_service.car ON car.license_plate LIKE 'AH%' AND car.license_plate = r.car;";
        ResultSet rs = s.executeQuery(sql);
        while (rs.next()) {
            System.out.println(rs.getString("name"));
        }
        s.close();
        c.close();
    }
}
