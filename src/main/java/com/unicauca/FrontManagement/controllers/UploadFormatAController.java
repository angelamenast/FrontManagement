package com.unicauca.FrontManagement.controllers;
import com.unicauca.FrontManagement.entity.*;
import com.unicauca.FrontManagement.services.DegreeProjectService;
import com.unicauca.FrontManagement.services.PersonService;
import com.unicauca.FrontManagement.services.ProfessorService;
import com.unicauca.FrontManagement.services.StudentService;
import com.unicauca.FrontManagement.utilities.Navigation;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


@Component
public class UploadFormatAController implements Initializable {

    @FXML
    private Button btnLetter;

    @FXML
    private Button btnFormat;

    @FXML
    private Button btnUpload;

    @FXML
    private DatePicker dtPckrDate;

    @FXML
    private ImageView imgLetter;

    @FXML
    private ImageView imgFormat;

    @FXML
    private RadioButton rdBtnIP;

    @FXML
    private RadioButton rdBtnPP;

    @FXML
    private ComboBox<Professor> cbxDirector;  // Cambio

    @FXML
    private ComboBox<Professor> cbxCodirector; // Cambio

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtEmail2;

    @FXML
    private TextArea txtSpecificObjectives;

    @FXML
    private TextField txtGeneralObjective;

    @FXML
    private TextField txtTitle;

    @FXML
    private VBox vBoxEstudiante2;


    @Autowired
    private DegreeProjectService projectService;
    @Autowired
    private ProfessorService professorService;
    @Autowired
    private StudentService studentService;


    @Getter
    @Setter
    private Person person = null;

    private File fileFormatA = null;
    private File fileLetter = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.imgLetter.setVisible(false);
        this.btnLetter.setVisible(false);
        this.vBoxEstudiante2.setVisible(false);

        try {
            // Cargar los profesores desde el servicio
            List<Professor> professors = professorService.listProfessors();

            // Asignar lista completa al ComboBox (no los nombres)
            cbxDirector.setItems(FXCollections.observableArrayList(professors));
            cbxCodirector.setItems(FXCollections.observableArrayList(professors));

            // Configurar cómo se muestran los nombres en la lista desplegable
            cbxDirector.setCellFactory(param -> new ListCell<Professor>() {
                @Override
                protected void updateItem(Professor prof, boolean empty) {
                    super.updateItem(prof, empty);
                    if (empty || prof == null) {
                        setText(null);
                    } else {
                        setText(prof.getName() + " " + prof.getLastName());
                    }
                }
            });

            cbxCodirector.setCellFactory(param -> new ListCell<Professor>() {
                @Override
                protected void updateItem(Professor prof, boolean empty) {
                    super.updateItem(prof, empty);
                    if (empty || prof == null) {
                        setText(null);
                    } else {
                        setText(prof.getName() + " " + prof.getLastName());
                    }
                }
            });

            // Configurar cómo se muestra el profesor seleccionado (texto en el campo)
            cbxDirector.setConverter(new javafx.util.StringConverter<Professor>() {
                @Override
                public String toString(Professor prof) {
                    return (prof == null) ? "" : prof.getName() + " " + prof.getLastName();
                }

                @Override
                public Professor fromString(String string) {
                    return cbxDirector.getItems().stream()
                            .filter(p -> (p.getName() + " " + p.getLastName()).equals(string))
                            .findFirst().orElse(null);
                }
            });

            cbxCodirector.setConverter(new javafx.util.StringConverter<Professor>() {
                @Override
                public String toString(Professor prof) {
                    return (prof == null) ? "" : prof.getName() + " " + prof.getLastName();
                }

                @Override
                public Professor fromString(String string) {
                    return cbxCodirector.getItems().stream()
                            .filter(p -> (p.getName() + " " + p.getLastName()).equals(string))
                            .findFirst().orElse(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Navigation.showAlert("Error", "Error al cargar profesores: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private boolean canUploadProject(String email) {
        int version = 1;
        try{
            version = projectService.findLastFileVersionByStudentEmail(email, "FormatoA");
        }
        catch(Exception e){
            System.out.println("Error finding last file version: " + e.getMessage());
        }
        return version <= 0;
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
                fileFormatA = new File();
                fileFormatA.setType(EnumFileType.FormatoA);
                fileFormatA.setVersion(1);
                fileFormatA.setName(selectedFile.getName());
                fileFormatA.setDocument(Files.readAllBytes(selectedFile.toPath()));
            } catch (IOException e) {
                System.out.println("Error trying to upload file : " + e.getMessage());
            }

        }
    }

    @FXML
    void eventClickBtnLetter() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Carta de Recomendación (PDF)");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf")
        );

        java.io.File selectedFile = fileChooser.showOpenDialog(btnFormat.getScene().getWindow());

        if (selectedFile != null) {
            try {
                fileLetter = new File();
                fileLetter.setType(EnumFileType.CartaEmpresa);
                fileLetter.setVersion(1);
                fileLetter.setName(selectedFile.getName());
                fileLetter.setDocument(Files.readAllBytes(selectedFile.toPath()));
            } catch (IOException e) {
                System.out.println("Error trying to upload file : " + e.getMessage());
            }
        }
    }

    @FXML
    void eventClickBtnUpload() {
        DegreeProject degreeProject = getDegreeData();
        if (degreeProject == null) {
            System.out.println("Error getting data from format A.");
            return;
        }

        String email1 = txtEmail.getText().trim();
        String email2 = (rdBtnIP.isSelected() ? txtEmail2.getText().trim() : null);

        // Validar si los estudiantes pueden iniciar el proceso en la modalidad seleccionada

        if (!canUploadProject(email1)) {
            Navigation.showAlert("Warning", "The student asociated with " + email1 + " Cant init a new process in this modality.", Alert.AlertType.WARNING);
            return;
        }

        if (email2 != null && !email2.isBlank() && !canUploadProject(email2)) {
            Navigation.showAlert("Warning", "The student asociated with " + email2 + " Cant init a new process in this modality.", Alert.AlertType.WARNING);
            return;
        }

        if (rdBtnIP.isSelected() && fileFormatA != null) {
            try {
                // Enviar al microservicio de proyectos (por ejemplo, mediante tu DegreeProjectService)
                projectService.saveProject(degreeProject);

                Navigation.showAlert("Éxito", "Proyecto de grado subido correctamente.", Alert.AlertType.INFORMATION);

            } catch (Exception e) {
                Navigation.showAlert("Error", "Error al subir el proyecto: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
        else if (rdBtnPP.isSelected() && fileFormatA != null && fileLetter != null) {
            try {
                //  Enviar al microservicio de proyectos (por ejemplo, mediante tu DegreeProjectService)
                projectService.saveProject(degreeProject);

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
    }


    private DegreeProject getDegreeData() {
        try{

            DegreeProject degreeProject = new DegreeProject();

            String title = txtTitle.getText();
            EnumModality modality = rdBtnIP.isSelected() ? EnumModality.ProyectoInvestigacion : EnumModality.PracticaProfesional;
            LocalDate date = dtPckrDate.getValue();

            Professor director = cbxDirector.getValue();
            Professor codirector = cbxCodirector.getValue();

            Student student = studentService.obtainUserByEmail(txtEmail.getText());
            System.out.println(student.getName());
            Student student2 = null;
            if (txtEmail2.getText() != null && !txtEmail2.getText().isBlank() && rdBtnIP.isSelected()) {
                student2 = studentService.obtainUserByEmail(txtEmail2.getText());
            }

            String generalObjective = txtGeneralObjective.getText();
            List<String> specificObjectives = Arrays.stream(txtSpecificObjectives.getText().split("\\R"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            if (title.isBlank() || date == null || director == null || codirector == null || student == null || generalObjective.isBlank() || specificObjectives.isEmpty()) {
                System.out.println("There are empty fields");
                return null;
            } else if(specificObjectives.size() > 4){
                System.out.println("There are more of than 4 specific objectives");
                return null;
            }

            //Asignar toda la informacion a degreeProject

            degreeProject.setTitle(title);
            degreeProject.setGeneralObjective(generalObjective);
            degreeProject.setSpecificObjectives(specificObjectives);
            degreeProject.setModality(modality);

            List<File> files = new ArrayList<>();
            fileFormatA.setDate(date);
            files.add(fileFormatA);
            if(fileLetter != null){
                fileLetter.setDate(date);
            }
            if(modality == EnumModality.PracticaProfesional) {
                files.add(fileLetter);
            }

            List<Student> students = new ArrayList<>();
            students.add(student);
            if(student2 != null) {
                students.add(student2);
            }

            degreeProject.setFiles(files);
            degreeProject.setStudents(students);
            degreeProject.setDirector(director);

            List<Professor> codirectors = new ArrayList<>();
            codirectors.add(codirector);

            degreeProject.setCodirectors(codirectors);

            State state= new State();
            state.setName("FirstReviewFormatA");
            degreeProject.setState(state);

            return degreeProject;

        }catch(Exception e){
            System.out.println("Error getting information from FormatA : " + e.getMessage());
            return null;
        }

    }

    private void cleanFields() {
        txtTitle.setText("");
        txtEmail.setText("");
        txtEmail2.setText("");
        rdBtnIP.setSelected(false);
        rdBtnPP.setSelected(false);
        dtPckrDate.setValue(null);
        cbxDirector.setValue(null);
        cbxCodirector.setValue(null);
        txtGeneralObjective.setText("");
        txtSpecificObjectives.setText("");
        imgLetter.setVisible(false);
        btnLetter.setVisible(false);
        vBoxEstudiante2.setVisible(false);
        fileFormatA = null;
        fileLetter = null;

    }

    @FXML
    void eventClickRdBtnIP(ActionEvent event) {
        if (rdBtnIP.isSelected()) {
            this.rdBtnPP.setSelected(false);
            this.imgLetter.setVisible(false);
            this.btnLetter.setVisible(false);
            this.vBoxEstudiante2.setVisible(true);
        } else {
            this.vBoxEstudiante2.setVisible(false);
        }
    }

    @FXML
    void eventClickRdBtnPP(ActionEvent event) {
        if (rdBtnPP.isSelected()) {
            this.rdBtnIP.setSelected(false);
            this.imgLetter.setVisible(true);
            this.btnLetter.setVisible(true);
            this.vBoxEstudiante2.setVisible(false);
        } else {
            this.imgLetter.setVisible(false);
            this.btnLetter.setVisible(false);
        }
    }
}
