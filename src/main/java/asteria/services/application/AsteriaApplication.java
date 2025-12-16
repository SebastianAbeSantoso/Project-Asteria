package asteria.services.application;

import asteria.services.ai.ChatHistoryManager;
import asteria.services.insight.InsightRules;
import asteria.services.dataimport.api.YahooFinanceDownloaderImpl;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import asteria.controller.AsteriaController;
import asteria.database.DatabaseManager;
import asteria.database.SqliteConnectionFactory;
import asteria.repository.PriceHistoryRepository;
import asteria.services.ai.AzureOpenAI;
import asteria.services.calculators.momentum.RsiCalculatorImpl;
import asteria.services.calculators.trend.EmaCalculatorImpl;
import asteria.services.calculators.trend.MacdCalculatorImpl;
import asteria.services.calculators.trend.SmaCalculatorImpl;
import asteria.services.calculators.volatility.AtrCalculatorImpl;
import asteria.services.calculators.volatility.BollingerBandsCalculatorImpl;
import asteria.services.calculators.momentum.StochasticCalculatorImpl;
import asteria.services.dataimport.csv.YahooCsvImporterImpl;
import asteria.services.bridge.StockService;

import java.nio.file.Path;

public class AsteriaApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        SqliteConnectionFactory sqliteConnectionFactory = new SqliteConnectionFactory();
        DatabaseManager databaseManager = new DatabaseManager(sqliteConnectionFactory);
        databaseManager.initializeDatabase();
        YahooFinanceDownloaderImpl yahooFinanceDownloader = new YahooFinanceDownloaderImpl(Path.of("bin/YahooFinanceDownloader.exe"));

        PriceHistoryRepository repository = new PriceHistoryRepository(sqliteConnectionFactory);
        YahooCsvImporterImpl csvImporter = new YahooCsvImporterImpl(repository);

        SmaCalculatorImpl smaCalculator = new SmaCalculatorImpl();
        EmaCalculatorImpl emaCalculatorImpl = new EmaCalculatorImpl();
        MacdCalculatorImpl macdCalculator = new MacdCalculatorImpl(emaCalculatorImpl);
        RsiCalculatorImpl rsiCalculator = new RsiCalculatorImpl();
        BollingerBandsCalculatorImpl bollingerCalculator = new BollingerBandsCalculatorImpl();
        AtrCalculatorImpl atrCalculator = new AtrCalculatorImpl();
        StochasticCalculatorImpl stochasticCalculator = new StochasticCalculatorImpl();
        StockService stockService = new StockService(repository, smaCalculator, emaCalculatorImpl, macdCalculator, rsiCalculator, bollingerCalculator,  atrCalculator, stochasticCalculator);

        InsightRules insightRules = new InsightRules(stockService);
        ChatHistory chatHistory = new ChatHistory();
        ChatHistoryManager chatHistoryManager = new ChatHistoryManager("data/ai/chat_log.txt");
        AzureOpenAI ai = new AzureOpenAI(chatHistory, chatHistoryManager);;


        AsteriaController controller = new AsteriaController(ai, csvImporter, stockService, yahooFinanceDownloader);
        FXMLLoader fxmlLoader = new FXMLLoader(AsteriaApplication.class.getResource("/asteria/testing-ui.fxml"));
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);

        stage.setTitle("Asteria");
        stage.setScene(scene);
        stage.show();
    }
}