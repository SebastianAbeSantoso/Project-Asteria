package asteria.services.orchestrator;

import asteria.model.MarketInsight;
import asteria.model.MarketSnapshot;
import asteria.model.MarketView;
import asteria.repository.PriceHistoryRepository;
import asteria.services.ai.MessageSender;
import asteria.services.bridge.StockCalculationSuite;
import asteria.services.dataimport.api.YahooFinanceDownloader;
import asteria.services.dataimport.csv.YahooCsvImporter;
import asteria.services.insight.InsightRules;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

public class AsteriaOrchestrator {

    private final MessageSender messageSender;
    private final YahooCsvImporter importCsv;
    private final StockCalculationSuite stockCalculationSuite;
    private final YahooFinanceDownloader yahooFinanceDownloader;
    private final InsightRules insightRules;
    private final PriceHistoryRepository pricerepo;
    private String logMessage;

    public AsteriaOrchestrator(MessageSender messageSender, YahooCsvImporter importCsv, StockCalculationSuite stockCalculationSuite, YahooFinanceDownloader yahooFinanceDownloader, InsightRules insightRules, PriceHistoryRepository pricerepo) {
        this.messageSender = messageSender;
        this.importCsv = importCsv;
        this.stockCalculationSuite = stockCalculationSuite;
        this.yahooFinanceDownloader = yahooFinanceDownloader;
        this.insightRules = insightRules;
        this.pricerepo = pricerepo;
    }

    public Path downloadCsv(String symbol) {
        try {
            Path out = Path.of("data/import/" + symbol + ".csv");
            System.out.println("Downloading: " + symbol);

            yahooFinanceDownloader.download(symbol, out);

            logMessage = "Success: " + symbol + " has been downloaded.";
            System.out.println(logMessage);
            return out;

        } catch (IOException | InterruptedException e) {
            logMessage = "Error: " + symbol + " could not be downloaded: " + e.getMessage();
            System.out.println(logMessage);
            return null;
        }
    }

    public void importCsv(String symbol) {
        try {
            importCsv.importPriceCsv(symbol, Path.of("data/import/" + symbol + ".csv"));
            logMessage = "Success: Data for " + symbol + " has been imported.";
        } catch (IOException | SQLException e) {
            logMessage = "Error: Failed to import " + symbol + ".\n\nReason: " + e.getMessage();
        }
    }

    public void insertCsv(String symbol) {
        Path csv = downloadCsv(symbol);
        if (csv == null) return;
        importCsv(symbol);
    }


    public void getFullAnalysis(String symbol) {
        try {
            stockCalculationSuite.getFullAnalysis(symbol);
        } catch (SQLException | IOException e) {
            logMessage = "Error analysis: " + e.getMessage();
        }
    }


    public String getInsightRules(String symbol) {
        try {
            logMessage = "Success: Imported insight rules for " + symbol + ".";
            return insightRules.getOverallInsight(symbol);
        } catch (SQLException | IOException e) {
            logMessage = "Error: Failed to import insight rules for " + symbol + ".\n\nReason: " + e.getMessage();
            return logMessage;
        }
    }

    public String getOpenAiResponse (String input, String symbol) {
        try {
            if (symbol == null || symbol.isBlank()) {
                return messageSender.sendMessage(input);
            }
            String insight = insightRules.getOverallInsight(symbol);
            MarketSnapshot analysis = stockCalculationSuite.getFullAnalysis(symbol);

            logMessage = "Success: made input " + symbol + ".";

            return messageSender.sendMessage(input + "\n" + insight + "\n" + analysis);
        } catch (SQLException | IOException e) {
            logMessage = "Error inputting to AI: " + e.getMessage();
            return logMessage;
        }
    }

    public boolean symbolExistsInDb(String symbol) {
        try {
            return pricerepo.symbolExistsInDb(symbol);
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean symbolExistsOnYahoo(String symbol) {
        try {
            return yahooFinanceDownloader.symbolExists(symbol);
        } catch (Exception e) {
            return false;
        }
    }

    public MarketView getMarketView(String symbol) throws Exception {
        String sym = symbol.trim().toUpperCase();

        MarketSnapshot snapshot = stockCalculationSuite.getFullAnalysis(sym);
        if (snapshot == null) {
            throw new IllegalStateException("No market data for " + sym);
        }

        MarketInsight insight = new MarketInsight(
                insightRules.getTrend(sym),
                insightRules.getMomentum(sym),
                insightRules.getVolatility(sym)
        );

        return new MarketView(snapshot, insight);
    }
}
