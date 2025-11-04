package com.unicauca.FrontManagement.controllers;

import com.unicauca.FrontManagement.entity.DegreeProject;
import com.unicauca.FrontManagement.entity.EnumFileType;
import com.unicauca.FrontManagement.entity.Person;
import com.unicauca.FrontManagement.entity.Student;
import com.unicauca.FrontManagement.services.DegreeProjectService;
import com.unicauca.FrontManagement.services.PersonService;
import com.unicauca.FrontManagement.utilities.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Component
public class EvaluateFormatAController {

    @FXML
    private Button btnApprove;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnDownloadFormatA;
    @FXML
    private Button btnDecline;
    @FXML
    private Button btnUploadCorrection;
    @FXML
    private Label lblEmail1;
    @FXML
    private Label lblEmail2;//coman mierda todos los odio
    @FXML
    private Label lblEmailStudent1;
    @FXML
    private Label lblEmailStudent2;
    @FXML
    private Label lblDirectorName;
    @FXML
    private Label lblProjectName;
    @FXML
    private Label lblProjectType;

    @Autowired
    private DegreeProjectService degreeProjectService;

    private DegreeProject degreeProject;
    private byte[] devolutionFile;
    private String devolutionFileName;

    @Getter
    @Setter
    private Person person;

    @FXML
    public void initialize() {
        devolutionFile = null;
        devolutionFileName = null;
    }
//////////////////////////////////////
    public void setDegreeProject(DegreeProject degreeProject) {
        this.degreeProject = degreeProject;
        loadProjectData();
    }

    public void loadProjectData() {

        if (degreeProject != null) {
            lblProjectName.setText(degreeProject.getTitle());
            lblDirectorName.setText(degreeProject.getDirector().getName());
            lblProjectType.setText(degreeProject.getFiles().getLast().getName().toString());

            List<Student> students = degreeProject.getStudents();

            lblEmail1.setText(students.getFirst().getUser().getEmail());

            if (lblEmail2 != null && !students.isEmpty()) {
                lblEmailStudent2.setVisible(true);
                lblEmail2.setVisible(true);
                lblEmail2.setText(lblEmail2.getText());
            } else {
                lblEmailStudent2.setVisible(false);
                lblEmail2.setVisible(false);
            }
        } else {
            System.out.println("There's not a degree project");
        }
    }

    @FXML
    void eventBtnDownloadFormatA(ActionEvent event) {

        byte[] file = degreeProject.getFiles().getLast().getDocument();

        if (file != null) {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Guardar Formato A");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
                File fileSaved = fileChooser.showSaveDialog(btnDownloadFormatA.getScene().getWindow());

                if (fileSaved != null) {
                    try (FileOutputStream fos = new FileOutputStream(fileSaved)) {
                        fos.write(file);
                        fos.flush();
                    }
                    System.out.println("Archivo descargado correctamente en: " + fileSaved.getAbsolutePath());
                    Navigation.showAlert("Exito","Se descargo El Formato A correctamente",Alert.AlertType.CONFIRMATION);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No se encontró el archivo en la BD.");
        }
    }

    @FXML
    void eventBtnApprove(ActionEvent event) {
        if(updateProjectAndState("Approved")){
            Navigation.showAlert("Exito", "El FormatoA fue correctamente Aprobado", Alert.AlertType.CONFIRMATION);
        }else{
            Navigation.showAlert("Advertencia", "Debe subir un Formato para su Devolución", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void eventBtnDecline(ActionEvent event) {
        if(updateProjectAndState("Correction")){
            Navigation.showAlert("Exito", "El Formato A fue correctamente Rechazado", Alert.AlertType.CONFIRMATION);
        }else{
            Navigation.showAlert("Advertencia", "Debe subir un Formato para su Devolución", Alert.AlertType.ERROR);
        }
    }

    private boolean updateProjectAndState(String state) {
        try {
            if (devolutionFile == null) {
                return false;
            }

            // Obtener archivos actuales del proyecto
            List<com.unicauca.FrontManagement.entity.File> files = degreeProject.getFiles();

            com.unicauca.FrontManagement.entity.File file = new com.unicauca.FrontManagement.entity.File();

            file.setId(files.getFirst().getId());
            file.setName(devolutionFileName);
            file.setType(EnumFileType.FormatoA);
            file.setVersion(files.getFirst().getVersion());
            file.setDocument(devolutionFile);

            files.set(0, file);
            degreeProject.setFiles(files);

            // Llamar servicio REST
            degreeProjectService.updateProjectAndChanceState(degreeProject, state);
            Navigation.showAlert("Éxito", "El proyecto fue actualizado correctamente", Alert.AlertType.CONFIRMATION);

            // Recargar vista principal
            CoordinatorDashboardController controlador = Navigation.getController("CoordinatorDashboard");
            AnchorPane anchorPaneCentral = controlador.getAnchrPane();
            ListFormatAController ctrl = Navigation.loadInAnchorPane(anchorPaneCentral, "ListFormatA");
            ctrl.setPerson(this.person);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            Navigation.showAlert("Error", "No se pudo actualizar el proyecto: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    @FXML
    void eventBtnUploadCorrection(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de devolución");
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                devolutionFileName = file.getName();
                devolutionFile = Files.readAllBytes(file.toPath());
                System.out.println("Archivo cargado en memoria: " + file.getName());
                Navigation.showAlert("Exito","Se subio el Archivo Correctamente",Alert.AlertType.CONFIRMATION);

            } catch (IOException e) {
                e.printStackTrace();
                Navigation.showAlert("Error", "No se pudo cargar el archivo de devolución.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void eventBtnCancel(ActionEvent event) {
        CoordinatorDashboardController controller = Navigation.getController("CoordinatorDashboard");
        AnchorPane anchorPaneCentral = controller.getAnchrPane();
        Navigation.loadInAnchorPane(anchorPaneCentral, "ListFormatA");
    }

}
