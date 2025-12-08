package project_asteria.Database;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.sql.*;

public class DatabaseManager {
    private static final String DB_FILE_PATH = "data/asteria.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE_PATH;

    private static Connection connection;

    private static void validateDataFolder() throws Exception {
        Path dataDir = Paths.get("data");
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    public static void initializeDatabase() throws Exception {
        validateDataFolder();

        try (Connection conn = getConnection()){
            Statement stmt = conn.createStatement();

            String createUsersTable = """
                    CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    display_name TEXT,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP
                    );
                    """;

            String createWatchlistTable = """
                    CREATE TABLE IF NOT EXISTS watchlist (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id  INTEGER NOT NULL,
                    symbol TEXT NOT NULL,
                    nickname TEXT,
                    is_active INTEGER NOT NULL DEFAULT 1,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                    );
                    """;

            String createPriceHistoryTable = """
                    CREATE TABLE IF NOT EXISTS price_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    symbol TEXT NOT NULL,
                    date TEXT NOT NULL,
                    open REAL,
                    high REAL,
                    low REAL,
                    close REAL,
                    volume REAL
                    );
                    """;

            String createInsightLogsTable = """
                    CREATE TABLE IF NOT EXISTS insight_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    symbol TEXT NOT NULL,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                    direction TEXT,
                    prob_up REAL,
                    prob_down REAL,
                    prob_neutral REAL,
                    summary TEXT,
                    details TEXT
                    );
                    """;

            stmt.execute(createUsersTable);
            stmt.execute(createWatchlistTable);
            stmt.execute(createPriceHistoryTable);
            stmt.execute(createInsightLogsTable);
        }
    }
}
