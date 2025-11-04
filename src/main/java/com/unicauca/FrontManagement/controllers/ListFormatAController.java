package com.unicauca.FrontManagement.controllers;

import com.unicauca.FrontManagement.entity.*;
import com.unicauca.FrontManagement.entity.decorator.PriorityDegreeProject;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListFormatAController {

    @FXML
    private ImageView btnSearch;

    @FXML
    private ImageView btnFilter;

    @FXML
    private ComboBox<String> cbxFilters;

    @FXML
    private TableView<DegreeProject> tblFormats;

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

    @FXML
    private ImageView btnPriorityFilter;

    @FXML
    private ComboBox<String> cbxPriorityFilters;

    @FXML
    private TableView<PriorityDegreeProject> tblPriorityFormats;

    @FXML
    private TableColumn<PriorityDegreeProject, String> directorPriority;

    @FXML
    private TableColumn<PriorityDegreeProject, String> projectTitlePriority;

    @FXML
    private TableColumn<PriorityDegreeProject, String> projectTypePriority;

    @FXML
    private TableColumn<PriorityDegreeProject, String> studentEmailPriority;

    @FXML
    private TableColumn<PriorityDegreeProject, Void> evaluatePriority;


    @Autowired
    private DegreeProjectService projectService;

    @Getter @Setter
    private Person person = null;

    private boolean priorityTableInitialized = false;

    @FXML
    public void initialize() {
        priorityTableInitialized = false;

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

        // evaluate para tabla normal (abre ventana)
        evaluate.setCellFactory(param -> new TableCell<>() {
            private final Label btn = new Label("Evaluar");

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
                "PracticaProfesional",
                "Todos"
        ));
        cbxFilters.setPromptText("Modalidad");

        cbxPriorityFilters.setItems(FXCollections.observableArrayList(
                "Prioridad alta",
                "Todos"
        ));
        cbxPriorityFilters.setPromptText("Filtro prioridad");

        // Al inicio la tabla de prioridades está oculta
        tblPriorityFormats.setVisible(false);

        // Carga inicial de la tabla normal
        loadFormatA();
    }

    private List<DegreeProject> filterByModality(List<DegreeProject> list, String modality) {
        if (modality == null || modality.isBlank() || modality.equals("Todos")) {
            return list;
        }

        String normalized = "";
        switch (modality) {
            case "PracticaProfesional":
                normalized = "PracticaProfesional";
                break;
            case "Investigacion":
                normalized = "ProyectoInvestigacion";
                break;
        }

        String finalNormalized = normalized;
        return list.stream()
                .filter(project ->
                        project.getModality() != null &&
                                project.getModality().name().equalsIgnoreCase(finalNormalized)
                )
                .collect(Collectors.toList());
    }

    private List<PriorityDegreeProject> filterByPriority(List<DegreeProject> list, String priorityFilter) {

        if (priorityFilter == null || priorityFilter.isBlank() || priorityFilter.equals("Prioridad normal")) {
            return new ArrayList<>();
        }

        List<PriorityDegreeProject> finalList = new ArrayList<>();

        LocalDate cutoff = LocalDate.of(2025, 11, 1);

        for (DegreeProject project : list) {
            if (project.getFiles() == null) continue;
            for (File file : project.getFiles()) {
                if (file != null && file.getDate() != null && file.getDate().isBefore(cutoff)) {
                    PriorityDegreeProject p = new PriorityDegreeProject(project);
                    finalList.add(p);
                    break; // si ya cumple, no necesitamos revisar más archivos de este proyecto
                }
            }
        }

        return finalList;
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

    private void loadFormatA() {
        try {
            List<DegreeProject> projects = projectService.findProjectsByTypeFile("FormatoA");

            List<DegreeProject> filteredProjects = new ArrayList<>();

            for(DegreeProject project : projects){
                if(project.getState() != null &&
                        (project.getState().getName().equals("ThirdReviewFormatA")
                                || project.getState().getName().equals("FirstReviewFormatA")
                                || project.getState().getName().equals("SecondReviewFormatA"))){
                    filteredProjects.add(project);
                }
            }
            ObservableList<DegreeProject> data = FXCollections.observableArrayList(filteredProjects);
            tblFormats.setItems(data);

            tblFormats.setVisible(true);
            tblPriorityFormats.setVisible(false);

        } catch (Exception e) {
            System.out.println("Error al cargar proyectos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openEvaluateWindow(DegreeProject project) {
        CoordinatorDashboardController controller = Navigation.getController("CoordinatorDashboard");
        AnchorPane anchorCenterPane = controller.getAnchrPane();

        EvaluateFormatAController ctrl = Navigation.loadInAnchorPane(anchorCenterPane, "EvaluateFormatA");

        ctrl.setDegreeProject(project);
    }

    @FXML
    void btnEventFilter() {
        String selectedModality = cbxFilters.getSelectionModel().getSelectedItem();

        try {
            List<DegreeProject> allProjects = projectService.findProjectsByTypeFile("FormatoA");

            List<DegreeProject> filtered = filterByModality(allProjects, selectedModality);

            List<DegreeProject> filteredProjects = new ArrayList<>();

            for(DegreeProject project : filtered){
                if(project.getState() != null &&
                        (project.getState().getName().equals("ThirdReviewFormatA")
                                || project.getState().getName().equals("FirstReviewFormatA")
                                || project.getState().getName().equals("SecondReviewFormatA"))){
                    filteredProjects.add(project);
                }
            }

            tblFormats.setItems(FXCollections.observableArrayList(filteredProjects));
            tblFormats.setVisible(true);
            tblPriorityFormats.setVisible(false);

        } catch (Exception e) {
            System.out.println("Error al filtrar proyectos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void btnPriorityFilter() {
        try{
            String selectedPriority = cbxPriorityFilters.getSelectionModel().getSelectedItem();

            List<DegreeProject> allProjects = projectService.findProjectsByTypeFile("FormatoA");

            List<PriorityDegreeProject> filtered = filterByPriority(allProjects, selectedPriority);

            List<PriorityDegreeProject> filteredProjects = new ArrayList<>();

            for(PriorityDegreeProject project : filtered){

                DegreeProject original;

                try {
                    original = project.getOriginalProject();
                } catch (NoSuchMethodError | AbstractMethodError ex) {

                    original = (DegreeProject) project;
                } catch (Exception ex) {

                    original = (DegreeProject) project;
                }

                if(original.getState() != null &&
                        (original.getState().getName().equals("ThirdReviewFormatA")
                                || original.getState().getName().equals("FirstReviewFormatA")
                                || original.getState().getName().equals("SecondReviewFormatA"))){
                    filteredProjects.add(project);
                }
            }

            if (filteredProjects.isEmpty()) {
                loadFormatA();
                return;
            }

            initPriorityTableColumns();

            tblFormats.setVisible(false);
            tblPriorityFormats.setVisible(true);

            tblPriorityFormats.setItems(FXCollections.observableArrayList(filteredProjects));

        }catch(Exception e){
            System.out.println("Error al filtrar proyectos por prioridad: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @FXML
    void searchStudent()  {
        try {
            String emailSearched = txtSearch.getText().trim();

            List<DegreeProject> allProjects = projectService.findProjectsByTypeFile("FormatoA");

            List<DegreeProject> filtered = filterByEmail(allProjects, emailSearched);

            tblFormats.setItems(FXCollections.observableArrayList(filtered));
            tblFormats.setVisible(true);
            tblPriorityFormats.setVisible(false);

        } catch (Exception e) {
            System.out.println("Error al buscar por correo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initPriorityTableColumns() {
        if (priorityTableInitialized) return;
        priorityTableInitialized = true;

        directorPriority.setCellValueFactory(cellData -> {
            PriorityDegreeProject p = cellData.getValue();
            DegreeProject original = null;
            try {
                original = p.getOriginalProject();
            } catch (Exception ex) {
                // si no existe getOriginalProject(), asumimos p es también DegreeProject
                original = (DegreeProject) p;
            }
            Professor dir = original.getDirector();
            String nombreDir = (dir != null) ? dir.getName() : "Sin director";
            return new javafx.beans.property.SimpleStringProperty(nombreDir);
        });

        studentEmailPriority.setCellValueFactory(cellData -> {
            PriorityDegreeProject p = cellData.getValue();
            DegreeProject original;
            try {
                original = p.getOriginalProject();
            } catch (Exception ex) {
                original = (DegreeProject) p;
            }

            List<Student> students = original.getStudents();
            if (students == null || students.isEmpty()) {
                return new javafx.beans.property.SimpleStringProperty("Sin estudiante");
            }

            String correos = students.stream()
                    .map(s -> s.getUser() != null ? s.getUser().getEmail() : null)
                    .filter(email -> email != null && !email.isBlank())
                    .collect(Collectors.joining("\n"));

            return new javafx.beans.property.SimpleStringProperty(correos);
        });

        projectTitlePriority.setCellValueFactory(cellData -> {
            PriorityDegreeProject p = cellData.getValue();


            String title = (p.getTitle() != null && !p.getTitle().isBlank())
                    ? p.getTitle()
                    : "Sin título";
            return new javafx.beans.property.SimpleStringProperty(title);
        });

        projectTypePriority.setCellValueFactory(cellData -> {
            PriorityDegreeProject p = cellData.getValue();
            DegreeProject original;
            try {
                original = p.getOriginalProject();
            } catch (Exception ex) {
                original = (DegreeProject) p;
            }

            String modality = (original.getModality() != null)
                    ? original.getModality().toString()
                    : "Sin tipo";
            return new javafx.beans.property.SimpleStringProperty(modality);
        });

        // evaluatePriority: debe abrir la misma ventana que evaluate
        evaluatePriority.setCellFactory(param -> new TableCell<>() {
            private final Label btn = new Label("Evaluar");

            {
                btn.setStyle("-fx-text-fill: blue; -fx-underline: true; -fx-cursor: hand;");
                btn.setOnMouseClicked(event -> {
                    PriorityDegreeProject p = getTableView().getItems().get(getIndex());
                    DegreeProject original;
                    try {
                        original = p.getOriginalProject();
                    } catch (Exception ex) {
                        original = (DegreeProject) p;
                    }
                    openEvaluateWindow(original);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

}
