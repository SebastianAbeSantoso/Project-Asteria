package asteria.services.calculators.momentum;

import asteria.model.PriceCandle;

import java.io.IOException;
import java.util.List;

public interface RsiCalculator {
    double calculateRsi(List<PriceCandle> candles, int period) throws IOException;
}
