package com.unicauca.FrontManagement.controllers;

import com.unicauca.FrontManagement.entity.*;
import com.unicauca.FrontManagement.services.DegreeProjectService;
import com.unicauca.FrontManagement.utilities.Navigation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListPreliminaryProjectController {

    @FXML
    private ImageView btnSearch;

    @FXML
    private ImageView btnFilter;

    @FXML
    private ComboBox<String> cbxFilters;

    @FXML
    private TableView<DegreeProject> tblDrafts;

    @FXML
    private TableColumn<DegreeProject, String> director;

    @FXML
    private TableColumn<DegreeProject, String> studentEmail;

    @FXML
    private TableColumn<DegreeProject, String> projectTitle;

    @FXML
    private TableColumn<DegreeProject, String> projectType;

    @FXML
    private TableColumn<DegreeProject, Void> evaluate;

    @FXML
    private TextField txtSearch;

    @Autowired
    private DegreeProjectService projectService;

    @Getter @Setter
    private Person person = null;

    @FXML
    public void initialize() {

        director.setCellValueFactory(cellData -> {
            Professor dir = cellData.getValue().getDirector();
            String nombreDir = (dir != null) ? dir.getName() : "Sin director";
            return new javafx.beans.property.SimpleStringProperty(nombreDir);
        });

        studentEmail.setCellValueFactory(cellData -> {
            List<Student> students = cellData.getValue().getStudents();
            if (students == null || students.isEmpty()) {
                return new javafx.beans.property.SimpleStringProperty("Sin estudiante");
            }
            String correos = students.stream()
                    .map(s -> s.getUser() != null ? s.getUser().getEmail() : null)
                    .filter(email -> email != null && !email.isBlank())
                    .collect(Collectors.joining("\n"));
            return new javafx.beans.property.SimpleStringProperty(correos);
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

        evaluate.setCellFactory(param -> new TableCell<>() {
            private final Label btn = new Label("Asignar Evaluadores");

            {
                btn.setStyle("-fx-text-fill: blue; -fx-underline: true; -fx-cursor: hand;");
                btn.setOnMouseClicked(event -> {
                    DegreeProject project = getTableView().getItems().get(getIndex());
                    openEvaluateWindow(project);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        cbxFilters.setItems(FXCollections.observableArrayList(
                "Investigacion",
                "PracticaProfesional"
        ));
        cbxFilters.setPromptText("Modalidad");

        loadDrafts();
    }

    private void loadDrafts() {
        try {
            List<DegreeProject> drafts = projectService.findProjectsByTypeFile("Anteproyecto"); // Aquí cambiamos el tipo
            if(drafts.isEmpty()){
                Navigation.showAlert("No existen proyectos", "No se han encontrado Anteproyecto.", Alert.AlertType.WARNING);
            }

            ObservableList<DegreeProject> data = FXCollections.observableArrayList(drafts);
            tblDrafts.setItems(data);
        } catch (Exception e) {
            System.out.println("Error al cargar anteproyectos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openEvaluateWindow(DegreeProject project) {
        DepartamentHeadDashboardController controller = Navigation.getController("DepartamentHeadDashboard");
        AnchorPane anchorPaneCentral = controller.getAnchrPane();
        //EvaluateDraftController ctrl = Navigation.loadInAnchorPane(anchorPaneCentral, "EvaluateDraft");
        //ctrl.setDegreeProject(project);
    }

    @FXML
    void btnEventFilter() {
        String selectedModality = cbxFilters.getSelectionModel().getSelectedItem();
        try {
            List<DegreeProject> allDrafts = projectService.findProjectsByTypeFile("Anteproyecto");
            List<DegreeProject> filtered = filterByModality(allDrafts, selectedModality);
            tblDrafts.setItems(FXCollections.observableArrayList(filtered));
        } catch (Exception e) {
            System.out.println("Error al filtrar anteproyectos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void searchStudent()  {
        try {
            String correoBuscado = txtSearch.getText().trim();
            List<DegreeProject> allDrafts = projectService.findProjectsByTypeFile("Anteproyecto");
            List<DegreeProject> filtered = filterByEmail(allDrafts, correoBuscado);
            tblDrafts.setItems(FXCollections.observableArrayList(filtered));
        } catch (Exception e) {
            System.out.println("Error al buscar anteproyectos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<DegreeProject> filterByModality(List<DegreeProject> list, String modality) {
        if (modality == null || modality.isBlank()) return list;
        String normalized = modality.trim().toUpperCase();
        return list.stream()
                .filter(project -> project.getModality() != null &&
                        project.getModality().name().equalsIgnoreCase(normalized))
                .collect(Collectors.toList());
    }

    private List<DegreeProject> filterByEmail(List<DegreeProject> list, String filterEmail) {
        if (filterEmail == null || filterEmail.isBlank()) return list;
        String emailFiltro = filterEmail.toLowerCase();
        return list.stream()
                .filter(project -> project.getStudents() != null &&
                        project.getStudents().stream()
                                .anyMatch(student -> student.getUser() != null &&
                                        student.getUser().getEmail() != null &&
                                        student.getUser().getEmail().toLowerCase().contains(emailFiltro)
                                )
                )
                .collect(Collectors.toList());
    }
}
