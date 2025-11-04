package com.unicauca.FrontManagement.controllers;
import com.unicauca.FrontManagement.entity.*;
import com.unicauca.FrontManagement.utilities.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


@Component
public class CoordinatorDashboardController implements Initializable {


    @FXML
    private AnchorPane anchrCenterPane;

    @FXML
    private Label lblName;

    private Person person;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    void showMyData() throws IOException {
        MyDataController controller
                = Navigation.loadInAnchorPane(anchrCenterPane, "MyData");

        if (controller != null) {
            controller.setPerson(this.person);
        }
    }

    @FXML
    private void listFormats() throws IOException {
        ListFormatAController controller = Navigation.loadInAnchorPane(anchrCenterPane, "ListFormatA");
        controller.setPerson(this.person);
    }

    public void inicializatePerson(Person person) throws IOException {
        setPerson(person);
        loadPerson();
        showMyData();
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public AnchorPane getAnchrPane(){
        return this.anchrCenterPane;
    }

    private void loadPerson(){

        lblName.setText(person.getName()+" "+ person.getLastName());

    }

    @FXML
    void eventBtnEndSession(ActionEvent event) {
        Navigation.changeView("Login");
    }

    }