package project_asteria.Services.Calculators;

import project_asteria.Model.PriceCandle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IEmaCalculator {
    double calculateEma(List<PriceCandle> candles, int period) throws IOException, SQLException;
    List<Double> calculateEmaValue(List<Double> values, int period);

}
