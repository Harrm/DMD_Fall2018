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

    private ResultSet executePrintReturn(String sql) throws SQLException {
        Statement s = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        boolean hasMoreResultSets = s.execute(sql);
        ResultSet resultSet = null;
        while ( hasMoreResultSets || s.getUpdateCount() != -1 ) {
            if (hasMoreResultSets) {
                resultSet = s.getResultSet();
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
            hasMoreResultSets = s.getMoreResults();
        }
        return null;
    }

    @Override
    public ResultSet query1(String username) throws SQLException {
        String sql = "SELECT * " +
                "FROM taxi_service.customer as c " +
                "       JOIN taxi_service.ride as r ON c.username = '" + username + "' AND r.username = c.username " +
                "       AND r.license_plate LIKE 'AN%' AND date(r.order_time) = '2018-11-23' " +
                "       JOIN taxi_service.car ON car.license_plate = r.license_plate AND car.color = 'red';";
        return executePrintReturn(sql);
    }

    @Override
    public ResultSet query2(LocalDate date) throws SQLException {
        String sql = "CREATE TEMPORARY TABLE result(\"time\" VARCHAR(100), total int);\n" +
                "DO $$ BEGIN\n" +
                "FOR h IN 0..23 LOOP\n" +
                "  INSERT INTO result\n" +
                "   SELECT concat(to_char('00:00'::time + h * interval '1 hour', 'HH24:MI'), 'h-', to_char('00:00'::time + (h + 1) * interval '1 hour', 'HH24:MI'), 'h') as \"time\", count(DISTINCT socket_id) as total\n" +
                "          FROM taxi_service.charge WHERE start_time < '" + date.toString() + " 00:00'::timestamp + (h + 1) * interval '1 hour' and finish_time >'" + date.toString() + " 00:00'::timestamp + h * interval '1 hour';\n" +
                "          END LOOP;\n" +
                "END$$;\n" +
                "SELECT * FROM result;\n" +
                "DROP TABLE result;";
        return executePrintReturn(sql);
    }

    @Override
    public ResultSet query3() throws SQLException {
        String sql = "SELECT * FROM (SELECT  100 * count(DISTINCT r.license_plate) / (SELECT count(*) FROM taxi_service.car) as \"Morning\" FROM taxi_service.ride as r " +
                "WHERE age(r.departure_time, '2018-11-23') < '7 days' AND extract(HOUR FROM r.departure_time) BETWEEN 7 AND 9) a " +
                "JOIN " +
                "(SELECT 100 * count(DISTINCT r.license_plate) / (SELECT count(*) FROM taxi_service.car) as \"Afternoon\" FROM taxi_service.ride as r " +
                "WHERE age(r.departure_time, '2018-11-23') < '7 days' AND extract(HOUR FROM r.departure_time) BETWEEN 12 AND 13) b ON TRUE " +
                "JOIN " +
                "(SELECT 100 * count(DISTINCT r.license_plate) / (SELECT count(*) FROM taxi_service.car) as \"Evening\" FROM taxi_service.ride as r " +
                "WHERE age(r.departure_time, '2018-11-23') < '7 days' AND extract(HOUR FROM r.departure_time) BETWEEN 17 AND 18) c ON TRUE;";
        return executePrintReturn(sql);
    }

    @Override
    public ResultSet query4(String username) throws SQLException {
        String sql = "SELECT p1.*, p2.real_bank_receipt_id as \"doubling receipt\" FROM taxi_service.payment as p1 " +
                "JOIN taxi_service.payment as p2 ON p1.username = p2.username AND p1.amount = p2.amount AND p1 .real_bank_receipt_id < p2.real_bank_receipt_id " +
                "WHERE p1.username = '" + username + "';";
        return executePrintReturn(sql);
    }

    @Override
    public ResultSet query5(LocalDate date) throws SQLException {
        String sql = "SELECT sum(distance_to_order_location) / count(distinct license_plate) as \"Average distance to pick up point\", " +
                "avg(arrival_time - departure_time) as \"Average trip duration\" " +
                "FROM taxi_service.ride as r " +
                "WHERE departure_time::date = '" + date.toString() + "' AND arrival_time IS NOT NULL;";
        return executePrintReturn(sql);
    }

    @Override
    public ResultSet query6() throws SQLException {
        String sql = "WITH morn as (SELECT * FROM taxi_service.ride WHERE departure_time::time BETWEEN '7:00' AND '10:00'),\n" +
                " noon as (SELECT * FROM taxi_service.ride WHERE departure_time::time BETWEEN '12:00' AND '14:00'),\n" +
                " even as (SELECT * FROM taxi_service.ride WHERE departure_time::time BETWEEN '17:00' AND '19:00')\n" +
                "SELECT *\n" +
                "FROM (SELECT point(ROUND(departure_location [0]::numeric, 4), ROUND(departure_location [ 1]::numeric, 4)) as morning_pickup,\n" +
                "             count(*),\n" +
                "             ROW_NUMBER() over (ORDER BY count(*) DESC) as top\n" +
                "      FROM morn\n" +
                "           GROUP BY (ROUND(departure_location [ 0]::numeric, 4), ROUND(departure_location [ 1]::numeric, 4))\n" +
                "           ORDER BY count (*) DESC\n" +
                "           LIMIT 3) as mornst\n" +
                "JOIN (SELECT point(ROUND(arrival_location [0]::numeric, 4), ROUND(arrival_location [ 1]::numeric, 4)) as morning_drop,\n" +
                "             count(*),\n" +
                "             ROW_NUMBER() over (ORDER BY count(*) DESC) as top\n" +
                "      FROM morn\n" +
                "           GROUP BY (ROUND(arrival_location [ 0]::numeric, 4), ROUND(arrival_location [ 1]::numeric, 4))\n" +
                "           ORDER BY count (*) DESC\n" +
                "           LIMIT 3) as mornfi USING(top)\n" +
                "\n" +
                "JOIN (SELECT point(ROUND(departure_location [0]::numeric, 4), ROUND(departure_location [ 1]::numeric, 4)) as afternoon_pickup,\n" +
                "             count(*),\n" +
                "             ROW_NUMBER() over (ORDER BY count(*) DESC) as top\n" +
                "      FROM noon\n" +
                "           GROUP BY (ROUND(departure_location [ 0]::numeric, 4), ROUND(departure_location [ 1]::numeric, 4))\n" +
                "           ORDER BY count (*) DESC\n" +
                "           LIMIT 3) as noonst USING(top)\n" +
                "JOIN (SELECT point(ROUND(arrival_location [0]::numeric, 4), ROUND(arrival_location [ 1]::numeric, 4)) as afternoon_drop,\n" +
                "             count(*),\n" +
                "             ROW_NUMBER() over (ORDER BY count(*) DESC) as top\n" +
                "      FROM noon\n" +
                "           GROUP BY (ROUND(arrival_location [ 0]::numeric, 4), ROUND(arrival_location [ 1]::numeric, 4))\n" +
                "           ORDER BY count (*) DESC\n" +
                "           LIMIT 3) as noonfi USING(top)\n" +
                "\n" +
                "JOIN (SELECT point(ROUND(departure_location [0]::numeric, 4), ROUND(departure_location [ 1]::numeric, 4)) as evening_pickup,\n" +
                "             count(*),\n" +
                "             ROW_NUMBER() over (ORDER BY count(*) DESC) as top\n" +
                "      FROM even\n" +
                "           GROUP BY (ROUND(departure_location [ 0]::numeric, 4), ROUND(departure_location [ 1]::numeric, 4))\n" +
                "           ORDER BY count (*) DESC\n" +
                "           LIMIT 3) as evenst USING(top)\n" +
                "JOIN (SELECT point(ROUND(arrival_location [0]::numeric, 4), ROUND(arrival_location [ 1]::numeric, 4)) as evening_drop,\n" +
                "             count(*),\n" +
                "             ROW_NUMBER() over (ORDER BY count(*) DESC) as top\n" +
                "      FROM even\n" +
                "           GROUP BY (ROUND(arrival_location [ 0]::numeric, 4), ROUND(arrival_location [ 1]::numeric, 4))\n" +
                "           ORDER BY count (*) DESC\n" +
                "           LIMIT 3) as evenfi USING(top);\n";
        return executePrintReturn(sql);
    }

    @Override
    public ResultSet query7() throws SQLException {
        String sql = "SELECT license_plate, count(*) " +
                "FROM taxi_service.ride " +
                "WHERE age(departure_time) < '3 months' " +
                "GROUP BY (license_plate) " +
                "ORDER BY count(*) " +
                "LIMIT (SELECT count(*) FROM taxi_service.car) / 10;";
        return executePrintReturn(sql);
    }

    @Override
    public ResultSet query8(LocalDate date) throws SQLException {
        String sql = "SELECT username, count(*) FROM (SELECT DISTINCT username, ride.license_plate, start_time " +
                "FROM taxi_service.ride " +
                "  JOIN taxi_service.charge ON ride.license_plate = charge.license_plate AND departure_time::date = start_time::date " +
                "WHERE age(ride.departure_time, '" + date.toString() + "') BETWEEN '0 days' AND '30 days') as tab " +
                "GROUP BY username;";
        return executePrintReturn(sql);
    }

    @Override
    public ResultSet query9() throws SQLException {
        String sql = "SELECT workshop_id, part_id, average_quantity " +
                "FROM (SELECT ROW_NUMBER() over (partition by repairment.workshop_id ORDER BY sum(quantity) DESC) as rating, " +
                "             repairment.workshop_id, " +
                "             part_id, " +
                "             sum(quantity) / 52 as average_quantity " +
                "      FROM taxi_service.used_parts " +
                "             JOIN taxi_service.repairment ON used_parts.repairment_id = repairment.id " +
                "      WHERE age(start_time) < '1 year' " +
                "      GROUP BY (repairment.workshop_id, part_id)) t " +
                "WHERE rating = 1; ";
        return executePrintReturn(sql);
    }

    @Override
    public ResultSet query10() throws SQLException {
        String sql = "SELECT name as \"Car model\", sum(price) / 365 / count(DISTINCT license_plate) as \"Average maintance cost during last year\" " +
                "FROM taxi_service.car_model " +
                "       JOIN taxi_service.car ON car_model.id = car.model " +
                "       JOIN " +
                "       (SELECT license_plate, price " +
                "        FROM taxi_service.charge " +
                "        WHERE age(start_time) < '1 year' " +
                "        UNION " +
                "        SELECT license_plate, price " +
                "        FROM taxi_service.repairment " +
                "        WHERE age(start_time) < '1 year') as tbl " +
                "       USING (license_plate) " +
                "GROUP BY id;";
        return executePrintReturn(sql);
    }
}