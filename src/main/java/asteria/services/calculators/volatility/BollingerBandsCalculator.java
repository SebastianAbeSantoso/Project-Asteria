package asteria.services.calculators.volatility;

import asteria.model.BollingerBandsResult;
import asteria.model.PriceCandle;

import java.util.List;

public interface BollingerBandsCalculator {
    BollingerBandsResult calculateBollingerBands (List<PriceCandle> candles, int period, double numStdDev);
    BollingerBandsResult calculateStandardBollingerBands (List<PriceCandle> candles);
}
