package project_asteria.Services.Calc;

import project_asteria.Model.PriceCandle;

import java.io.IOException;
import java.util.List;

public interface SmaCalc {
    double calculateSma(List<PriceCandle> candles, int period) throws IOException;
}
