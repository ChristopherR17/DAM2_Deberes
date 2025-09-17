package com.project;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;

public class Controller {

    @FXML
    private Button buttonAdd, buttonRest, buttonMulti, buttonDiv, buttonResult;

    @FXML
    private Button num9, num8, num7, num6, num5, num4, num3, num2, num1, num0, dot;

    @FXML
    private Text textCounter;

    private StringBuilder input = new StringBuilder();

    @FXML
    private void actionNums(ActionEvent event) {
        input = num9.getText();
    }

    @FXML
    private void actionAdd(ActionEvent event) {
        // counter++;
        // textCounter.setText(String.valueOf(counter));
    }

    @FXML
    private void actionRest(ActionEvent event){

    }

    @FXML
    private void actionMulti(ActionEvent event){

    }

    @FXML
    private void actionDiv(ActionEvent event){

    }

    @FXML
    private void actionResult(ActionEvent event){

    }
}
