package main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public interface Queries {
    public ResultSet query1(String username) throws SQLException;
//    public ResultSet query2(LocalDate date);
    public ResultSet query3() throws SQLException;
}
