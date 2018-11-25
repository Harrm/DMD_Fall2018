package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


class DataGenerator {

    public static void main(String[] args) {
        Connection c = null;
        try {
            String password = null;
            try {
                Scanner passwordScanner = new Scanner(new File("password.txt"));
                password = passwordScanner.nextLine();
                if (password == null) throw new Exception("Add password.txt file with password");
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
            c = DriverManager
                    .getConnection("jdbc:postgresql://elmer.db.elephantsql.com:5432/lherrbcv",
                            "lherrbcv", password);
            DataGenerator dg = new DataGenerator(c);
            dg.generateTestData();

            c.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    DataGenerator(Connection c) throws SQLException, FileNotFoundException {
        connection = c;
        insertEntityStatement = new HashMap<>();
        insertEntityStatement.put("car", connection.prepareStatement(
                "INSERT INTO taxi_service.car " +
                        "(license_plate, location, color, model) " +
                        "VALUES (?, point(?, ?), ?, ?)"));
        insertEntityStatement.put("customer", connection.prepareStatement(
                "INSERT INTO taxi_service.customer " +
                        "(full_name, username, location, email, phone) " +
                        "VALUES (?, ?, ?, ?, ?)"));
        insertEntityStatement.put("ride", connection.prepareStatement(
                "INSERT INTO taxi_service.ride " +
                        "(license_plate, username, price, order_time, departure_time, arrival_time, departure_location, arrival_location, distance, payment_id)" +
                        "VALUES (?, ?, ?, ?, ?, ?, point(?, ?), point(?, ?), ?, ?)"));
        insertEntityStatement.put("charging_station", connection.prepareStatement(
                "INSERT INTO taxi_service.charging_station " +
                        "(gps_location, price) " +
                        "VALUES (point(?, ?), ?)"));
        insertEntityStatement.put("plug_type", connection.prepareStatement(
                "INSERT INTO taxi_service.plug_type " +
                        "(name) " +
                        "VALUES (?)"));
        insertEntityStatement.put("socket", connection.prepareStatement(
                "INSERT INTO taxi_service.socket " +
                        "(plug_id, station_id) " +
                        "VALUES (?, ?)"));
        insertEntityStatement.put("car_model", connection.prepareStatement(
                "INSERT INTO taxi_service.car_model" +
                        "(name, plug) " +
                        "VALUES (?, ?)"));
        insertEntityStatement.put("charge", connection.prepareStatement(
                "INSERT INTO taxi_service.charge" +
                        "(license_plate, socket_id, start_time, finish_time, charged_energy, price) " +
                        "VALUES (?, ?, ?, ?, ?, ?)"));
        insertEntityStatement.put("workshop", connection.prepareStatement(
                "INSERT INTO taxi_service.workshop" +
                        "(gps_location, working_hours) " +
                        "VALUES (point(?, ?), timerange(?, ?))"));
        insertEntityStatement.put("repairment", connection.prepareStatement(
                "INSERT INTO taxi_service.repairment" +
                        "(license_plate, workshop_id, start_time, end_time, description, price) " +
                        "VALUES (?, ?, ?, ?, ?, ?)"));
        insertEntityStatement.put("part", connection.prepareStatement(
                "INSERT INTO taxi_service.part" +
                        "(name) " +
                        "VALUES (?)"));
        insertEntityStatement.put("used_part", connection.prepareStatement(
                "INSERT INTO taxi_service.used_parts" +
                        "(repairment_id, part_id, quantity) " +
                        "VALUES (?, ?, ?)"));
        rand = new Random();
        updateNameList();
        updateCitiesList();
        updateMakesList();
    }

    void generateTestData() throws SQLException {
        updateModelList();
        updateLicensePlateList();
        updateUsernameList();
        updateStationIdList();
        updateSocketList();
        updatePlugIdList();
        updateWorkshopIdList();
        updateRepairIdList();
        updatePartIdList();

        for(int i = 0; i < 15; i++) {
            Point p = new Point();
            p.x = rand.nextDouble() * rand.nextInt(100);
            p.y = rand.nextDouble() * rand.nextInt(100);
            locations.add(p);
        }

        for (int i = 0; i < 10000; i++) {
            try{
                generateUsedPart();
            } catch(SQLException e) {

            }
        }
    }

    void generateCar() throws SQLException {
        String licensePlate = "" +
                nextChar() +
                nextChar() +
                rand.nextInt(10) +
                rand.nextInt(10) +
                rand.nextInt(10) +
                nextChar();
        double locationX = rand.nextDouble() * rand.nextInt(100);
        double locationY = rand.nextDouble() * rand.nextInt(100);
        String color = colors[rand.nextInt(colors.length)];
        int model = models.get(rand.nextInt(models.size()));

        PreparedStatement s = insertEntityStatement.get("car");
        s.setString(1, licensePlate);
        s.setDouble(2, locationX);
        s.setDouble(3, locationY);
        s.setString(4, color);
        s.setInt(5, model);
        s.execute();
    }

    void generateCustomer() throws SQLException {
        String fullName = names.get(rand.nextInt(names.size()));
        String username = fullName.replaceAll(
                "["+fullName.charAt(rand.nextInt(fullName.length()))+"]",
                       String.valueOf(rand.nextInt(100)));
        String location = cities.get(rand.nextInt(cities.size())) + ", house "+ rand.nextInt(126);
        String email = username + "@mail.ur";
        String phone = "+7"+rand.nextInt(1000000000);
        PreparedStatement s = insertEntityStatement.get("customer");
        s.setString(1, fullName);
        s.setString(2, username);
        s.setString(3, location);
        s.setString(4, email);
        s.setString(5, phone);
        s.execute();
    }



    void generateRide() throws SQLException {
        String license_plate = licensePlates.get(rand.nextInt(licensePlates.size()));
        String username = usernames.get(rand.nextInt(usernames.size()));
        int price = rand.nextInt(10000);
        Timestamp order_time = nextTimestamp();
        Timestamp departure_time = Timestamp.from(order_time.toInstant().plus(rand.nextInt(30), ChronoUnit.MINUTES));
        Timestamp arrival_time = Timestamp.from(departure_time.toInstant().plus(rand.nextInt(24*60), ChronoUnit.MINUTES));
        int i = rand.nextInt(locations.size());
        Point departure_location = locations.get(i);
        int i1 = rand.nextInt(locations.size());
        while(i1 == i) {
            i1 = rand.nextInt(locations.size());
        }
        Point arrival_location = locations.get(i1);
        double distance = rand.nextDouble() * rand.nextInt(10000);
        int receipt_id = rand.nextInt(100000);

        PreparedStatement s = insertEntityStatement.get("ride");
        s.setString(1, license_plate);
        s.setString(2, username);
        s.setInt(3, price);
        s.setTimestamp(4, order_time);
        s.setTimestamp(5, departure_time);
        s.setTimestamp(6, arrival_time);
        s.setDouble(7, departure_location.x);
        s.setDouble(8, departure_location.y);
        s.setDouble(9, arrival_location.x);
        s.setDouble(10, arrival_location.y);
        s.setDouble(11, distance);
        s.setInt(12, receipt_id);

        s.execute();
    }


    void generateStation() throws SQLException {
        double gps_location_x = rand.nextDouble() * rand.nextInt(100);
        double gps_location_y = rand.nextDouble() * rand.nextInt(100);
        int price = 5 + rand.nextInt(96);

        PreparedStatement s = insertEntityStatement.get("charging_station");
        s.setDouble(1, gps_location_x);
        s.setDouble(2, gps_location_y);
        s.setInt(3, price);
        s.execute();
    }

    void generateWorkshop() throws SQLException {
        double gps_location_x = rand.nextDouble() * rand.nextInt(100);
        double gps_location_y = rand.nextDouble() * rand.nextInt(100);
        int working_hours_start = 5 + rand.nextInt(12);
        int working_hours_end = Math.min(24, working_hours_start + 6 + rand.nextInt(10));

        PreparedStatement s = insertEntityStatement.get("workshop");
        s.setDouble(1, gps_location_x);
        s.setDouble(2, gps_location_y);
        s.setTime(3, Time.valueOf(working_hours_start+":00:00"));
        s.setTime(4, Time.valueOf((working_hours_end == 24) ? "23:59:00" : working_hours_end+":00:00"));
        s.execute();
    }

    void generatePlug() throws SQLException {
        String name = "" + nextChar() + nextChar() + rand.nextInt(1000) + nextChar();
        PreparedStatement s = insertEntityStatement.get("plug_type");
        s.setString(1, name);
        s.execute();
    }

    void generateSocket() throws SQLException {
        int plug_id = plug_ids.get(rand.nextInt(plug_ids.size()));
        int station_id = station_ids.get(rand.nextInt(station_ids.size()));
        PreparedStatement s = insertEntityStatement.get("socket");
        s.setInt(1, plug_id);
        s.setInt(2, station_id);
        s.execute();
    }

    void generateModel() throws SQLException {
        String name = makes.get(rand.nextInt(makes.size())) + " " + nextChar() + nextChar() + rand.nextInt(1000);
        int plug = plug_ids.get(rand.nextInt(plug_ids.size()));

        PreparedStatement s = insertEntityStatement.get("car_model");
        s.setString(1, name);
        s.setInt(2, plug);
        s.execute();
    }


    void generateRepairment() throws SQLException {
        String license_plate = licensePlates.get(rand.nextInt(licensePlates.size()));
        int workshop_id = workshops.get(rand.nextInt(workshops.size()));
        Timestamp start_time = nextTimestamp();
        Timestamp end_time = Timestamp.from(start_time.toInstant().plus(rand.nextInt(12*60), ChronoUnit.MINUTES));
        String description = "";
        int price = rand.nextInt(1000);

        PreparedStatement s = insertEntityStatement.get("repairment");
        s.setString(1, license_plate);
        s.setInt(2, workshop_id);
        s.setTimestamp(3, start_time);
        s.setTimestamp(4, end_time);
        s.setString(5, description);
        s.setInt(6, price);
        s.execute();
    }

    void generatePart() throws SQLException {
        String name = ""+nextChar()+nextChar()+nextChar()+rand.nextInt(1000);

        PreparedStatement s = insertEntityStatement.get("part");
        s.setString(1, name);
        s.execute();
    }

    void generateUsedPart() throws SQLException {
        int repairment_id = repairs.get(rand.nextInt(repairs.size()));
        int part_id = part_ids.get(rand.nextInt(part_ids.size()));
        int quantity = rand.nextInt(100);

        PreparedStatement s = insertEntityStatement.get("used_part");
        s.setInt(1, repairment_id);
        s.setInt(2, part_id);
        s.setInt(3, quantity);
        s.execute();
    }

    void generateCharge() throws SQLException {
        String car = licensePlates.get(rand.nextInt(licensePlates.size()));
        int socket_id = sockets.get(rand.nextInt(sockets.size()));
        ResultSet rs = connection.createStatement().executeQuery(
        "SELECT price FROM taxi_service.charging_station, taxi_service.socket WHERE charging_station.id = socket.station_id and socket.id = "+socket_id);
        rs.next();
        int station_price = rs.getInt("price");
        Timestamp start_time = nextTimestamp();
        Timestamp finish_time = Timestamp.from(start_time.toInstant().plus(rand.nextInt(3*60), ChronoUnit.MINUTES));
        double charged_energy = rand.nextDouble() * rand.nextInt(100);

        PreparedStatement s = insertEntityStatement.get("charge");
        s.setString(1, car);
        s.setInt(2, socket_id);
        s.setTimestamp(3, start_time);
        s.setTimestamp(4, finish_time);
        s.setDouble(5, charged_energy);
        s.setDouble(6, charged_energy * station_price);
        s.execute();
    }

    private char nextChar() {
        return (char)(rand.nextInt('Z'-'A') + (int)'A');
    }

    private Timestamp nextTimestamp() {
        int year = 2018;
        int month = 1 + rand.nextInt(12);
        int day = 1 + rand.nextInt(28);
        int hour = rand.nextInt(24);
        int minute = rand.nextInt(60);
        LocalDateTime datetime = LocalDateTime.of(year, month, day, hour, minute);
        return Timestamp.valueOf(datetime);
    }

    private void updateModelList() throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery("SELECT id FROM taxi_service.car_model");
        while(rs.next()) {
            models.add(rs.getInt("id"));
        }
        s.close();
    }

    private void updateLicensePlateList() throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery("SELECT license_plate FROM taxi_service.car");
        while(rs.next()) {
            licensePlates.add(rs.getString("license_plate"));
        }
        s.close();
    }

    private void updateUsernameList() throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery("SELECT username FROM taxi_service.customer");
        while(rs.next()) {
            usernames.add(rs.getString("username"));
        }
        s.close();
    }

    private void updateSocketList() throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery("SELECT id FROM taxi_service.socket");
        while(rs.next()) {
            sockets.add(rs.getInt("id"));
        }
        s.close();
    }

    private void updatePlugIdList() throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery("SELECT id FROM taxi_service.plug_type");
        while(rs.next()) {
            plug_ids.add(rs.getInt("id"));
        }
        s.close();
    }

    private void updateRepairIdList() throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery("SELECT id FROM taxi_service.repairment");
        while(rs.next()) {
            repairs.add(rs.getInt("id"));
        }
        s.close();
    }

    private void updatePartIdList() throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery("SELECT id FROM taxi_service.part");
        while(rs.next()) {
            part_ids.add(rs.getInt("id"));
        }
        s.close();
    }

    private void updateStationIdList() throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery("SELECT id FROM taxi_service.charging_station");
        while(rs.next()) {
            station_ids.add(rs.getInt("id"));
        }
        s.close();
    }

    private void updateWorkshopIdList() throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery("SELECT id FROM taxi_service.workshop");
        while(rs.next()) {
            workshops.add(rs.getInt("id"));
        }
        s.close();
    }

    private void updateNameList() throws FileNotFoundException {
        Scanner s = new Scanner(new File("names.txt"));
        while(s.hasNext()) {
            names.add(s.next());
        }
    }

    private void updateMakesList() throws FileNotFoundException {
        Scanner s = new Scanner(new File("makes.txt"));
        while(s.hasNextLine()) {
            makes.add(s.nextLine());
        }
    }

    private void updateCitiesList() throws FileNotFoundException {
        Scanner s = new Scanner(new File("cities.txt"));
        while(s.hasNextLine()) {
            cities.add(s.nextLine());
        }
    }

    static class Point {
        double x, y;
    }

    private Connection connection;
    private HashMap<String, PreparedStatement> insertEntityStatement;
    private Random rand;
    private ArrayList<String> licensePlates = new ArrayList<>();
    private ArrayList<Integer> sockets = new ArrayList<>();
    private ArrayList<Integer> part_ids = new ArrayList<>();
    private ArrayList<String> usernames = new ArrayList<>();
    private ArrayList<Integer> repairs = new ArrayList<>();
    private ArrayList<Integer> workshops = new ArrayList<>();
    private ArrayList<Integer> models = new ArrayList<>();
    private ArrayList<Integer> plug_ids = new ArrayList<>();
    private ArrayList<Integer> station_ids = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> cities = new ArrayList<>();
    private ArrayList<String> makes = new ArrayList<>();
    private ArrayList<Point> locations = new ArrayList<>();
    private String[] colors = new String[] {"red", "white", "black", "green", "yellow"};
}
