/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.unicauca.frontmanagement.controllers;

import com.unicauca.frontmanagement.entity.Person;
import com.unicauca.frontmanagement.utilities.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class StudentDashboardController implements Initializable {

    @FXML
    private AnchorPane anchrCenterPane;

    @FXML
    private Label lblName;

    @Setter @Getter
    private Person person;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void inicializatePerson(Person person){
        setPerson(person);
        loadPerson();
        showMyData();
    }
    
    private void loadPerson(){
        lblName.setText(person.getName() +" "+person.getLastName());
    }

    @FXML
    private void eventBtnEndSesion(ActionEvent event) {
        Navigation.changeView("Login");
    }

    @FXML
    void showMyData() {
        MyDataController controller= Navigation.loadInAnchorPane(anchrCenterPane, "MyData");
        controller.setPerson(person);
    }

//    @FXML
//    void mostrarMiProyecto(MouseEvent event) {
//        EstudianteSeguimientoProyectoController controlador =
//                Navigation.cargarEnAnchorPane(anchrCenterPane,"EstudianteSeguimientoProyecto");
//        controlador.inicializarperson(person);
//
//    }
    
}
