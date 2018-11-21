package main;

import org.postgresql.Driver;

import java.sql.*;

public class Application {
    public static void main(String[] args) throws SQLException {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            Driver driver = new Driver();
            c = DriverManager
                    .getConnection("jdbc:postgresql://elmer.db.elephantsql.com:5432/lherrbcv",
                            "lherrbcv", "password");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        Statement s = c.createStatement();
        String sql = "SELECT *\n" +
                "FROM taxi_service.customer as c\n" +
                "       JOIN taxi_service.rents as r ON c.username = 'INSERT' AND r.customer = c.username\n" +
                "       JOIN taxi_service.car ON car.license_plate LIKE 'AH%' AND car.license_plate = r.car;";
        ResultSet rs = s.executeQuery(sql);
        while (rs.next()) {
            System.out.println(rs.getString("name"));
        }
        s.close();
        c.close();
    }
}
