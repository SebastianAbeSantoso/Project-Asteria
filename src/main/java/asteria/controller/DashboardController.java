package asteria.controller;
import asteria.services.orchestrator.AsteriaOrchestrator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;
import java.sql.SQLException;

public class DashboardController {
    private final AsteriaOrchestrator orchestrator;

    public DashboardController(AsteriaOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @FXML
    private void handleInput(ActionEvent event) throws SQLException, IOException {
    }

    @FXML
    private void handleImport(ActionEvent event) {
    }

    @FXML
    private void handleDownload(ActionEvent event) throws IOException, InterruptedException {

    }

    @FXML
    private void handleCalc(ActionEvent event) {

    }
}