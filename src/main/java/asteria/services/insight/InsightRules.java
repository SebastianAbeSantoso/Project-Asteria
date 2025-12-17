package asteria.services.insight;

import java.io.IOException;
import java.sql.SQLException;

public interface InsightRules {
    String getOverallInsight(String symbol) throws SQLException, IOException;}
