


package com.unicauca.FrontManagement.controllers;

import com.unicauca.FrontManagement.entity.*;
import com.unicauca.FrontManagement.services.DegreeProjectService;
import com.unicauca.FrontManagement.entity.Person;
import com.unicauca.FrontManagement.services.PersonService;
import com.unicauca.FrontManagement.services.ProfessorService;
import com.unicauca.FrontManagement.services.StudentService;
import com.unicauca.FrontManagement.utilities.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import jdk.dynalink.NamedOperation;
import jdk.swing.interop.SwingInterOpUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


@Component
public class ReuploadFormatController implements Initializable {

    @FXML
    private Button btnCancel, btnFormat, btnUpload;
    @FXML
    private DatePicker dtPckrDate;
    @FXML
    private Label lblDirector, lblCodirector, lblModality, lblAttempt;
    @FXML
    private Label lblEmailStudent1, lblEmailStudent2;
    @FXML
    private TextField txtTitle, txtGeneralObjective;
    @FXML
    private TextArea txtSpecificObjectives;
    @FXML
    private VBox vBoxStudent2;

    @Autowired
    private PersonService personService;

    @Autowired
    private DegreeProjectService projectService;

    private Person person;
    private DegreeProject originalProject;
    private File formatFile = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    /*
    private boolean canReuploadProject() {
        int version = 1;
        try {
            version = this.originalProject.getFiles().getFirst().getVersion();
        } catch (Exception e) {
            System.out.println("Error finding last file version: " + e.getMessage());
        }
        return version <= 3;
    }
*/
    @FXML
    void eventClickBtnCancel() {
        returnPastScreen();
    }

    public void setDegreeProject(DegreeProject originalProject, Person person) {
        this.originalProject = originalProject;
        this.person = person;

        try {

            txtTitle.setText(originalProject.getTitle());
            txtGeneralObjective.setText(originalProject.getGeneralObjective());
            txtSpecificObjectives.setText(originalProject.getSpecificObjectives().getLast());
            lblDirector.setText(originalProject.getDirector().getName());
            lblCodirector.setText(originalProject.getCodirectors().getFirst() != null ? originalProject.getCodirectors().getFirst().getName() : "N/A");
            lblModality.setText(originalProject.getModality().toString());
            lblAttempt.setText(String.valueOf(originalProject.getFiles().getLast().getVersion() + 1));
            lblEmailStudent1.setText(originalProject.getStudents().get(0).getUser().getEmail());
            lblEmailStudent2.setText(originalProject.getStudents().size() > 1 ? originalProject.getStudents().get(1).getUser().getEmail() : "N/A");
            dtPckrDate.setValue(originalProject.getFiles().getFirst().getDate());

            vBoxStudent2.setVisible(originalProject.getStudents().size() > 1);
        } catch (Exception e) {
            Navigation.showAlert("Error", "No se pudo cargar el proyecto: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void eventClickBtnFormat() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Formato A (PDF)");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf")
        );

        java.io.File selectedFile = fileChooser.showOpenDialog(btnFormat.getScene().getWindow());

        if (selectedFile != null) {
            try {
                formatFile = new File();
                formatFile.setId(originalProject.getFiles().getFirst().getId());
                formatFile.setType(EnumFileType.FormatoA);
                formatFile.setVersion(originalProject.getFiles().getFirst().getVersion() + 1);
                formatFile.setName(selectedFile.getName());
                formatFile.setDocument(Files.readAllBytes(selectedFile.toPath()));
            } catch (IOException e) {
                System.out.println("Error trying to upload file : " + e.getMessage());
            }

        }
    }

    private DegreeProject getDegreeData() {
        String title = txtTitle.getText();
        LocalDate date = dtPckrDate.getValue();
        String generalObjective = txtGeneralObjective.getText();
        String specificObjectives = txtSpecificObjectives.getText();

        if (title.isBlank() || date == null || generalObjective.isBlank() || specificObjectives.isBlank()) {
            System.out.println("Faltan datos obligatorios.");
            return null;
        }

        DegreeProject degreeProject = this.originalProject;
        degreeProject.setTitle(title);
        degreeProject.setGeneralObjective(generalObjective);
        degreeProject.setSpecificObjectives(List.of(specificObjectives));

        List<File> files = new ArrayList<>();

        formatFile.setDate(date);
        files.add(formatFile);

        for (File file : degreeProject.getFiles()) {
            if(!file.getType().equals(EnumFileType.FormatoA)){
                file.setDate(date);
                files.add(file);
            }
        }

        degreeProject.setFiles(files);

        return degreeProject;
    }

    @FXML
    void eventClickBtnUpload() {
        DegreeProject degreeProject = getDegreeData();

        if (degreeProject == null) {
            System.out.println("Error getting data from format A.");
            return;
        }

        if (formatFile != null) {
            try {
                // Enviar al microservicio de proyectos (por ejemplo, mediante tu DegreeProjectService)
                projectService.reuploadProject(degreeProject);
                Navigation.showAlert("Éxito", "Proyecto de grado subido correctamente.", Alert.AlertType.INFORMATION);

            } catch (Exception e) {
                Navigation.showAlert("Error", "Error al subir el proyecto: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            System.out.println("Any file was selected.");
            return;
        }
        cleanFields();

        returnPastScreen();

    }

    public void returnPastScreen(){
        ProfessorDashboardController controller = Navigation.getController("ProfessorDashboard");
        AnchorPane anchorPaneCentral = controller.getAnchrPane();
        ReuploadFormatAListController controllerFormatAList = Navigation.loadInAnchorPane(anchorPaneCentral, "ReuploadFormatAList");
        controllerFormatAList.setPerson(this.person);
    }

    private void cleanFields() {
        txtTitle.setText("");
        lblModality.setText("");
        lblDirector.setText("");
        lblCodirector.setText("");
        lblAttempt.setText("");
        lblEmailStudent1.setText("");
        lblEmailStudent2.setText("");
    }


}

