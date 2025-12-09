package project_asteria.Services.Calc;

import project_asteria.Model.PriceCandle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface EmaCalcValue {
    public List<Double> calculateEmaValue(List<Double> values, int period);
}
