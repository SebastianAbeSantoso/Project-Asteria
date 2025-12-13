package project_asteria.View.Application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project_asteria.Controller.AsteriaController;
import project_asteria.Database.DatabaseManager;
import project_asteria.Database.SqliteConnectionFactory;
import project_asteria.Repository.PriceHistoryRepository;
import project_asteria.Services.AI.AzureOpenAI;
import project_asteria.Services.CSV.CsvImporter;
import project_asteria.Services.Calculators.*;
import project_asteria.Services.Bridge.StockService;

public class AsteriaApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        SqliteConnectionFactory sqliteConnectionFactory = new SqliteConnectionFactory();
        DatabaseManager databaseManager = new DatabaseManager(sqliteConnectionFactory);

        databaseManager.initializeDatabase();

        AzureOpenAI ai = new AzureOpenAI();;
        PriceHistoryRepository repository = new PriceHistoryRepository(sqliteConnectionFactory);
        CsvImporter csvImporter = new CsvImporter(repository);
        SmaCalculator smaCalculator = new SmaCalculator();
        EmaCalculator emaCalculator = new EmaCalculator();
        MacdCalculator macdCalculator = new MacdCalculator(emaCalculator);
        RsiCalculator rsiCalculator = new RsiCalculator();
        BollingerBandsCalculator bollingerCalculator = new BollingerBandsCalculator();
        AtrCalculator atrCalculator = new AtrCalculator();
        StockService stockService = new StockService(repository, smaCalculator, emaCalculator, macdCalculator, rsiCalculator, bollingerCalculator,  atrCalculator);


        AsteriaController controller = new AsteriaController(ai, csvImporter, stockService);
        FXMLLoader fxmlLoader = new FXMLLoader(AsteriaApplication.class.getResource("/project_asteria/testing-ui.fxml"));
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);

        stage.setTitle("Asteria");
        stage.setScene(scene);
        stage.show();
    }
}