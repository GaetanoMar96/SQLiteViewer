package viewer;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBConn implements AutoCloseable {

    final static String path = "jdbc:sqlite:%s";
    private static final String firstDatabaseFileName = "firstDatabase.db";
    private static final String secondDatabaseFileName = "secondDatabase.db";

    private static final String create_Contacts = "CREATE TABLE IF NOT EXISTS contacts (" +
            "contact_id INTEGER PRIMARY KEY," +
            "first_name TEXT NOT NULL," +
            "last_name TEXT NOT NULL," +
            "email TEXT NOT NULL UNIQUE," +
            "phone TEXT NOT NULL UNIQUE" +
            ");";

    private static final String create_Groups = "CREATE TABLE IF NOT EXISTS groups (" +
            "   group_id INTEGER PRIMARY KEY," +
            "   name TEXT NOT NULL" +
            ");";

    private static final String create_Projects = "CREATE TABLE IF NOT EXISTS projects (" +
            "id integer PRIMARY KEY," +
            "name text NOT NULL," +
            "begin_date text,\n" +
            "end_date text" +
            ");";

    private static final String get_public_tables = "SELECT name FROM sqlite_master " +
            "WHERE type ='table' AND name NOT LIKE 'sqlite_%';";

    public static final String ALL_ROWS_QUERY = "SELECT * FROM %s;";

    private Connection connection;

    public JDBConn(String fileName) throws SQLException {
        connect(fileName);
    }

    private void connect(String fileName) throws SQLException {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(String.format(path, fileName));
        connection = dataSource.getConnection();
    }

    public List<String> getTables() throws SQLException {
        List<String> list = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(get_public_tables);
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                list.add(name);
            }
            return list;
        }
    }

    public TableModelClass getData(String query) throws SQLException {
        Map<Integer, Object[]> collection = new HashMap<>();
        try (Statement statement = connection.createStatement()) {
            connection.setAutoCommit(true);
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData result_metadata = resultSet.getMetaData();
            int columnCount = result_metadata.getColumnCount();
            String[] columns = new String[columnCount];
            for (int i = 0; i < result_metadata.getColumnCount(); i++) {
                columns[i] = result_metadata.getColumnName(i + 1);
            }
            int i = 0;
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int j = 0; j < columnCount; j++) {
                    row[j] = resultSet.getObject(j + 1);
                }
                collection.put(i++, row);
            }
            return new TableModelClass(columns, collection);
        }
    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }
}
