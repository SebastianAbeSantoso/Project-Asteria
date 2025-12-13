package project_asteria.Services.Calculators;

import project_asteria.Model.PriceCandle;

import java.util.List;

public interface IAtrCalculator {
    List<Double> calculateATR(List<PriceCandle> candles, int period);
}
