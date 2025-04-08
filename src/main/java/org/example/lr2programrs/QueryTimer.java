package org.example.lr2programrs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//public class QueryTimer {
//    public static long measureQueryTime(String dbType, String query, String host, String dbName, String user, String password) {
//        long startTime = System.currentTimeMillis();
//        try (Connection conn = new DatabaseConnection(dbType, host, dbName, user, password).connect()) {
//            Statement stmt = conn.createStatement();
//            stmt.executeUpdate(query);
//        } catch (SQLException e) {
//            System.out.println("Database error: " + e.getMessage());
//            return -1;
//        }
//        long endTime = System.currentTimeMillis();
//        return endTime - startTime;
//    }
//}

