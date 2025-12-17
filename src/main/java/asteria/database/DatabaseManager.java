package asteria.database;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.sql.*;

public class DatabaseManager {
    private final IConnectionFactory dbFactory;

    public DatabaseManager(IConnectionFactory dbFactory) {
        this.dbFactory = dbFactory;
    }


    private void validateDataFolder() throws Exception {
        Path dataDir = Paths.get("data");
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
    }

    public void initializeDatabase() throws Exception {
        validateDataFolder();

        try (Connection conn = dbFactory.getConnection()) {
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
                    adjustedClose REAL,
                    volume REAL,
                    UNIQUE(symbol, date)
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
