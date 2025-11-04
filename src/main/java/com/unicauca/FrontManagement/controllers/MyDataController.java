package com.unicauca.FrontManagement.controllers;

import com.unicauca.FrontManagement.entity.Person;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class MyDataController implements Initializable {
    
    @FXML
    private TextField txtLastName;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtProgram;

    @FXML
    private TextField txtRole;

    private Person person;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }



    public void loadPerson(){
        txtName.setText(person.getName());
        txtLastName.setText(person.getLastName());
        txtPhone.setText(person.getPhoneNumber());
        txtEmail.setText(person.getUser().getEmail());
        txtProgram.setText(String.valueOf(person.getProgram()));
        txtRole.setText(String.valueOf(person.getUser().getRoles().getFirst().getRoleType().toString()));

    }

    public void setPerson(Person person) {
        this.person = person;
        loadPerson();
    }
    
}
