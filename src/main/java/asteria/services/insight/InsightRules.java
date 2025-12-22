package asteria.services.insight;

import java.io.IOException;
import java.sql.SQLException;

public interface InsightRules {
    String getOverallInsight(String symbol) throws SQLException, IOException;
    InsightRulesImpl.Volatility getVolatility(String symbol) throws SQLException;
    InsightRulesImpl.Momentum getMomentum(String symbol) throws SQLException, IOException;
    InsightRulesImpl.Trend getTrend(String symbol) throws SQLException, IOException;
}

