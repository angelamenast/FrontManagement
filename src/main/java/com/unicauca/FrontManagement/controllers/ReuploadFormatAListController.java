package com.unicauca.FrontManagement.controllers;

import com.unicauca.FrontManagement.entity.DegreeProject;
import com.unicauca.FrontManagement.entity.Person;
import com.unicauca.FrontManagement.entity.Student;
import com.unicauca.FrontManagement.repositories.IDegreeProjectRepository;
import com.unicauca.FrontManagement.services.DegreeProjectService;
import com.unicauca.FrontManagement.utilities.Navigation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
public class ReuploadFormatAListController implements Initializable {

    @FXML
    private TableView<DegreeProject> tblFormats;

    @FXML
    private TableColumn<DegreeProject, String> projectTitle;

    @FXML
    private TableColumn<DegreeProject, String> emailStudent1;

    @FXML
    private TableColumn<DegreeProject, String> emailStudent2;

    @FXML
    private TableColumn<DegreeProject, String> projectType;

    @FXML
    private TableColumn<DegreeProject, Void> reuploadFormat;

    @FXML
    private TableColumn<DegreeProject, Void> formatFile;

    @FXML
    private ComboBox<String> cbxFilters;

    @FXML
    private TextField txtSearch;

    @Autowired
    private DegreeProjectService projectService;

    private Person person = null;
    private Long idProfesor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        emailStudent1.setCellValueFactory(cellData -> {
            List<Student> students = cellData.getValue().getStudents();

            if (students == null || students.isEmpty()) {
                return new javafx.beans.property.SimpleStringProperty("Sin estudiantes");
            }

            Student student = students.getFirst();
            String email = student.getUser().getEmail();

            return new javafx.beans.property.SimpleStringProperty(email);
        });

        emailStudent2.setCellValueFactory(cellData -> {
            List<Student> students = cellData.getValue().getStudents();

            if (students == null || students.isEmpty()) {
                return new javafx.beans.property.SimpleStringProperty("Sin estudiantes");
            }

            if (students.size() == 1) {
                return new javafx.beans.property.SimpleStringProperty("");
            }

            Student student = students.getLast();
            String email = student.getUser().getEmail();

            return new javafx.beans.property.SimpleStringProperty(email);
        });

        projectTitle.setCellValueFactory(cellData -> {
            String title = (cellData.getValue().getTitle() != null && !cellData.getValue().getTitle().isBlank())
                    ? cellData.getValue().getTitle()
                    : "Sin título";
            return new javafx.beans.property.SimpleStringProperty(title);
        });

        projectType.setCellValueFactory(cellData -> {
            String modality = (cellData.getValue().getModality() != null)
                    ? cellData.getValue().getModality().toString()
                    : "Sin tipo";
            return new javafx.beans.property.SimpleStringProperty(modality);
        });

        cbxFilters.setItems(FXCollections.observableArrayList(
                "Investigacion",
                "PracticaProfesional"
        ));
        cbxFilters.setPromptText("Modalidad");

        formatFile.setCellFactory(param -> new TableCell<>() {
            private final Label link = new Label("Descargar");

            {
                link.setStyle("-fx-text-fill: blue; -fx-underline: true; -fx-cursor: hand;");
                link.setOnMouseClicked(event -> {
                    DegreeProject devolutionProject = getTableView().getItems().get(getIndex());
                    downloadFile(devolutionProject);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(link);
                }
            }
        });

        reuploadFormat.setCellFactory(param -> new TableCell<>() {
            private final Label btn = new Label("Resubir");

            {
                btn.setStyle("-fx-text-fill: blue; -fx-underline: true; -fx-cursor: hand;");
                btn.setOnMouseClicked(event -> {
                    DegreeProject devolutionProject = getTableView().getItems().get(getIndex());
                    openWindowReupload(devolutionProject);
                });
            }

            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

    }

    private void openWindowReupload(DegreeProject project) {
        ProfessorDashboardController controller = Navigation.getController("ProfessorDashboard");
        AnchorPane anchorCenterPane = controller.getAnchrPane();

        ReuploadFormatController ctrl = Navigation.loadInAnchorPane(anchorCenterPane, "ReuploadFormatA");

        ctrl.setDegreeProject(project, person);

    }

    private List<DegreeProject> filterByModality(List<DegreeProject> list, String modality) {
        if (modality == null || modality.isBlank()) {
            return list;
        }

        String normalized = modality.trim().toUpperCase();

        return list.stream()
                .filter(project ->
                        project.getModality() != null &&
                                project.getModality().name().equalsIgnoreCase(normalized)
                )
                .collect(Collectors.toList());
    }


    private List<DegreeProject> filterByEmail(List<DegreeProject> list, String filterEmail) {
        if (filterEmail == null || filterEmail.isBlank()) {
            return list;
        }

        String emailFiltro = filterEmail.toLowerCase();
        return list.stream()
                .filter(project ->
                        project.getStudents() != null &&
                                project.getStudents().stream()
                                        .anyMatch(student ->
                                                student.getUser() != null &&
                                                        student.getUser().getEmail() != null &&
                                                        student.getUser().getEmail().toLowerCase().contains(emailFiltro)
                                        )
                )
                .collect(Collectors.toList());
    }

    private void loadProjects () {
        try {
            // Llama al servicio para traer los proyectos con archivos del tipo deseado
            List<DegreeProject> projects = projectService.findProjectsByTypeFile("FormatoA");

            List<DegreeProject> filteredProjects = new ArrayList<>();

            for(DegreeProject project : projects){
                if((project.getState().getName().equals("FirstCorrectionFormatA") || project.getState().getName().equals("SecondCorrectionFormatA")) && Objects.equals(project.getDirector().getId(), this.idProfesor)){
                    filteredProjects.add(project);
                }
            }

            ObservableList<DegreeProject> data = FXCollections.observableArrayList(filteredProjects);
            tblFormats.setItems(data);

        } catch (Exception e) {
            System.out.println("Error al cargar proyectos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void btnEventFilter(MouseEvent event) {
        String selectedModality = cbxFilters.getSelectionModel().getSelectedItem();

        try {
            // Trae todos los proyectos con archivos FormatoA
            List<DegreeProject> allProjects = projectService.findProjectsByTypeFile("FormatoA");

            // Si se seleccionó una modalidad, se filtra
            List<DegreeProject> filtered = filterByModality(allProjects, selectedModality);

            // Muestra los resultados filtrados en la tabla
            tblFormats.setItems(FXCollections.observableArrayList(filtered));

        } catch (Exception e) {
            System.out.println("Error al filtrar proyectos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void searchStudent(MouseEvent event) {
        try {
            String emailSearched = txtSearch.getText().trim();

            List<DegreeProject> allProjects = projectService.findProjectsByTypeFile("FormatoA");

            List<DegreeProject> filtered = filterByEmail(allProjects, emailSearched);

            tblFormats.setItems(FXCollections.observableArrayList(filtered));

        } catch (Exception e) {
            System.out.println("Error al buscar por correo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void downloadFile(DegreeProject devolutionProject) {
        if (devolutionProject.getFiles().getFirst() != null) {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Guardar devolución");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
                fileChooser.setInitialFileName("Devolucion_" + devolutionProject.getFiles().getFirst().getName() + ".pdf");

                File file = fileChooser.showSaveDialog(tblFormats.getScene().getWindow());

                if (file != null) {
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        fos.write(devolutionProject.getFiles().getFirst().getDocument());
                        fos.flush();
                    }
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Archivo descargado en: " + file.getAbsolutePath());
                    alert.showAndWait();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error al guardar el archivo");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No hay archivo disponible para esta devolución");
            alert.showAndWait();
        }
    }

    public void setPerson(Person person) {
        this.person = person;
        this.idProfesor = person.getId();
        loadProjects();
    }
}
