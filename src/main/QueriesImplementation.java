package main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class QueriesImplementation implements Queries {

    private Connection connection;

    public QueriesImplementation(Connection connection) {
        this.connection = connection;
    }

    public ResultSet query1(String username) throws SQLException {
        Statement s = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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

    public ResultSet query3() throws SQLException {
        Statement s = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String sql = "SELECT * FROM (SELECT  100 * count(DISTINCT r.car) / (SELECT count(*) FROM taxi_service.car) as \"Morning\" FROM taxi_service.ride as r " +
                        "WHERE age(r.departure_time) < '7 days' AND extract(HOUR FROM r.departure_time) BETWEEN 7 AND 9) a " +
                        "JOIN " +
                        "(SELECT 100 * count(DISTINCT r.car) / (SELECT count(*) FROM taxi_service.car) as \"Afternoon\" FROM taxi_service.ride as r " +
                        "WHERE age(r.departure_time) < '7 days' AND extract(HOUR FROM r.departure_time) BETWEEN 12 AND 13) b ON TRUE " +
                        "JOIN " +
                        "(SELECT 100 * count(DISTINCT r.car) / (SELECT count(*) FROM taxi_service.car) as \"Evening\" FROM taxi_service.ride as r " +
                        "WHERE age(r.departure_time) < '7 days' AND extract(HOUR FROM r.departure_time) BETWEEN 17 AND 18) c ON TRUE;";
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
