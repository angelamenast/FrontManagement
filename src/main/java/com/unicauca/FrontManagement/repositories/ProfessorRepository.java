package com.unicauca.FrontManagement.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicauca.FrontManagement.entity.*;
import com.unicauca.FrontManagement.infra.dto.ProfessorRequest;
import com.unicauca.FrontManagement.infra.dto.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class ProfessorRepository implements IProfessorRepository {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    private final String USER_SERVICE_URL = "http://localhost:8080/api/usersmanagement";

    @Override
    public boolean registerProfessor(Person person) throws Exception {
        List<Role> roleList = person.getUser().getRoles();

        User user = new User();
        user.setEmail(person.getUser().getEmail());
        user.setPassword(person.getUser().getPassword());
        user.setRoles(roleList);

        UserRequest userReq = new UserRequest();
        userReq.setEmail(person.getUser().getEmail());
        userReq.setPassword(person.getUser().getPassword());
        userReq.setRoles(  person.getUser().getRoles().stream()
                .map(role -> role.getRoleType().name())
                .toList()
        );

        ProfessorRequest professorDTO = new ProfessorRequest();
        professorDTO.setName(person.getName());
        professorDTO.setLastName(person.getLastName());
        professorDTO.setPhoneNumber(person.getPhoneNumber());
        professorDTO.setProgram(person.getProgram().toString());
        if (person instanceof Professor p) {
            professorDTO.setOffice(null);
        }
        professorDTO.setUserRequest(userReq);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = null;

        response = restTemplate.postForEntity(
                USER_SERVICE_URL + "/professor",
                new HttpEntity<>(professorDTO, headers),
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

    @Override
    public List<Professor> listProfessors() throws Exception {
        try {
            String url = USER_SERVICE_URL + "/professors";

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            System.out.println("Respuesta: " + response.getBody());

            // Si la respuesta fue exitosa
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Convierte el JSON a una lista de objetos Professor
                return objectMapper.readValue(response.getBody(), new TypeReference<List<Professor>>() {});
            }

            // Si no hay contenido
            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                System.out.println("No hay profesores registrados.");
                return List.of();
            }

            // Si hay otro tipo de respuesta
            throw new RuntimeException("Respuesta inesperada del servidor: " + response.getStatusCode());

        } catch (Exception e) {
            System.err.println("Error al obtener los profesores: " + e.getMessage());
            throw new Exception("Error al obtener los profesores: " + e.getMessage(), e);
        }
    }

}
