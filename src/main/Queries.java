package main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public interface Queries {
    ResultSet query1(String username) throws SQLException;
    ResultSet query2(LocalDate date) throws SQLException;
    ResultSet query3() throws SQLException;
//    public ResultSet query4(String username) throws SQLException;
    ResultSet query5(LocalDate date) throws SQLException;
//    public ResultSet query6() throws SQLException;
//    public ResultSet query7() throws SQLException;
    public ResultSet query8(LocalDate date) throws SQLException;
//    public ResultSet query9() throws SQLException;
//    public ResultSet query10() throws SQLException;
}
