package main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class QueriesImplementation implements Queries {

    private Connection connection;

    QueriesImplementation(Connection connection) {
        this.connection = connection;
    }

    public ResultSet query1(String username) throws SQLException {
        Statement s = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String sql = "SELECT *\n" +
                "FROM taxi_service.customer as c\n" +
                "       JOIN taxi_service.ride as r ON c.username = '" + username + "' AND r.username = c.username " +
                "       AND r.license_plate LIKE 'AN%' AND date(r.order_time) = '2018-11-23'\n" +
                "       JOIN taxi_service.car ON car.license_plate = r.license_plate AND car.color = 'red';";
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

    public ResultSet query2(LocalDate date) throws SQLException {
        Statement s = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String sql = "SELECT concat(to_char(h, 'HH24:MI'), 'h-', to_char(h + '1 hour', 'HH24:MI'), 'h') as \"time\", count(DISTINCT socket_id) as total " +
                "FROM generate_series(timestamp '2000-01-01 00:00', timestamp '2000-01-01 23:59', interval '1 hour') as h " +
                "LEFT JOIN (SELECT start_time, socket_id FROM taxi_service.charge WHERE start_time::DATE = '" + date.toString() + "')" +
                "AS c ON EXTRACT(HOUR FROM h) = EXTRACT(HOUR FROM start_time) " +
                "GROUP BY h;";
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
        String sql = "SELECT * FROM (SELECT  100 * count(DISTINCT r.license_plate) / (SELECT count(*) FROM taxi_service.car) as \"Morning\" FROM taxi_service.ride as r " +
                "WHERE age(r.departure_time, '2018-11-23') < '7 days' AND extract(HOUR FROM r.departure_time) BETWEEN 7 AND 9) a " +
                "JOIN " +
                "(SELECT 100 * count(DISTINCT r.license_plate) / (SELECT count(*) FROM taxi_service.car) as \"Afternoon\" FROM taxi_service.ride as r " +
                "WHERE age(r.departure_time, '2018-11-23') < '7 days' AND extract(HOUR FROM r.departure_time) BETWEEN 12 AND 13) b ON TRUE " +
                "JOIN " +
                "(SELECT 100 * count(DISTINCT r.license_plate) / (SELECT count(*) FROM taxi_service.car) as \"Evening\" FROM taxi_service.ride as r " +
                "WHERE age(r.departure_time, '2018-11-23') < '7 days' AND extract(HOUR FROM r.departure_time) BETWEEN 17 AND 18) c ON TRUE;";
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

    @Override
    public ResultSet query5(LocalDate date) throws SQLException {
        Statement s = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String sql = "SELECT sum(distance_to_order_location) / count(distinct license_plate) as \"Average distance to pick up point\", " +
                "avg(arrival_time - departure_time) as \"average trip duration\" " +
                "FROM taxi_service.ride as r " +
                "WHERE departure_time::date = '" + date.toString() + "';";
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