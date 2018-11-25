package main;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
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
        System.out.println("Enter the number of query:");
        Scanner queryScanner = new Scanner(System.in);
        int queryNumber = queryScanner.nextInt();
        switch (queryNumber) {
            case 1:
                queries.query1("shoot");
                break;
            case 2:
                queries.query2(LocalDate.of(2018, 11, 22));
                break;
            case 3:
                queries.query3();
                break;
            case 4:
                queries.query4("shoot");
                break;
            case 5:
                queries.query5(LocalDate.of(2018, 11, 22));
                break;
            case 7:
                queries.query7();
                break;
            case 8:
                queries.query8(LocalDate.of(2018, 10, 24));
                break;
            default:
                System.out.println("Next time enter number from 1 to 10.");
        }
        c.close();
    }

}
