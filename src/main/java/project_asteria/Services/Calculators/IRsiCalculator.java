package project_asteria.Services.Calculators;

import project_asteria.Model.PriceCandle;

import java.io.IOException;
import java.util.List;

public interface IRsiCalculator {
    double calculateRsi(List<PriceCandle> candles, int period) throws IOException;
}
