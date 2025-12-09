package project_asteria.View.Application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project_asteria.Controller.TestingAIController;
import project_asteria.Database.DatabaseManager;
import project_asteria.Repository.PriceHistoryRepository;
import project_asteria.Services.AI.AzureOpenAI;
import project_asteria.Services.CSV.CsvImporter;
import project_asteria.Services.Calc.*;
import project_asteria.Services.Bridge.StockService;

import java.io.IOException;
import java.sql.SQLException;

public class AsteriaApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        try {
            DatabaseManager.initializeDatabase();
            System.out.println("SQLite initialized.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        AzureOpenAI ai = new AzureOpenAI();
        CsvImporter csvImporter = new CsvImporter();
        PriceHistoryRepository repository = new PriceHistoryRepository();
        SmaCalculator smaCalculator = new SmaCalculator();
        EmaCalculator emaCalculator = new EmaCalculator();
        MacdCalculator macdCalculator = new MacdCalculator(emaCalculator);
        RsiCalculator rsiCalculator = new RsiCalculator();

        StockService stockService = new StockService(repository, smaCalculator, emaCalculator, macdCalculator, rsiCalculator);

        TestingAIController controller = new TestingAIController(ai, csvImporter, stockService, stockService, stockService, stockService);
        FXMLLoader fxmlLoader = new FXMLLoader(AsteriaApplication.class.getResource("/project_asteria/testing-ui.fxml"));
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);

        stage.setTitle("Asteria");
        stage.setScene(scene);
        stage.show();
    }
}