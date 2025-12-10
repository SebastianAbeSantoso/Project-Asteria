package project_asteria.Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import project_asteria.Model.BollingerBandsResult;
import project_asteria.Model.MacdResult;
import project_asteria.Services.Bridge.*;
import project_asteria.Services.CSV.ImportCsv;
import project_asteria.Services.AI.SendMessage;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

public class AsteriaController {
    private String prompt;
    private SendMessage sendMsg;
    private ImportCsv importCsv;
    private GetSmaCalculator getSma;
    private GetEmaCalculator getEma;
    private GetMacdCalculator getMacd;
    private GetRsiCalculator getRsi;
    private GetBollingerBandsCalculator getBB;
    private GetAtrCalculator getAtr;
    private String AIresult;

    @FXML public TextField promptInput;
    @FXML public TextArea result;
    @FXML public TextArea resultCalc;
    @FXML public Button inputButton;
    @FXML public Button ImportButton;
    @FXML public Button handleCalc;
    @FXML public TextField calcModeInput;

    public AsteriaController(SendMessage sendMsg, ImportCsv importCsv, GetSmaCalculator getSma, GetEmaCalculator getEma, GetMacdCalculator getMacd, GetRsiCalculator getRsi, GetBollingerBandsCalculator getBB, GetAtrCalculator getAtr) {
        this.sendMsg = sendMsg;
        this.importCsv = importCsv;
        this.getSma = getSma;
        this.getEma = getEma;
        this.getMacd = getMacd;
        this.getRsi = getRsi;
        this.getBB = getBB;
        this.getAtr = getAtr;
    }

    @FXML
    private void handleInput(ActionEvent event) {
        String prompt = promptInput.getText();
        AIresult = sendMsg.sendMessage(prompt);
        result.clear();

        System.out.println("prompt    = " + prompt);
        System.out.println("AIresult  = " + AIresult + "\n");
        result.setText(AIresult+ "\n");

    }

    @FXML
    private void handleImport (ActionEvent event) {
        try {
            importCsv.importPriceCsv("BBCA", Path.of("data/import/BBCA.csv"));
            System.out.println("Import successful.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCalc (ActionEvent event) throws SQLException, IOException {
        int period = 10;
        String symbol = "BBCA";
        double stdDev = 2.0;
        calcModeInput.getText();
        if (calcModeInput.getText().equals("sma")) {
            double sma10 =  getSma.getSma(symbol, period);
            result.setText("Sma10 = " + sma10 + "\n");
        } else if (calcModeInput.getText().equals("ema")) {
            double ema10 = getEma.getEma(symbol, period);
            result.setText("Ema10 = " + ema10 + "\n");
        } else if (calcModeInput.getText().equals("macd")) {
            MacdResult macdResult = getMacd.getMacd(symbol, 12, 26,9);
            result.setText(macdResult.toString() + "\n");
        } else if (calcModeInput.getText().equals("rsi")) {
            double rsi10 = getRsi.getRsi(symbol, period);
            result.setText("Rsi10 = " + rsi10 + "\n");
        } else if (calcModeInput.getText().equals("bb")) {
            BollingerBandsResult BB = getBB.getBollingerBands(symbol);
            result.setText(BB + "\n");
        } else if (calcModeInput.getText().equals("bbc")) {
            BollingerBandsResult customBB = getBB.getCustomBollingerBands(symbol, period, stdDev);
            result.setText(customBB + "\n");
        } else if (calcModeInput.getText().equals("atr")) {
            List<Double> atr = getAtr.calculateATR(symbol, period);
            result.setText(atr + "\n");
        }

        calcModeInput.clear();
    }
}