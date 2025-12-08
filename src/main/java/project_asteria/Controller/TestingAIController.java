package project_asteria.Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import project_asteria.Database.DatabaseManager;
import project_asteria.Services.SendMessage;
import project_asteria.Services.ShowMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TestingAIController {
    private String prompt;
    private SendMessage sendMsg;
    private ShowMessage showMsg;
    private String AIresult;


    @FXML public TextField promptInput;
    @FXML public TextArea result;
    @FXML public Button inputButton;

    public TestingAIController(SendMessage sendMsg, ShowMessage showMsg) {
        this.sendMsg = sendMsg;
        this.showMsg = showMsg;
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
    public void testInsertUser() {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO users (username, display_name) VALUES (?, ?)")) {

            ps.setString(1, "demo");
            ps.setString(2, "Demo User");
            ps.executeUpdate();

            System.out.println("Insert user OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}