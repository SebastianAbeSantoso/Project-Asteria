package project_asteria.Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import project_asteria.Services.SendMessage;
import project_asteria.Services.ShowMessage;

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
}