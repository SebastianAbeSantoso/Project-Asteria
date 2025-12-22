package asteria.repository;

import asteria.database.ConnectionFactory;
import asteria.model.PriceCandle;
import asteria.model.WatchlistItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PriceHistoryRepository implements SqliteLoadCandles {
    private final ConnectionFactory db;

    public PriceHistoryRepository(ConnectionFactory db) {
        this.db = db;
    }

    public List<PriceCandle> loadCandles(String symbol) throws SQLException {
        String sql = """
                SELECT date, open, high, low, close, adjustedClose, volume
                FROM price_history
                WHERE symbol = ?
                ORDER BY date
                """;

        List<PriceCandle> result = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, symbol);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate date = LocalDate.parse(rs.getString("date"));
                    double open = rs.getDouble("open");
                    double high = rs.getDouble("high");
                    double low = rs.getDouble("low");
                    double close = rs.getDouble("close");
                    double adjustedClose = rs.getDouble("adjustedClose");
                    double volume = rs.getDouble("volume");

                    result.add(new PriceCandle(date, open, high, low, close, adjustedClose, volume));
                }
            }
        }
        return result;
    }

    public void upsertData(String symbol, List<PriceCandle> price_history) throws SQLException {
        String upsertSQL = """
        INSERT INTO price_history
            (symbol, date, open, high, low, close, adjustedClose, volume)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (symbol, date)
        DO UPDATE SET
            open = EXCLUDED.open,
            high = EXCLUDED.high, 
            low = EXCLUDED.low, 
            close = EXCLUDED.close, 
            adjustedClose = EXCLUDED.adjustedClose,
            volume = EXCLUDED.volume
        """;

        try (Connection conn = db.getConnection();) {
            conn.setAutoCommit(false);

            try (PreparedStatement upsertStmt = conn.prepareStatement(upsertSQL)) {

                upsertStmt.setString(1, symbol);

                for (PriceCandle candle : price_history) {
                    upsertStmt.setString(1, symbol);
                    upsertStmt.setString(2, candle.getDate().toString());
                    upsertStmt.setDouble(3, candle.getOpen());
                    upsertStmt.setDouble(4, candle.getHigh());
                    upsertStmt.setDouble(5, candle.getLow());
                    upsertStmt.setDouble(6, candle.getClose());
                    upsertStmt.setDouble(7, candle.getAdjustedclose());
                    upsertStmt.setDouble(8, candle.getVolume());
                    upsertStmt.addBatch();
                }

                upsertStmt.executeBatch();
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
