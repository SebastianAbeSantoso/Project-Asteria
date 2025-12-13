package project_asteria.Services.Calculators;

import project_asteria.Model.PriceCandle;

import java.io.IOException;
import java.util.List;

public interface ISmaCalculator {
    double calculateSma(List<PriceCandle> candles, int period) throws IOException;
}
