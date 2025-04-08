package org.example.lr2programrs;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DistributedTransaction {
    private final Connection conn1;
    private final Connection conn2;

    public DistributedTransaction(Connection conn1, Connection conn2) {
        this.conn1 = conn1;
        this.conn2 = conn2;
    }
    public void execute(String query1, String query2) throws SQLException {
        Statement stmt1 = null;
        Statement stmt2 = null;

        try {
            conn1.setAutoCommit(false);
            conn2.setAutoCommit(false);

            stmt1 = conn1.createStatement();
            stmt2 = conn2.createStatement();

            stmt1.executeUpdate(query1);
            stmt2.executeUpdate(query2);

            conn1.commit();
            conn2.commit();

            System.out.println("Транзакція успішно виконана в обох БД.");

        } catch (SQLException e) {
            if (conn1 != null) conn1.rollback();
            if (conn2 != null) conn2.rollback();
            System.out.println("Помилка! Транзакція відкотилася.");
            throw new SQLException("Помилка розподіленої транзакції: " + e.getMessage(), e);
        } finally {
            if (stmt1 != null) stmt1.close();
            if (stmt2 != null) stmt2.close();
            conn1.setAutoCommit(true);
            conn2.setAutoCommit(true);
        }
    }
}