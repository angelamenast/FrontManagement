package com.unicauca.FrontManagement.controllers;

import com.unicauca.FrontManagement.entity.DegreeProject;
import com.unicauca.FrontManagement.entity.EnumFileType;
import com.unicauca.FrontManagement.entity.File;
import com.unicauca.FrontManagement.entity.Person;
import com.unicauca.FrontManagement.services.DegreeProjectService;
import com.unicauca.FrontManagement.utilities.Navigation;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

@Component
public class UploadPreliminaryProjectController implements Initializable {

    @FXML
    private Button btnUpload;

    @FXML
    private Button btnUploadFile;

    @FXML
    private ComboBox<DegreeProject> cbxProjects;

    @FXML
    private DatePicker dtPckrDate;

    @FXML
    private ImageView imgFile;

    @FXML
    private Label lblShowDirector;

    @FXML
    private Label lblShowStudent;

    @FXML
    private Label lblShowTitle;

    @FXML
    private Label lblShowType;

    @FXML
    private Label lblStudent;

    @FXML
    private Label lblTitle;

    @FXML
    private Label lblTutor;

    @FXML
    private Label lblType;

    @Autowired
    private DegreeProjectService projectService;

    private Person person;
    private long idProfesor;

    private File fileAnteproyecto = null;

    private List<DegreeProject> approvedProjects;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Ocultar campos al inicio
        lblTitle.setVisible(false);
        lblStudent.setVisible(false);
        lblShowStudent.setVisible(false);
        lblShowDirector.setVisible(false);
        lblShowTitle.setVisible(false);
        lblType.setVisible(false);
        lblShowType.setVisible(false);
        lblTutor.setVisible(false);

        dtPckrDate.setValue(LocalDate.now()); // Fecha actual por defecto

    }

    private void loadProjects(){
        try {

            approvedProjects = projectService.findProjectsWithApprovedFormatA();

            List<DegreeProject> filteredProjects = new ArrayList<>();

            for(DegreeProject project : approvedProjects){
                if(Objects.equals(project.getDirector().getId(), this.idProfesor)){
                    filteredProjects.add(project);
                }
            }

            if (filteredProjects.isEmpty()) {
                Navigation.showAlert("Aviso", "No hay proyectos asociados con Formato A aprobado.", Alert.AlertType.INFORMATION);
            } else {
                cbxProjects.setItems(FXCollections.observableArrayList(filteredProjects));

                // Mostrar el título del proyecto en el ComboBox
                cbxProjects.setCellFactory(param -> new ListCell<>() {
                    @Override
                    protected void updateItem(DegreeProject project, boolean empty) {
                        super.updateItem(project, empty);
                        setText(empty || project == null ? null : project.getTitle());
                    }
                });

                cbxProjects.setConverter(new javafx.util.StringConverter<>() {
                    @Override
                    public String toString(DegreeProject project) {
                        return (project == null) ? "" : project.getTitle();
                    }

                    @Override
                    public DegreeProject fromString(String string) {
                        return cbxProjects.getItems().stream()
                                .filter(p -> p.getTitle().equals(string))
                                .findFirst().orElse(null);
                    }
                });
            }

            // Evento: al seleccionar un proyecto
            cbxProjects.setOnAction(e -> {
                DegreeProject selected = cbxProjects.getValue();
                if (selected != null) showProjectInfo(selected);
            });

        } catch (Exception e) {
            Navigation.showAlert("Error", "Error al cargar los proyectos: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showProjectInfo(DegreeProject project) {
        lblTitle.setVisible(true);
        lblStudent.setVisible(true);
        lblShowStudent.setVisible(true);
        lblShowDirector.setVisible(true);
        lblShowTitle.setVisible(true);
        lblType.setVisible(true);
        lblShowType.setVisible(true);
        lblTutor.setVisible(true);

        lblShowTitle.setText(project.getTitle());
        lblShowType.setText(project.getModality().toString());
        lblShowStudent.setText(project.getStudents().getFirst().getName());
        lblShowDirector.setText(project.getDirector().getName());

    }

    @FXML
    void eventClickBtnFile(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Anteproyecto (PDF)");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf")
        );

        java.io.File selectedFile = fileChooser.showOpenDialog(btnUploadFile.getScene().getWindow());

        if (selectedFile != null) {
            try {
                fileAnteproyecto = new File();
                fileAnteproyecto.setId(null);
                fileAnteproyecto.setVersion(1);
                fileAnteproyecto.setType(EnumFileType.Anteproyecto);
                fileAnteproyecto.setName(selectedFile.getName());
                fileAnteproyecto.setDocument(Files.readAllBytes(selectedFile.toPath()));
                fileAnteproyecto.setDate(dtPckrDate.getValue());

                Navigation.showAlert("Archivo seleccionado", "Se ha cargado el archivo " + selectedFile.getName(), Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                Navigation.showAlert("Error", "Error al cargar archivo: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void eventClickBtnUpload(MouseEvent event) {
        DegreeProject selectedProject = (DegreeProject) cbxProjects.getValue();

        if (selectedProject == null) {
            Navigation.showAlert("Advertencia", "Seleccione un proyecto.", Alert.AlertType.WARNING);
            return;
        }

        if (fileAnteproyecto == null) {
            Navigation.showAlert("Advertencia", "Seleccione un archivo de anteproyecto.", Alert.AlertType.WARNING);
            return;
        }

        try {

            System.out.println("Fecha anteproyecto: " + selectedProject.getFiles().getLast().getDate().toString());

            projectService.savePreliminaryProject(selectedProject.getId(),fileAnteproyecto);

            Navigation.showAlert("Éxito", "Anteproyecto subido correctamente.", Alert.AlertType.INFORMATION);
            cleanFields();
        } catch (Exception e) {
            Navigation.showAlert("Error", "Error al subir el anteproyecto: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cleanFields() {
        cbxProjects.setValue(null);
        fileAnteproyecto = null;
        lblShowTitle.setText("...");
        lblShowStudent.setText("...");
        lblShowDirector.setText("...");
        lblShowType.setText("...");
        dtPckrDate.setValue(LocalDate.now());
    }

    public void setPerson(Person person) {
        this.person = person;
        this.idProfesor = person.getId();
        loadProjects();
    }
}
