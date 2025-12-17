package asteria.controller;


import asteria.services.dataimport.api.YahooFinanceDownloader;
import asteria.services.insight.InsightRules;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import asteria.model.BollingerBandsResult;
import asteria.model.MacdResult;
import asteria.model.StochasticResult;
import asteria.services.bridge.*;
import asteria.services.dataimport.csv.YahooCsvImporter;
import asteria.services.ai.MessageSender;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

public class AsteriaController {
    private String prompt;
    private MessageSender messageSender;
    private YahooCsvImporter importCsv;
    private YahooFinanceDownloader yahooFinanceDownloader;
    private InsightRules insightRules;

    private StockCalculationSuite stockCalculationSuite;
    private String AIresult;

    @FXML
    public TextField promptInput;
    @FXML
    public TextArea result;
    @FXML
    public TextArea resultCalc;
    @FXML
    public Button inputButton;
    @FXML
    public Button ImportButton;
    @FXML
    public Button handleCalc;
    @FXML
    public TextField calcModeInput;

    public AsteriaController(MessageSender messageSender, YahooCsvImporter importCsv, StockCalculationSuite stockCalculationSuite, YahooFinanceDownloader yahooFinanceDownloader, InsightRules insightRules) {
        this.messageSender = messageSender;
        this.importCsv = importCsv;
        this.stockCalculationSuite = stockCalculationSuite;
        this.yahooFinanceDownloader = yahooFinanceDownloader;
        this.insightRules = insightRules;
    }

    @FXML
    private void handleInput(ActionEvent event) throws SQLException, IOException {
        String prompt = promptInput.getText();
        String symbol = calcModeInput.getText();
        if (calcModeInput.getText().equals(symbol)) prompt = prompt + "\n" + insightRules.getOverallInsight(symbol) + "\n" + stockCalculationSuite.getFullAnalysis(symbol);

        AIresult = messageSender.sendMessage(prompt);
        result.clear();

        result.setText(AIresult + "\n");

    }

    @FXML
    private void handleImport(ActionEvent event) {
        try {
            String symbol = calcModeInput.getText();

            importCsv.importPriceCsv(symbol, Path.of("data/import/" + symbol + ".csv"));
            System.out.println("Import successful.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDownload(ActionEvent event) throws IOException, InterruptedException {
        String symbol = calcModeInput.getText();

        yahooFinanceDownloader.download(symbol, Path.of("data/import/" + symbol + ".CSV"));

        System.out.println("Downloading: " + symbol);
    }

    @FXML
    private void handleCalc (ActionEvent event) throws SQLException, IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException {
        int period = 10;
        String symbol = "BBCA";
        double stdDev = 2.0;
        int kPeriod = 14;
        int dPeriod = 3;
        calcModeInput.getText();
        if (calcModeInput.getText().equals("sma")) {
            double sma10 = stockCalculationSuite.getSma(symbol, period);
            result.setText("Sma10 = " + sma10 + "\n");
        } else if (calcModeInput.getText().equals("ema")) {
            double ema10 = stockCalculationSuite.getEma(symbol, period);
            result.setText("Ema10 = " + ema10 + "\n");
        } else if (calcModeInput.getText().equals("macd")) {
            MacdResult macdResult = stockCalculationSuite.getMacd(symbol, 12, 26,9);
            result.setText(macdResult.toString() + "\n");
        } else if (calcModeInput.getText().equals("rsi")) {
            double rsi10 = stockCalculationSuite.getRsi(symbol, period);
            result.setText("Rsi10 = " + rsi10 + "\n");
        } else if (calcModeInput.getText().equals("bb")) {
            BollingerBandsResult BB = stockCalculationSuite.getBollingerBands(symbol);
            result.setText(BB + "\n");
        } else if (calcModeInput.getText().equals("bbc")) {
            BollingerBandsResult customBB = stockCalculationSuite.getCustomBollingerBands(symbol, period, stdDev);
            result.setText(customBB + "\n");
        } else if (calcModeInput.getText().equals("atr")) {
            List<Double> atr = stockCalculationSuite.getAtr(symbol, period);
            result.setText(atr + "\n");
        } else if (calcModeInput.getText().equals("stc")) {
            List<StochasticResult> stcc = stockCalculationSuite.getStochastic(symbol, kPeriod, dPeriod);
            result.setText(stcc + "\n");
        } else if (calcModeInput.getText().equals("stcc")) {
            List<StochasticResult> stc = stockCalculationSuite.getStandardStochastic(symbol);
            result.setText(stc + "\n");
        } else if (calcModeInput.getText().equals("build")){
            String myRealKey = "40MRj0fd7wtdSWOjvOGfc1R1nSunaBPJ6pUAjCWrQUMaUgV4onVzJQQJ99BFACfhMk5XJ3w3AAAAACOGPLIF";
            String salt = "AsteriaProject25";
            SecretKeySpec spec = new SecretKeySpec(salt.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, spec);

            byte[] encrypted = cipher.doFinal(myRealKey.getBytes());
            System.out.println("Encrypted Blob: " + Base64.getEncoder().encodeToString(encrypted));
        }

        calcModeInput.clear();
    }
}