package com.project;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;

public class Controller {

    @FXML
    private Button buttonAdd;
    private Button buttonRest;
    private Button buttonMulti;
    private Button buttonDiv;
    private Button buttonResult;

    @FXML
    private Button num9;
    private Button num8;
    private Button num7;
    private Button num6;
    private Button num5;
    private Button num4;
    private Button num3;
    private Button num2;
    private Button num1;
    private Button num0;

    @FXML
    private Text textCounter;

    private String aux;

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
