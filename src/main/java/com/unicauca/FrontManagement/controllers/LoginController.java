package com.unicauca.FrontManagement.controllers;

import com.unicauca.FrontManagement.entity.*;
import com.unicauca.FrontManagement.infra.dto.HeadOfDepartmentRequest;
import com.unicauca.FrontManagement.services.PersonService;
import com.unicauca.FrontManagement.utilities.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class LoginController implements Initializable {

    @FXML
    private TextField txtUser;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnLogin;
    @FXML
    private Label lblRegister;

    @Autowired
    private PersonService personService;

    @FXML
    private void evenBtnLogin(ActionEvent event) throws IOException {
    try{
            User userLogin = new User();
            userLogin.setEmail(txtUser.getText());
            userLogin.setPassword(txtPassword.getText());
            Person objPerson = personService.obtainUserByEmail(userLogin.getEmail());

            int flag = validateData(userLogin);

            switch (flag) {
                case 1:

                    if (objPerson.getUser().getRoles().getFirst().getRoleType() == EnumRole.Profesor) {
                        Navigation.showAlert("Login exitoso", "Bienvenido " + objPerson.getName()+" "+ objPerson.getLastName(), Alert.AlertType.CONFIRMATION);
                        //Navigation.changeViewNexWindow("ProfessorDashboard","Panel Profesor");
                        Navigation.changeView("ProfessorDashboard");
                        ProfessorDashboardController controller = Navigation.getController("ProfessorDashboard");
                        controller.setPerson(objPerson);
                    } else if (objPerson.getUser().getRoles().getFirst().getRoleType() == EnumRole.Estudiante) {
                        Navigation.showAlert("Login exitoso", "Bienvenido " + objPerson.getName()+" "+ objPerson.getLastName(), Alert.AlertType.CONFIRMATION);
                        //Navigation.changeViewNexWindow("StudentDashboard","Panel Estudiante");
                        Navigation.changeView("StudentDashboard");
                        StudentDashboardController controller = Navigation.getController("StudentDashboard");
                        controller.inicializatePerson(objPerson);
                    } else if (objPerson.getUser().getRoles().getFirst().getRoleType() == EnumRole.Coordinador) {
                        Navigation.showAlert("Login exitoso", "Bienvenido " + objPerson.getName()+" "+ objPerson.getLastName(), Alert.AlertType.CONFIRMATION);
                        //Navigation.changeViewNexWindow("CoordinatorDashboard","Panel Coordinador");
                        Navigation.changeView("CoordinatorDashboard");
                        CoordinatorDashboardController controller = Navigation.getController("CoordinatorDashboard");
                        controller.inicializatePerson(objPerson);
                    }else if (objPerson.getUser().getRoles().getFirst().getRoleType() == EnumRole.JefeDeDepartamento) {
                        Navigation.showAlert("Login exitoso", "Bienvenido " + objPerson.getName() + " " + objPerson.getLastName(), Alert.AlertType.CONFIRMATION);
                        // Abrir la vista del Jefe de Departamento
                        Navigation.changeView("DepartamentHeadDashboard"); // Asegúrate de tener esta vista
                        DepartamentHeadDashboardController controller = Navigation.getController("DepartamentHeadDashboard");
                        controller.inicializatePerson(objPerson);
                    }

                    else {
                        Navigation.showAlert("Error", "No se pudo determinar el rol del usuario", Alert.AlertType.ERROR);
                    }
                    break;

                case 2:
                    Navigation.showAlert("Error de login", "Por favor llene todos los campos requeridos para iniciar sesion", Alert.AlertType.INFORMATION);
                    break;
                default:
                    Navigation.showAlert("Error de login", "Usuario o contraseña incorrectos", Alert.AlertType.ERROR);
                    break;
            }
        } catch (Exception ex) {
            Navigation.showAlert("Error al inciar sesión: .", ex.getMessage(), Alert.AlertType.ERROR);
        }

    }

    @FXML
    private void evenBtnRegister(javafx.scene.input.MouseEvent event) {
        Navigation.changeView("Register");
        RegisterController controller = Navigation.getController("Register");
        controller.clean();
    }

    public void clean(){
        txtPassword.clear();
        txtUser.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public int validateData(User user){
        try{

            if (user.getEmail() == null || user.getEmail().isEmpty() || user.getPassword() == null || user.getPassword().isEmpty()){
                return 2;
            }
            else if(personService.loginPerson(user)){
                return 1;
            }
            else{
                return 3;
            }
        } catch (Exception ex) {
            //Navigation.showAlert("Error al crear cuenta.", ex.getMessage(), Alert.AlertType.ERROR);
            System.out.println(ex.getMessage());
        }
        return 3;
    }

}
