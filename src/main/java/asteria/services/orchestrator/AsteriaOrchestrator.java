package asteria.services.orchestrator;

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
    private String logMessage;

    public AsteriaOrchestrator(MessageSender messageSender, YahooCsvImporter importCsv, StockCalculationSuite stockCalculationSuite, YahooFinanceDownloader yahooFinanceDownloader, InsightRules insightRules) {
        this.messageSender = messageSender;
        this.importCsv = importCsv;
        this.stockCalculationSuite = stockCalculationSuite;
        this.yahooFinanceDownloader = yahooFinanceDownloader;
        this.insightRules = insightRules;

    }

    private void downloadCsv(String symbol) {
        try {
            System.out.println("Downloading: " + symbol);
            yahooFinanceDownloader.download(symbol, Path.of("data/import/" + symbol + ".CSV"));
            logMessage = "Success: " + symbol + " has been downloaded.";
            System.out.println(logMessage);
        } catch (IOException | InterruptedException e) {
            logMessage = "Error: " + e.getMessage() + " could not be downloaded.";
        }
    }

    private void importCsv(String symbol) {
        try {
            importCsv.importPriceCsv(symbol, Path.of("data/import/" + symbol + ".csv"));
            logMessage = "Success: Data for " + symbol + " has been imported.";
        } catch (IOException | SQLException e) {
            logMessage = "Error: Failed to import " + symbol + ".\n\nReason: " + e.getMessage();
        }
    }

    private void getFullAnalysis(String symbol) {
        try {
            stockCalculationSuite.getFullAnalysis(symbol);
        } catch (SQLException | IOException e) {
            logMessage = "Error analysis: " + e.getMessage();
        }
    }


    private String getInsightRules(String symbol) {
        try {
            logMessage = "Success: Imported insight rules for " + symbol + ".";
            return insightRules.getOverallInsight(symbol);
        } catch (SQLException | IOException e) {
            logMessage = "Error: Failed to import insight rules for " + symbol + ".\n\nReason: " + e.getMessage();
            return logMessage;
        }
    }

    private String getOpenAiResponse (String input, String symbol) {
        try {
            input = input + "\n" + insightRules.getOverallInsight(symbol) + "\n" + stockCalculationSuite.getFullAnalysis(symbol);
            String output = messageSender.sendMessage(input);
            logMessage = "Success: made input " + symbol + ".";
            return output;
        } catch (SQLException | IOException e) {
            logMessage = "Error inputting to AI: " + e.getMessage();
            return logMessage;
        }
    }
}
