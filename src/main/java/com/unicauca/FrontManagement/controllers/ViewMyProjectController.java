package com.unicauca.FrontManagement.controllers;

import com.unicauca.FrontManagement.entity.DegreeProject;
import com.unicauca.FrontManagement.entity.Person;
import com.unicauca.FrontManagement.entity.Student;
import com.unicauca.FrontManagement.services.DegreeProjectService;
import com.unicauca.FrontManagement.services.StudentService;
import javafx.fxml.Initializable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Component
public class ViewMyProjectController implements Initializable {
    @FXML
    private Label lblActualState;

    @FXML
    private Label lblDirector;

    @FXML
    private Label lblInitDate;

    @FXML
    private Label lblProjectType;

    @FXML
    private Label lblStudentName;

    @FXML
    private Label lblTitle;

    @FXML
    private Tab tbpnProjectInfo;

    @Getter
    @Setter
    private Person person;

    @Autowired
    private DegreeProjectService projectService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cleanLabels();
    }

    public void cleanLabels(){
        lblTitle.setVisible(false);
        lblActualState.setVisible(false);
        lblDirector.setVisible(false);
        lblInitDate.setVisible(false);
        lblProjectType.setVisible(false);
        lblStudentName.setVisible(false);
    }

    public void setLoggedStudent(Person person) {
        this.person = person;
        LoadData();
    }

    public void LoadData() {
        try {
            if (this.person == null) {
                System.err.println("No hay estudiante logueado.");
                return;
            }

             DegreeProject project = projectService.findProjectByEmail(person.getUser().getEmail());

            if (project == null) {
                lblTitle.setText("No tiene un proyecto asignado actualmente.");
                return;
            }

            lblTitle.setText(project.getTitle());
            lblActualState.setText(project.getState() != null ? project.getState().getName() : "Sin estado");
            lblDirector.setText(project.getDirector() != null ? project.getDirector().getName() : "Sin director");
            lblInitDate.setText(project.getFiles().getLast().getDate() != null
                    ? project.getFiles().getLast().getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "No registrada");
            lblProjectType.setText(project.getModality() != null ? project.getModality().toString() : "N/A");
            lblStudentName.setText(person.getName()+"  "+person.getLastName());

            // Mostrar labels
            lblTitle.setVisible(true);
            lblActualState.setVisible(true);
            lblDirector.setVisible(true);
            lblInitDate.setVisible(true);
            lblProjectType.setVisible(true);
            lblStudentName.setVisible(true);

        } catch (Exception e) {
            System.err.println("Error cargando datos del proyecto: " + e.getMessage());
        }
    }

}
