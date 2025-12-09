package project_asteria.Services.Calc;

import project_asteria.Model.PriceCandle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface EmaCalc {
    public double calculateEma(List<PriceCandle> candles, int period) throws IOException, SQLException;
}
