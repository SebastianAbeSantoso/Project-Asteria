package asteria.repository;

import asteria.database.ConnectionFactory;
import asteria.model.WatchlistItem;
import asteria.model.WatchlistItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WatchlistRepository {

    private final ConnectionFactory db;

    public WatchlistRepository(ConnectionFactory db) {
        this.db = db;
    }

    public List<String> loadWatchlistSymbols(int userId) throws SQLException {
        String sql = """
            SELECT symbol
            FROM watchlist
            WHERE user_id = ?
              AND is_active = 1
            ORDER BY id
            """;

        List<String> symbols = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    symbols.add(rs.getString("symbol"));
                }
            }
        }

        return symbols;
    }
    public List<WatchlistItem> loadWatchlistItems(List<String> symbols) throws SQLException {
        if (symbols == null || symbols.isEmpty()) return List.of();

        String placeholders = String.join(",", Collections.nCopies(symbols.size(), "?"));

        String sql = """
            WITH ranked AS (
                SELECT symbol, date, close,
                       ROW_NUMBER() OVER (PARTITION BY symbol ORDER BY date DESC) AS rn
                FROM price_history
                WHERE symbol IN (%s)
            ),
            latest AS (
                SELECT symbol, close AS last_close
                FROM ranked
                WHERE rn = 1
            ),
            prev AS (
                SELECT symbol, close AS prev_close
                FROM ranked
                WHERE rn = 2
            )
            SELECT l.symbol,
                   l.last_close,
                   COALESCE(p.prev_close, l.last_close) AS prev_close
            FROM latest l
            LEFT JOIN prev p ON p.symbol = l.symbol
            ORDER BY l.symbol
            """.formatted(placeholders);

        List<WatchlistItem> result = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < symbols.size(); i++) {
                stmt.setString(i + 1, symbols.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String symbol = rs.getString("symbol");
                    double lastClose = rs.getDouble("last_close");
                    double prevClose = rs.getDouble("prev_close");

                    double changePct = 0.0;
                    if (prevClose != 0.0) {
                        changePct = (lastClose - prevClose) * 100.0 / prevClose;
                    }

                    result.add(new WatchlistItem(symbol, lastClose, changePct));
                }
            }
        }

        return result;
    }

    public List<WatchlistItem> loadWatchlistItemsFromDb(int userId) throws SQLException {
        List<String> symbols = loadWatchlistSymbols(userId);
        return loadWatchlistItems(symbols);
    }

    public void addToWatchlist(int userId, String symbol, String nickname) throws SQLException {
        String sql = """
            INSERT INTO watchlist (user_id, symbol, nickname, is_active)
            VALUES (?, ?, ?, 1)
            """;

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, symbol);
            stmt.setString(3, nickname);
            stmt.executeUpdate();
        }
    }

    public void removeFromWatchlist(int userId, String symbol) throws SQLException {
        String sql = """
            UPDATE watchlist
            SET is_active = 0
            WHERE user_id = ? AND symbol = ?
            """;

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, symbol);
            stmt.executeUpdate();
        }
    }
}
