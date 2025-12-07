package project_asteria.View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project_asteria.Controller.TestingAIController;
import project_asteria.Services.AzureOpenAI;

import java.io.IOException;

public class TestingAIView extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        AzureOpenAI ai = new AzureOpenAI();
        TestingAIController controller = new TestingAIController(ai, ai);
        FXMLLoader fxmlLoader = new FXMLLoader(TestingAIView.class.getResource("/project_asteria/testing-ui.fxml"));
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
        stage.setTitle("XPresso Sign Up");
        stage.setScene(scene);
        stage.show();
    }
}