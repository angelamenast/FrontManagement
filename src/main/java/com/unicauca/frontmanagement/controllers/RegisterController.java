
package com.unicauca.frontmanagement.controllers;

import com.unicauca.frontmanagement.entity.*;
import com.unicauca.frontmanagement.services.PersonService;
import com.unicauca.frontmanagement.utilities.Navigation;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class RegisterController implements Initializable {

    @FXML
    private ComboBox<String> cbxProgram;

    @FXML
    private RadioButton rdbStudent;

    @FXML
    private RadioButton rdbProfessor;

    @FXML
    private RadioButton rdbCoordinator;

    @FXML
    private TextField txtLastName;

    @FXML
    private TextField txtResearchArea;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtName;

    @FXML
    private Button btnCreateAccount;

    @Autowired
    private PersonService personService;

    User userTemp = new User();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbxProgram.setItems(
                FXCollections.observableArrayList(
                        "Ingenieria De Sistemas",
                        "Ingenieria Electronica",
                        "Ingenieria Industrial",
                        "Tecnologia En Telematica"
                )
        );

        txtPhone.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*")) {
                        txtPhone.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                }
        );

    }

    @FXML
    private void eventClickbtnCreateAccount(ActionEvent event) {

        if (validateEmptyFields() == false) {
            Person person = userDataCapture();
            if (validatePassword() && validateEmail()) {
                registerPerson(person);
            }
        }

    }

    private boolean validateEmptyFields() {

        if (txtName.getText() == "") {
            Navigation.showAlert("Empty Fields", "Please enter your name.", Alert.AlertType.WARNING);
            return true;
        } else if (txtLastName.getText() == "") {
            Navigation.showAlert("Empty Fields", "Please enter your last name.", Alert.AlertType.WARNING);
            return true;
        } else if (txtEmail.getText() == "") {
            Navigation.showAlert("Empty Fields", "Please enter your email.", Alert.AlertType.WARNING);
            return true;
        } else if (txtResearchArea.getText() == "") {
            Navigation.showAlert("Empty Fields", "Please enter a research area.", Alert.AlertType.WARNING);
            return true;
        } else if (txtPassword.getText() == "") {
            Navigation.showAlert("Empty Fields", "Please enter a password.", Alert.AlertType.WARNING);
            return true;
        } else if (cbxProgram.getValue() == null) {
            Navigation.showAlert("Empty Fields", "Please enter your program.", Alert.AlertType.WARNING);
            return true;
        } else if (rdbProfessor.isSelected() == false && rdbStudent.isSelected() == false && rdbCoordinator.isSelected() == false) {
            Navigation.showAlert("Empty Fields", "Please enter a role.", Alert.AlertType.WARNING);
            return true;
        }
        return false;
    }

    private Person userDataCapture() {
        String name = txtName.getText();
        String lastName = txtLastName.getText();
        String email = txtEmail.getText();
        String researchArea = "";//txtResearchArea.getText();
        String password = txtPassword.getText();
        String phone = txtPhone.getText();
        EnumProgram program = null;
        switch (cbxProgram.getValue()) {

            case "Ingenieria De Sistemas" -> program = EnumProgram.Ingenieria_De_Sistemas;
            case "Ingenieria Electronica" -> program = EnumProgram.Ingenieria_Electronica;
            case "Ingenieria Industrial" -> program = EnumProgram.Ingenieria_Automatica_Industrial;
            case "Tecnologia En Telematica" -> program = EnumProgram.Tecnologia_en_Telematica;
        }
        List<Role> roles = new ArrayList<>();
        userTemp.setEmail(email);
        userTemp.setPassword(password);

        if (rdbProfessor.isSelected()) {
            Role role = new Role(EnumRole.Profesor);
            roles.add(role);
            userTemp.setRoles(roles);
            Professor newProfessor = Professor.builder().
                    name(name).lastName(lastName).phoneNumber(phone).program(program).user(userTemp).office(researchArea).build();
            return newProfessor;
        } else if (rdbStudent.isSelected()) {
            Role role = new Role(EnumRole.Estudiante);
            roles.add(role);
            userTemp.setRoles(roles);
            Student newStudent = Student.builder().
                    name(name).lastName(lastName).phoneNumber(phone).program(program).user(userTemp).studentCode(researchArea).build();
            return newStudent;
        } else {
            Role role = new Role(EnumRole.Coordinador);
            roles.add(role);
            userTemp.setRoles(roles);
            Coordinator newCoordinator = Coordinator.builder().
                    name(name).lastName(lastName).phoneNumber(phone).program(program).user(userTemp).nombredelperro(researchArea).build();
            return newCoordinator;
        }

    }

    private void registerPerson(Person person) {
        try {
            if (personService.registerPerson(person)) {
                Navigation.showAlert("Cuenta creada", "Cuenta creada exitosamente", Alert.AlertType.INFORMATION);
            } else {
                Navigation.showAlert("Cuenta existente.", "Ya existe una cuenta registrada con ese correo.", Alert.AlertType.ERROR);
                // AQUI FALTA VALIDAR QUE LA CUENTA YA EXISTE.
            }
        } catch (Exception ex) {
            Navigation.showAlert("Error al crear cuenta.", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validatePassword() {
        if (personService.validateSecurePassword(userTemp.getPassword()) == "OK") {
            return true;
        } else {
            Navigation.showAlert("Contraseña Incorrecta", personService.validateSecurePassword(userTemp.getPassword()), Alert.AlertType.ERROR);
            return false;
        }
    }

    private boolean validateEmail() {
        if (personService.validateEducationalEmail(userTemp.getEmail()) == "OK") {
            return true;
        } else {
            Navigation.showAlert("Contraseña Incorrecta", personService.validateEducationalEmail(userTemp.getEmail()), Alert.AlertType.ERROR);
            return false;
        }
    }

    @FXML
    private void eventClickcrdbStudent(ActionEvent event) {
        if (rdbStudent.isSelected()) {
            this.rdbProfessor.setSelected(false);
            this.rdbCoordinator.setSelected(false);
            this.txtResearchArea.setVisible(false);
        }
    }

    @FXML
    private void eventClickcrdbProfessor(ActionEvent event) {
        if (rdbProfessor.isSelected()) {
            this.rdbStudent.setSelected(false);
            this.rdbCoordinator.setSelected(false);
            this.txtResearchArea.setVisible(true);

        }
    }

    @FXML
    private void eventClickcrdbCoordinator(ActionEvent event) {
        if (rdbCoordinator.isSelected()) {
            this.rdbStudent.setSelected(false);
            this.rdbProfessor.setSelected(false);
            this.txtResearchArea.setVisible(false);
        }
    }

    @FXML
    void eventClicklblBack(MouseEvent event) {
        Navigation.changeView("Login");
        LoginController controller = Navigation.getController("Login");
        controller.clean();
    }

    public void clean() {
        txtName.clear();
        txtLastName.clear();
        txtPhone.clear();
        txtPassword.clear();
        txtEmail.clear();
        cbxProgram.setValue(null);
        rdbStudent.setSelected(false);
        rdbProfessor.setSelected(false);
        rdbCoordinator.setSelected(false);
    }


}
