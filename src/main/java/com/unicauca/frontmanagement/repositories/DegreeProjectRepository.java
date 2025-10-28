package com.unicauca.frontmanagement.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicauca.frontmanagement.entity.*;
import com.unicauca.frontmanagement.infra.dto.DegreeProjectRequest;
import com.unicauca.frontmanagement.infra.dto.FileRequest;
import com.unicauca.frontmanagement.infra.dto.StateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

@Component
public class DegreeProjectRepository implements IDegreeProjectRepository {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    // URL del microservicio de gestión de proyectos
    private final String PROJECT_SERVICE_URL = "http://localhost:8081/api/projectmanagement";

    /**
     * Obtiene la versión más reciente de un archivo de cierto tipo
     * perteneciente al proyecto activo del estudiante con el email dado.
     */
    @Override
    public int findLastFileVersionByStudentEmail(String email, String fileType) throws Exception {
        try {
            String url = PROJECT_SERVICE_URL + "/projectByEmail/" + email;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                return 0;
            }
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {

                // Deserializamos el proyecto desde JSON
                DegreeProject project = objectMapper.readValue(response.getBody(), DegreeProject.class);

                if (project.getFiles() == null || project.getFiles().isEmpty()) {
                    throw new Exception("El proyecto no tiene archivos asociados.");
                }

                // Filtramos solo los archivos del tipo solicitado
                EnumFileType type = EnumFileType.valueOf(fileType);
                List<File> filesOfType = project.getFiles().stream()
                        .filter(f -> f.getType() == type)
                        .toList();

                if (filesOfType.isEmpty()) {
                    throw new Exception("No se encontraron archivos del tipo " + fileType + " en el proyecto.");
                }

                // Obtenemos la versión más alta
                int latestVersion = filesOfType.stream()
                        .map(File::getVersion)
                        .max(Integer::compareTo)
                        .orElse(1); // Por si acaso

                System.out.println("Última versión del archivo " + fileType + " para el estudiante " + email + ": v" + latestVersion);
                return latestVersion;

            } else if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                throw new Exception("No hay proyectos activos para el estudiante con email: " + email);
            } else {
                throw new RuntimeException("Error en el servidor remoto: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.out.println("Error al obtener la versión del archivo: " + e.getMessage());
            throw new Exception("Error al obtener la versión del archivo: " + e.getMessage(), e);
        }
    }
    @Override
    public boolean saveProject(DegreeProject degreeProject) throws Exception{
        // Construir el objeto DegreeProjectRequest que se enviará al microservicio
        DegreeProjectRequest request = new DegreeProjectRequest();
        request.setTitle(degreeProject.getTitle());
        request.setGeneralObjective(degreeProject.getGeneralObjective());
        request.setSpecificObjectives(degreeProject.getSpecificObjectives());
        request.setModality(degreeProject.getModality().toString());

        //Archivos (Formato A y opcionalmente carta)
        List<FileRequest> fileRequests = new ArrayList<>();
        for (File file : degreeProject.getFiles()) {
            FileRequest fileRequest = new FileRequest();
            fileRequest.setName(file.getName());
            fileRequest.setType(file.getType().toString());
            fileRequest.setVersion(file.getVersion());
            fileRequest.setDocument(Base64.getEncoder().encodeToString(file.getDocument()));
            fileRequests.add(fileRequest);
        }
        request.setFiles(fileRequests);

        List<Long> studentsId = new ArrayList<>();
        for (Student s : degreeProject.getStudents()) {
            studentsId.add(s.getId());
        }
        request.setStudentsId(studentsId);

        if (degreeProject.getDirector() != null) {
            request.setDirectorId(degreeProject.getDirector().getId());
        }


        List<Long> codirectorsId = new ArrayList<>();
        if (degreeProject.getCodirectors() != null) {
            for (Professor c : degreeProject.getCodirectors()) {
                codirectorsId.add(c.getId());
            }
        }
        request.setCodirectorsId(codirectorsId);

        // Estado inicial del proyecto (primer estado del patrón State)
        StateRequest state = new StateRequest();
        state.setName("FirstReviewFormatA");
        request.setState(state);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = null;

        response = restTemplate.postForEntity(
                PROJECT_SERVICE_URL + "/SaveProject",
                new HttpEntity<>(request, headers),
                String.class
        );

        if (response != null && response.getStatusCode() == HttpStatus.CREATED) {
            System.out.println("User created successfully: " + response.getBody());
            return true;
        } else {
            throw new RuntimeException("Error creating user: " +
                    (response != null ? response.getStatusCode() : "No response from server"));
        }

    }

}
