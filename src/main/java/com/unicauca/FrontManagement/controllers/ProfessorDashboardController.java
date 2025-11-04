package com.unicauca.FrontManagement.controllers;


import com.unicauca.FrontManagement.entity.*;
import com.unicauca.FrontManagement.utilities.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Component;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class ProfessorDashboardController implements Initializable {

    @FXML
    private AnchorPane anchrCenterPane;

    @FXML
    private Label lblName;

    private Person person;

    @FXML
    private void showMyData() throws IOException {
        MyDataController controller
                = Navigation.loadInAnchorPane(anchrCenterPane, "MyData");
        if (controller != null) {
            controller.setPerson(this.person);
        }
    }

    @FXML
    private void showUploadFormatA() {
        UploadFormatAController controller
                = Navigation.loadInAnchorPane(anchrCenterPane, "UploadFormatA");
        if (controller != null) {
            controller.setPerson(this.person);
        }
    }

    @FXML
    private void showReuploadProject() {
        ReuploadFormatAListController controller = Navigation.loadInAnchorPane(anchrCenterPane, "ReuploadFormatAList");
        controller.setPerson(this.person);

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //ServicioNotificaciones.getInstance().subscribe(msg -> {
         //   Platform.runLater(() -> {
           //     Alert alerta = new Alert(Alert.AlertType.INFORMATION);
             //   alerta.setTitle("Nueva Notificación");
               // alerta.setHeaderText("Actualización del sistema");
                //alerta.setContentText(msg);
                //alerta.show();
            //});
        //});

    }

    @FXML
    void showUploadPreliminaryProject() {
        UploadPreliminaryProjectController controller = Navigation.loadInAnchorPane(anchrCenterPane, "UploadPreliminaryProject");
        controller.setPerson(this.person);

    }


    public void loadPerson() {
        lblName.setText(person.getName() + " " + person.getLastName());
    }

    public void setPerson(Person person) throws IOException {
        this.person = person;
        loadPerson();
        showMyData();
    }

    @FXML
    void eventBtnEndSession(ActionEvent event) {
        Navigation.changeView("Login");
    }

    public AnchorPane getAnchrPane(){
        return this.anchrCenterPane;
    }
}