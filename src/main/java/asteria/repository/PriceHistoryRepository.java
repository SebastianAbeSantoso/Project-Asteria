package asteria.repository;

import asteria.database.IConnectionFactory;
import asteria.model.PriceCandle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PriceHistoryRepository implements ISqliteLoadCandles {

    private final IConnectionFactory db;

    public PriceHistoryRepository(IConnectionFactory db) {
        this.db = db;
    }

    public void saveCandles(String symbol, List<PriceCandle> price_history) throws SQLException {
        String deleteSQL = "DELETE FROM price_history WHERE symbol = ?";
        String insertSQL = "INSERT INTO price_history" +
                "(symbol, date, open, high, low, close, volume)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = db.getConnection();) {
            conn.setAutoCommit(false);

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSQL);) {

                deleteStmt.setString(1, symbol);
                deleteStmt.executeUpdate();

                for (PriceCandle candle : price_history) {
                    insertStmt.setString(1, symbol);
                    insertStmt.setString(2, candle.getDate().toString());
                    insertStmt.setDouble(3, candle.getOpen());
                    insertStmt.setDouble(4, candle.getHigh());
                    insertStmt.setDouble(5, candle.getLow());
                    insertStmt.setDouble(6, candle.getClose());
                    insertStmt.setDouble(7, candle.getVolume());
                    insertStmt.addBatch();
                }

                insertStmt.executeBatch();
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<PriceCandle> loadCandles(String symbol) throws SQLException {
        String sql = """
                SELECT date, open, high, low, close, volume
                FROM price_history
                WHERE symbol = ?;
                ORDER BY date;
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
                    double volume = rs.getDouble("volume");

                    result.add(new PriceCandle(date, open, high, low, close, volume));
                }
            }
        }
        return result;
    }
}
