package main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public interface Queries {
    public ResultSet query1(String username) throws SQLException;
//    public ResultSet query2(LocalDate date);
    public ResultSet query3() throws SQLException;
//    public ResultSet query4(String username) throws SQLException;
//    public ResultSet query5(LocalDate date) throws SQLException;
//    public ResultSet query6() throws SQLException;
//    public ResultSet query7() throws SQLException;
//    public ResultSet query8() throws SQLException;
//    public ResultSet query9() throws SQLException;
//    public ResultSet query10() throws SQLException;
}
