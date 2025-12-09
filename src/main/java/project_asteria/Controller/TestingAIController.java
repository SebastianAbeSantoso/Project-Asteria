package project_asteria.Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import project_asteria.Model.MacdResult;
import project_asteria.Services.Bridge.GetEmaCalculator;
import project_asteria.Services.Bridge.GetMacdCalculator;
import project_asteria.Services.Bridge.GetSmaCalculator;
import project_asteria.Services.CSV.ImportCsv;
import project_asteria.Services.AI.SendMessage;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

public class TestingAIController {
    private String prompt;
    private SendMessage sendMsg;
    private ImportCsv importCsv;
    private GetSmaCalculator getSma;
    private GetEmaCalculator getEma;
    private GetMacdCalculator getMacd;
    private String AIresult;

    @FXML public TextField promptInput;
    @FXML public TextArea result;
    @FXML public TextArea resultCalc;
    @FXML public Button inputButton;
    @FXML public Button ImportButton;
    @FXML public Button handleCalc;
    @FXML public TextField calcModeInput;

    public TestingAIController(SendMessage sendMsg, ImportCsv importCsv, GetSmaCalculator getSma, GetEmaCalculator getEma, GetMacdCalculator getMacd) {
        this.sendMsg = sendMsg;
        this.importCsv = importCsv;
        this.getSma = getSma;
        this.getEma = getEma;
        this.getMacd = getMacd;
    }

    @FXML
    private void handleInput(ActionEvent event) {
        String prompt = promptInput.getText();
        AIresult = sendMsg.sendMessage(prompt);
        result.clear();

        System.out.println("prompt    = " + prompt+ "\n");
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
        calcModeInput.getText();
        if (calcModeInput.getText().equals("sma")) {
            double sma10 =  getSma.getSma("BBCA", 10);
            result.setText("Sma10 = " + sma10 + "\n");
        } else if (calcModeInput.getText().equals("ema")) {
            double ema10 = getEma.getEma("BBCA", 10);
            result.setText("Ema10 = " + ema10 + "\n");
        } else if (calcModeInput.getText().equals("macd")) {
            MacdResult macdResult = getMacd.getMacd("BBCA", 12, 26,9);
            result.setText(macdResult.toString() + "\n");
        }
        calcModeInput.clear();
    }
}