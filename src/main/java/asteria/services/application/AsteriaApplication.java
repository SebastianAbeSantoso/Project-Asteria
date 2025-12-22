package asteria.services.application;

import asteria.repository.WatchlistRepository;
import asteria.security.KeyProviderImpl;
import asteria.services.ai.ChatHistoryManager;
import asteria.services.insight.InsightRulesImpl;
import asteria.services.dataimport.api.YahooFinanceDownloaderImpl;
import asteria.services.orchestrator.AsteriaOrchestrator;
import asteria.services.watchlist.WatchlistService;
import asteria.services.watchlist.WatchlistServiceImpl;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import asteria.controller.DashboardController;
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
        WatchlistRepository watchlistRepository = new WatchlistRepository(sqliteConnectionFactory);
        WatchlistServiceImpl watchlistServiceimpl = new WatchlistServiceImpl(watchlistRepository);

        SmaCalculatorImpl smaCalculator = new SmaCalculatorImpl();
        EmaCalculatorImpl emaCalculatorImpl = new EmaCalculatorImpl();
        MacdCalculatorImpl macdCalculator = new MacdCalculatorImpl(emaCalculatorImpl);
        RsiCalculatorImpl rsiCalculator = new RsiCalculatorImpl();
        BollingerBandsCalculatorImpl bollingerCalculator = new BollingerBandsCalculatorImpl();
        AtrCalculatorImpl atrCalculator = new AtrCalculatorImpl();
        StochasticCalculatorImpl stochasticCalculator = new StochasticCalculatorImpl();
        StockService stockService = new StockService(repository, smaCalculator, emaCalculatorImpl, macdCalculator, rsiCalculator, bollingerCalculator,  atrCalculator, stochasticCalculator);

        InsightRulesImpl insightRulesImpl = new InsightRulesImpl(stockService);
        KeyProviderImpl keyProvider = new KeyProviderImpl();
        ChatHistory chatHistory = new ChatHistory();
        ChatHistoryManager chatHistoryManager = new ChatHistoryManager("data/ai/chat_log.txt");
        AzureOpenAI ai = new AzureOpenAI(chatHistory, chatHistoryManager, keyProvider);;

        AsteriaOrchestrator orchestrator = new AsteriaOrchestrator(ai, csvImporter, stockService, yahooFinanceDownloader, insightRulesImpl);
        DashboardController controller = new DashboardController(orchestrator, watchlistServiceimpl);

        FXMLLoader fxmlLoader = new FXMLLoader(AsteriaApplication.class.getResource("/asteria/dashboard/asteria-dashboard.fxml"));
        fxmlLoader.setController(controller);

        Scene scene = new Scene(fxmlLoader.load(), 1280, 800);

        stage.setTitle("Asteria");
        stage.setScene(scene);
        stage.show();
    }
}