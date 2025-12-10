package project_asteria.Services.Calc;

import project_asteria.Model.BollingerBandsResult;
import project_asteria.Model.PriceCandle;

import java.util.List;

public interface BollingerBandsCalc {
    BollingerBandsResult calculateBollingerBands (List<PriceCandle> candles, int period, double numStdDev);
    BollingerBandsResult calculateStandardBollingerBands (List<PriceCandle> candles);
}
