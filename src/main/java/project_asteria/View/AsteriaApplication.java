package project_asteria.View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project_asteria.Controller.TestingAIController;
import project_asteria.Database.DatabaseManager;
import project_asteria.Services.AzureOpenAI;

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

        DatabaseManager.debugPrintTables();

        AzureOpenAI ai = new AzureOpenAI();

        TestingAIController controller = new TestingAIController(ai, ai);
        FXMLLoader fxmlLoader = new FXMLLoader(AsteriaApplication.class.getResource("/project_asteria/testing-ui.fxml"));
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);

        stage.setTitle("Asteria");
        stage.setScene(scene);
        stage.show();
    }
}