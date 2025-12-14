package asteria.model;

public record MarketSnapshot(
    String symbol,
    double currentPrice,
    double rsi,
    double sma20,
    double ema20,
    double atr,
    MacdResult macd,
    BollingerBandsResult bb,
    StochasticResult stoch
) {
    @Override
    public String toString() {
        return String.format(
                """
                        Analysis for %s:
                        - Price: %.2f
                        - SMA: %.2f
                        - EMA: %.2f
                        - RSI: %.2f
                        - MACD: %.4f (Signal: %.4f, Hist: %.4f)
                        - Bollinger: Upper %.2f, Lower %.2f
                        - Stochastic: K %.2f, D %.2f
                        - ATR: %.2f
                """,
                symbol,
                currentPrice,
                sma20,
                ema20,
                rsi,
                macd.getMacd(),
                macd.getSignal(),
                macd.getHistogram(),
                bb.getUpperBand(),
                bb.getLowerBand(),
                stoch.getkValue(),
                stoch.getdValue(),
                atr
        );
    }
}
