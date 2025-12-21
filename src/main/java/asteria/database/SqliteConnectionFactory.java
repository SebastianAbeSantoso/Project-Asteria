package asteria.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteConnectionFactory implements ConnectionFactory {
    private static final String DB_FILE_PATH = "data/asteria.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE_PATH;

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

}
