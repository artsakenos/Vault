package tk.artsakenos.vault.libraries;

import lombok.Getter;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"UnusedReturnValue", "unused"})
@Getter
public class SQLiteWrapper {
    private final Connection connection;

    public static class QueryResult implements AutoCloseable {
        private final PreparedStatement statement;
        @Getter
        private final ResultSet resultSet;

        public QueryResult(PreparedStatement statement, ResultSet resultSet) {
            this.statement = statement;
            this.resultSet = resultSet;
        }

        @Override
        public void close() throws SQLException {
            resultSet.close();
            statement.close();
        }
    }

    // Constructor to initialize the database connection
    public SQLiteWrapper(String fileName) throws SQLException {
        String url = "jdbc:sqlite:" + fileName;
        this.connection = DriverManager.getConnection(url);
        try (Statement stmt = connection.createStatement()) {
            // SQLite's WAL mode allows multiple readers to coexist with a single writer,
            // reducing the likelihood of "database is locked" errors.
            stmt.execute("PRAGMA journal_mode=WAL;");
        }
    }


    /**
     * try (QueryResult queryResult = sqliteWrapper.queryRS("SELECT * FROM my_table")) {
     * ResultSet resultSet = queryResult.getResultSet();
     * while (resultSet.next()) {
     * // Process the result set
     * }
     * } // Both ResultSet and PreparedStatement will be closed automatically
     *
     * @param sql    The SQL Query
     * @param params The Params
     * @return a QueryResult
     * @throws SQLException The SQL Exception
     */
    public QueryResult queryRS(String sql, Object... params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        setParameters(statement, params);
        ResultSet resultSet = statement.executeQuery();
        return new QueryResult(statement, resultSet);
    }

    /**
     * List<Map<String, Object>> results = sqliteWrapper.query("SELECT * FROM my_table");
     * for (Map<String, Object> row : results) {
     * // Process each row
     * }
     *
     * @param sql    The SQL Query
     * @param params The Params
     * @return a QueryResult
     * @throws SQLException The SQL Exception
     */
    public List<Map<String, Object>> query(String sql, Object... params) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = resultSet.getObject(i);
                        row.put(columnName, value);
                    }
                    results.add(row);
                }
            }
        }
        return results;
    }

    // Method to execute an UPDATE, INSERT, or DELETE query
    public int update(String sql, Object... params) throws SQLException {
        connection.setAutoCommit(false); // Start transaction
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, params);
            int result = statement.executeUpdate();
            connection.commit(); // Commit transaction
            return result;
        } catch (SQLException e) {
            connection.rollback(); // Rollback on error
            throw e;
        } finally {
            connection.setAutoCommit(true); // Reset auto-commit
        }
    }

    // Method to check if a table exists in the database
    public boolean tableExists(String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet tables = meta.getTables(null, null, tableName, null);
        return tables.next();
    }

    // Helper method to set parameters in a PreparedStatement
    private void setParameters(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }

    // Close the database connection
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}