package project_asteria.Services.Calc;

import project_asteria.Model.PriceCandle;

import java.util.List;

public interface AtrCalc {
    List<Double> calculateATR(List<PriceCandle> candles, int period);
}
