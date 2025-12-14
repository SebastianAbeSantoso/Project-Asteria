package asteria.services.calculators.momentum;

import asteria.model.PriceCandle;
import asteria.model.StochasticResult;

import java.util.List;

public interface StochasticCalculator {
    List<StochasticResult> getStochastic(List<PriceCandle> candles, int kPeriod, int dPeriod);
    List<StochasticResult> getStandardStochastic(List<PriceCandle> candles);
    }
