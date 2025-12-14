package asteria.services.calculators.trend;

import asteria.model.PriceCandle;

import java.io.IOException;
import java.util.List;

public interface SmaCalculator {
    double calculateSma(List<PriceCandle> candles, int period) throws IOException;
}
