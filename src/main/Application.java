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
        query1("shoot");
        c.close();
    }

    private static ResultSet query1(String username) throws SQLException {
        Statement s = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String sql = "SELECT *\n" +
                "FROM taxi_service.customer as c\n" +
                "       JOIN taxi_service.ride as r ON c.username = '" + username + "' AND r.customer = c.username " +
                "       AND r.car LIKE 'AN%' AND date(r.order_time) = '2018-11-23'\n" +
                "       JOIN taxi_service.car ON car.license_plate = r.car AND car.color = 'red';";
        ResultSet resultSet = s.executeQuery(sql);
        for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
            System.out.print(resultSet.getMetaData().getColumnName(i + 1) + " ");
        }
        System.out.println();
        while (resultSet.next()) {
            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                System.out.print(resultSet.getString(i + 1) + " ");
            }
            System.out.println();
        }
        resultSet.beforeFirst();
        return resultSet;
    }

}
