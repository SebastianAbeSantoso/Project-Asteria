package asteria.model;
import java.util.Locale;

public class WatchlistItem {
    private final String symbol;
    private final double lastClose;
    private final double changePct;

    public WatchlistItem(String symbol, double lastClose, double changePct) {
        this.symbol = symbol;
        this.lastClose = lastClose;
        this.changePct = changePct;
    }

    public String getSymbol() { return symbol; }
    public double getLastClose() { return lastClose; }
    public double getChangePct() { return changePct; }

    public String getFormattedPrice() {
        return String.format(Locale.US, "%.2f", lastClose);
    }

    public String getFormattedChangePct() {
        return String.format(Locale.US, "%+.1f%%", changePct);
    }

    public boolean isUp() { return changePct > 0; }
    public boolean isDown() { return changePct < 0; }
}
