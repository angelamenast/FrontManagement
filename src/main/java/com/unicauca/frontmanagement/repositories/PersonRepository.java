package com.unicauca.frontmanagement.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicauca.frontmanagement.entity.*;
import com.unicauca.frontmanagement.infra.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.List;

@Component
public class PersonRepository implements IPersonRepository {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;
    private final String USER_SERVICE_URL = "http://localhost:8080/api/usersmanagement";

    @Autowired
    IStudentRepository studentRepository;

    @Autowired
    IProfessorRepository professorRepository;

    @Autowired
    ICoordinatorRepository coordinatorRepository;

    @Autowired
    IHeadOfDepartmentRepository headOfDepartmentRepository;


    @Override
    public boolean registerPerson(Person person) throws Exception {

            List<Role> roleList = person.getUser().getRoles();

            for (Role r : roleList) {

                switch (r.getRoleType()) {
                    case Estudiante -> {
                        return studentRepository.registerStudent(person);
                    }
                    case Profesor -> {
                        return professorRepository.registerProfessor(person);
                    }
                    case Coordinador -> {
                        return coordinatorRepository.registerCoordinator(person);
                    }
                    case JefeDeDepartamento -> {
                        return headOfDepartmentRepository.registerHeadOfDepartment(person);
                    }
                }

            }

        return false;
    }

    @Override
    public boolean loginPerson(User user) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);


            HttpEntity<User> request = new HttpEntity<>(user, headers);

            ResponseEntity<Boolean> response = restTemplate.postForEntity(
                    USER_SERVICE_URL + "/login",
                    request,
                    Boolean.class
            );

            if (response.getStatusCode() == HttpStatus.OK && Boolean.TRUE.equals(response.getBody())) {
                System.out.println("Login successful for: " + user.getEmail());
                return true;

            } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                System.out.println("Invalid email or password for: " + user.getEmail());
                throw new RuntimeException("Invalid email or password");

            } else {
                System.out.println("Unexpected response: " + response.getStatusCode());
                throw new RuntimeException("Unexpected response from server: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.out.println("There was an error during login: " + e.getMessage());
            throw new Exception("Login failed: " + e.getMessage(), e);
        }
    }


    @Override
    public Person obtainUserByEmail(String email) throws Exception {
        try {
            String url = USER_SERVICE_URL + "/personByEmail/" + email;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            System.out.println("Respuesta: " + response.getBody());
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {

                String body = response.getBody();

                if (body.contains("studentCode")) {
                    return objectMapper.readValue(body, Student.class);
                } else if (body.contains("office")) {
                    return objectMapper.readValue(body, Professor.class);
                } else if (body.contains("nombredelperro")) {
                    return objectMapper.readValue(body, Coordinator.class);
                } else if (body.contains("suputamadre")) {
                    return objectMapper.readValue(body, HeadOfDepartment.class);
                } else {

                    return objectMapper.readValue(body, Person.class);
                }
            } else if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                System.out.println("No person registered with email: " + email);
                return null;
            } else {
                throw new RuntimeException("Unexpected server response: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.out.println("Error obtaining person by email: " + e.getMessage());
            throw new Exception("Error obtaining person by email: " + e.getMessage(), e);
        }
    }

    @Override
    public String obtainUserRole(String email) {
        return "";
    }

}
