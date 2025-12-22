package asteria.model;

import asteria.services.insight.InsightRulesImpl;

public record MarketInsight(
        InsightRulesImpl.Trend trend,
        InsightRulesImpl.Momentum momentum,
        InsightRulesImpl.Volatility volatility
) {}