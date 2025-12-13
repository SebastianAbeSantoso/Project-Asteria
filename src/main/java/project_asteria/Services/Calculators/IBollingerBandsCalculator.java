package project_asteria.Services.Calculators;

import project_asteria.Model.BollingerBandsResult;
import project_asteria.Model.PriceCandle;

import java.util.List;

public interface IBollingerBandsCalculator {
    BollingerBandsResult calculateBollingerBands (List<PriceCandle> candles, int period, double numStdDev);
    BollingerBandsResult calculateStandardBollingerBands (List<PriceCandle> candles);
}
