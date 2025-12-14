package asteria.services.calculators.trend;

import asteria.model.PriceCandle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface EmaCalculator {
    double calculateEma(List<PriceCandle> candles, int period) throws IOException, SQLException;
    List<Double> calculateEmaValue(List<Double> values, int period);

}
