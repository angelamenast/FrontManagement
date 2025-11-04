    package com.unicauca.FrontManagement.repositories;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.unicauca.FrontManagement.entity.*;
    import com.unicauca.FrontManagement.infra.dto.*;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.*;
    import org.springframework.stereotype.Component;
    import org.springframework.web.client.RestTemplate;

    import java.time.LocalDate;
    import java.util.*;

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
        public List<DegreeProject> findProjectsByTypeFile(String fileType) throws Exception {
            try {
                String url = PROJECT_SERVICE_URL + "/projectsByFileType/" + fileType;

                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                    System.out.println("No hay proyectos registrados con tipo de archivo: " + fileType);
                    return new ArrayList<>();
                }

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    String json = response.getBody();

                    // Aquí reacomodamos el JSON antes de deserializarlo
                    json = json.replaceAll("\"email\"\\s*:", "\"user\": {\"email\":")
                            .replaceAll(",\\s*\"password\"", "}, \"password\"");

                    // Luego deserializamos normalmente
                    List<DegreeProject> projects = objectMapper.readValue(
                            json,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, DegreeProject.class)
                    );

                    System.out.println("Se recibieron " + projects.size() + " proyectos con archivo tipo: " + fileType);
                    return projects;
                } else {
                    throw new RuntimeException("Error al consultar proyectos: " + response.getStatusCode());
                }

            } catch (Exception e) {
                System.out.println("Error al obtener proyectos por tipo de archivo: " + e.getMessage());
                throw new Exception("Error al obtener proyectos por tipo de archivo: " + e.getMessage(), e);
            }
        }

        @Override
        public void savePreliminaryProject(Long projectId, File file) throws Exception {
            try {
                // ---- Construimos el FileEvent ----
                FileEvent fileEvent = new FileEvent();
                fileEvent.setName(file.getName());
                fileEvent.setType("Anteproyecto");
                fileEvent.setVersion(file.getVersion());
                fileEvent.setDocument(Base64.getEncoder().encodeToString(file.getDocument()));
                fileEvent.setDate(file.getDate() != null ? file.getDate().toString() : LocalDate.now().toString());

                // ---- Construimos el JSON ----
                Map<String, Object> payload = new HashMap<>();
                payload.put("projectId", projectId);
                payload.put("file", fileEvent);

                // ---- Configuramos headers y cuerpo ----
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(payload);
                HttpEntity<String> entity = new HttpEntity<>(json, headers);

                // ---- Petición POST ----
                String url = PROJECT_SERVICE_URL + "/uploadPreliminaryProject";
                ResponseEntity<String> response = restTemplate.exchange(
                        url, HttpMethod.POST, entity, String.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    System.out.println("Anteproyecto subido correctamente.");
                } else {
                    throw new RuntimeException("Error al subir anteproyecto: " + response.getStatusCode());
                }

            } catch (Exception e) {
                System.err.println(" Error al subir el anteproyecto: " + e.getMessage());
                throw new Exception("Error al subir el anteproyecto: " + e.getMessage(), e);
            }
        }

        @Override
        public List<DegreeProject> findProjectsWithApprovedFormatA() throws Exception {
            try {
                String url = PROJECT_SERVICE_URL + "/approvedFormatA";

                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                // Si no hay proyectos
                if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                    System.out.println("No hay proyectos con Formato A aprobado.");
                    return new ArrayList<>();
                }

                // Si todo salió bien
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    String json = response.getBody();

                    // Ajuste por si hay propiedades planas de email (como haces en otros métodos)
                    json = json.replaceAll("\"email\"\\s*:", "\"user\": {\"email\":")
                            .replaceAll(",\\s*\"password\"", "}, \"password\"");

                    // Deserializar lista de DegreeProject
                    List<DegreeProject> projects = objectMapper.readValue(
                            json,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, DegreeProject.class)
                    );

                    System.out.println("Se recibieron " + projects.size() + " proyectos con Formato A aprobado.");
                    return projects;

                } else {
                    throw new RuntimeException("Error al consultar proyectos aprobados: " + response.getStatusCode());
                }

            } catch (Exception e) {
                System.out.println("Error al obtener proyectos con Formato A aprobado: " + e.getMessage());
                throw new Exception("Error al obtener proyectos con Formato A aprobado: " + e.getMessage(), e);
            }
        }


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
        public DegreeProject findProjectByEmail(String email) throws Exception {
            try {
                String url = PROJECT_SERVICE_URL + "/projectByEmail/" + email;

                // ---- Realizamos la petición ----
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                // ---- Caso: no hay proyecto ----
                if (response.getStatusCode() == HttpStatus.NO_CONTENT || response.getBody() == null) {
                    System.out.println("No se encontró proyecto para el estudiante con email: " + email);
                    return null;
                }

                // ---- Caso: éxito ----
                if (response.getStatusCode() == HttpStatus.OK) {
                    String json = response.getBody();

                    //  Ajuste por posibles estructuras planas de email (igual que en otros métodos)
                    json = json.replaceAll("\"email\"\\s*:", "\"user\": {\"email\":")
                            .replaceAll(",\\s*\"password\"", "}, \"password\"");

                    // ---- Deserializar el JSON a un DegreeProject ----
                    DegreeProject project = objectMapper.readValue(json, DegreeProject.class);

                    System.out.println("Proyecto encontrado para " + email + ": " + project.getTitle());
                    return project;

                } else {
                    throw new RuntimeException("Error al obtener el proyecto. Código: " + response.getStatusCode());
                }

            } catch (Exception e) {
                System.out.println("Error al obtener proyecto por email: " + e.getMessage());
                throw new Exception("Error al obtener proyecto por email: " + e.getMessage(), e);
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
                fileRequest.setDate(file.getDate().toString());
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
            state.setName(degreeProject.getState().getName());
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
                return true;
            } else {
                throw new RuntimeException("Error creating user: " +
                        (response != null ? response.getStatusCode() : "No response from server"));
            }

        }

        @Override
        public boolean reuploadProject(DegreeProject degreeProject) throws Exception{
            // Construir el objeto DegreeProjectRequest que se enviará al microservicio
            DegreeProjectEvent request = new DegreeProjectEvent();
            request.setId(degreeProject.getId());
            request.setTitle(degreeProject.getTitle());
            request.setGeneralObjective(degreeProject.getGeneralObjective());
            request.setSpecificObjectives(degreeProject.getSpecificObjectives());
            request.setModality(degreeProject.getModality().toString());

            //Archivos (Formato A y opcionalmente carta)
            List<FileEvent> fileRequests = new ArrayList<>();
            for (File file : degreeProject.getFiles()) {
                FileEvent fileRequest = new FileEvent();
                fileRequest.setId(file.getId());
                fileRequest.setName(file.getName());
                fileRequest.setType(file.getType().toString());
                fileRequest.setVersion(file.getVersion());
                fileRequest.setDocument(Base64.getEncoder().encodeToString(file.getDocument()));
                fileRequest.setDate(file.getDate().toString());
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
            StateRequest stateRequest = new StateRequest();
            stateRequest.setName(degreeProject.getState().getName());
            request.setState(stateRequest);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<String> response = null;

            response = restTemplate.postForEntity(
                    PROJECT_SERVICE_URL + "/reuploadProject",
                    new HttpEntity<>(request, headers),
                    String.class
            );

            if (response != null && response.getStatusCode() == HttpStatus.CREATED) {
                return true;
            } else {
                throw new RuntimeException("Error creating user: " +
                        (response != null ? response.getStatusCode() : "No response from server"));
            }

        }

        @Override
        public void updateProjectAndChanceState(DegreeProject degreeProject, String state) throws Exception{
            try {
                DegreeProjectEvent projectEvent = new DegreeProjectEvent();

                projectEvent.setId(degreeProject.getId());
                projectEvent.setTitle(degreeProject.getTitle());
                projectEvent.setGeneralObjective(degreeProject.getGeneralObjective());
                projectEvent.setSpecificObjectives(degreeProject.getSpecificObjectives());
                projectEvent.setModality(degreeProject.getModality().toString());

                // ---- Files ----
                List<FileEvent> fileEvents = new ArrayList<>();
                if (degreeProject.getFiles() != null) {
                    for (File file : degreeProject.getFiles()) {
                        FileEvent fileEvent = new FileEvent();
                        fileEvent.setId(file.getId());
                        fileEvent.setName(file.getName());
                        fileEvent.setType(file.getType().toString());
                        fileEvent.setVersion(file.getVersion());
                        fileEvent.setDocument(file.getDocument().toString());
                        fileEvents.add(fileEvent);

                        if (file.getDate() != null) {
                            fileEvent.setDate(file.getDate().toString());
                        } else {
                            fileEvent.setDate(LocalDate.now().toString());
                            System.out.println(" El archivo " + file.getName() + " no tenía fecha. Se asignó la actual.");
                        }
                    }
                }

                projectEvent.setFiles(fileEvents);

                // ---- Students ----
                List<Long> studentIds = new ArrayList<>();
                if (degreeProject.getStudents() != null) {
                    for (Student student : degreeProject.getStudents()) {
                        if (student.getId() != null)
                            studentIds.add(student.getId());
                    }
                }
                projectEvent.setStudentsId(studentIds);

                // ---- Director ----
                if (degreeProject.getDirector() != null) {
                    projectEvent.setDirectorId(degreeProject.getDirector().getId());
                }

                // ---- Codirectors ----
                List<Long> codirectorIds = new ArrayList<>();
                if (degreeProject.getCodirectors() != null) {
                    for (Professor codirector : degreeProject.getCodirectors()) {
                        if (codirector.getId() != null)
                            codirectorIds.add(codirector.getId());
                    }
                }
                projectEvent.setCodirectorsId(codirectorIds);

                // ---- State ----
                StateRequest stateRequest = new StateRequest();
                stateRequest.setName(state);
                projectEvent.setState(stateRequest);

                // Construimos la URL con el query param
                String url = PROJECT_SERVICE_URL + "/updateProject?action=" + state;

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                ObjectMapper mapper = new ObjectMapper();
                String projectJson = mapper.writeValueAsString(degreeProject);

                HttpEntity<String> entity = new HttpEntity<>(projectJson, headers);

                // Hacemos la petición PUT
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class
                );

                if (response.getStatusCode() == HttpStatus.OK) {
                    System.out.println(" Proyecto actualizado y estado cambiado correctamente a: " + state);
                } else {
                    throw new RuntimeException("Error al actualizar el proyecto: " + response.getStatusCode());
                }

            } catch (Exception e) {
                System.err.println(" Error al cambiar el estado del proyecto: " + e.getMessage());
                throw new Exception("Error al cambiar el estado del proyecto: " + e.getMessage(), e);
            }
        }


    }
