package asteria.services.calculators.volatility;

import asteria.model.PriceCandle;

import java.util.List;

public interface AtrCalculator {
    List<Double> calculateAtr(List<PriceCandle> candles, int period);
}
