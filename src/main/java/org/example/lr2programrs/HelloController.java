package org.example.lr2programrs;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.sql.*;

public class HelloController {
    @FXML private ComboBox<String> dbTypeComboBox;
    @FXML private TextArea queryTextArea;
    @FXML private TextField hostField, dbNameField, userField;
    @FXML private PasswordField passwordField;
    @FXML private TableView<ObservableList<String>> resultTable;
    @FXML private Label timeLabel;
    @FXML private Button executeButton;

    @FXML private ComboBox<String> dbTypeComboBox2;
    @FXML private TextField hostField2, dbNameField2, userField2;
    @FXML private PasswordField passwordField2;
    @FXML private TextArea queryTextArea2;
    @FXML private TableView<ObservableList<String>> resultTable2;

    @FXML
    public void initialize() {
        dbTypeComboBox.getItems().addAll("PostgreSQL", "MySQL");
        dbTypeComboBox2.getItems().addAll("PostgreSQL", "MySQL");
    }

    @FXML
    public void executeQuery() {
        if (!validateInput(dbTypeComboBox, hostField, dbNameField, userField, passwordField, queryTextArea)) return;

        String dbType = dbTypeComboBox.getValue();
        String host = hostField.getText();
        String dbName = dbNameField.getText();
        String user = userField.getText();
        String password = passwordField.getText();
        String query = queryTextArea.getText();

        try {
            DatabaseConnection connection = new DatabaseConnection(dbType, host, dbName, user, password);
            try (Connection conn = connection.connect(); Statement stmt = conn.createStatement()) {
                long start = System.currentTimeMillis();
                boolean hasResultSet = stmt.execute(query);
                long end = System.currentTimeMillis();

                resultTable.getItems().clear();
                resultTable.getColumns().clear();

                if (hasResultSet) {
                    ResultSet rs = stmt.getResultSet();
                    loadResultsToTable(rs, resultTable);
                } else {
                    showAlert("Запит виконано успішно.");
                }

                timeLabel.setText("Time: " + (end - start) + " мс");

            }
        } catch (SQLException e) {
            showAlert("SQL Error: " + e.getMessage());
        }
    }

    @FXML
    public void executeDistributedTransaction() {
        if (!validateInput(dbTypeComboBox, hostField, dbNameField, userField, passwordField, queryTextArea) ||
                !validateInput(dbTypeComboBox2, hostField2, dbNameField2, userField2, passwordField2, queryTextArea2)) {
            return;
        }

        String dbType1 = dbTypeComboBox.getValue();
        String host1 = hostField.getText();
        String dbName1 = dbNameField.getText();
        String user1 = userField.getText();
        String password1 = passwordField.getText();
        String query1 = queryTextArea.getText();

        String dbType2 = dbTypeComboBox2.getValue();
        String host2 = hostField2.getText();
        String dbName2 = dbNameField2.getText();
        String user2 = userField2.getText();
        String password2 = passwordField2.getText();
        String query2 = queryTextArea2.getText();

        try {
            DatabaseConnection conn1 = new DatabaseConnection(dbType1, host1, dbName1, user1, password1);
            DatabaseConnection conn2 = new DatabaseConnection(dbType2, host2, dbName2, user2, password2);

            try (Connection connection1 = conn1.connect(); Connection connection2 = conn2.connect()) {
                DistributedTransaction dt = new DistributedTransaction(connection1, connection2);
                dt.execute(query1, query2);

                // Показуємо лише якщо це SELECT
                if (query1.trim().toLowerCase().startsWith("select")) {
                    showQueryResults(connection1, query1, resultTable);
                }

                if (query2.trim().toLowerCase().startsWith("select")) {
                    showQueryResults(connection2, query2, resultTable2);
                }

                showAlert("Розподілену транзакцію виконано успішно.");
            }

        } catch (SQLException e) {
            showAlert("Error: " + e.getMessage());
        }
    }

    private void loadResultsToTable(ResultSet rs, TableView<ObservableList<String>> tableView) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            final int colIndex = i - 1;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(meta.getColumnName(i));
            col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(colIndex)));
            tableView.getColumns().add(col);
        }

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        while (rs.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getString(i));
            }
            data.add(row);
        }
        tableView.setItems(data);
    }

    private void showQueryResults(Connection conn, String query, TableView<ObservableList<String>> tableView) {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            tableView.getItems().clear();
            tableView.getColumns().clear();
            loadResultsToTable(rs, tableView);
        } catch (SQLException e) {
            showAlert("Помилка завантаження результатів: " + e.getMessage());
        }
    }

    private boolean validateInput(ComboBox<String> dbType, TextField host, TextField dbName,
                                  TextField user, PasswordField pass, TextArea queryArea) {
        if (dbType.getValue() == null || host.getText().isEmpty() || dbName.getText().isEmpty() ||
                user.getText().isEmpty() || pass.getText().isEmpty() || queryArea.getText().isEmpty()) {
            showAlert("Будь ласка, заповніть усі поля та виберіть тип бази даних.");
            return false;
        }
        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Повідомлення");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
