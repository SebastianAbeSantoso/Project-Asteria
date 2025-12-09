package project_asteria.Services.Calc;

import java.util.List;

public interface EmaCalcValue {
    public List<Double> calculateEmaValue(List<Double> values, int period);
}
