package asteria.model;

public record MarketView(
        MarketSnapshot snapshot,
        MarketInsight insight
) {}
