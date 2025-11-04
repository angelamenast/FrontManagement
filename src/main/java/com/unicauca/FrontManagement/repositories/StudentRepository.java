package com.unicauca.FrontManagement.repositories;

import com.unicauca.FrontManagement.entity.Person;
import com.unicauca.FrontManagement.entity.Role;
import com.unicauca.FrontManagement.entity.Student;
import com.unicauca.FrontManagement.entity.User;
import com.unicauca.FrontManagement.infra.dto.StudentRequest;
import com.unicauca.FrontManagement.infra.dto.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class StudentRepository implements IStudentRepository {


    private final String USER_SERVICE_URL = "http://localhost:8080/api/usersmanagement";

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public boolean registerStudent(Person person) throws Exception {
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

        StudentRequest studentDTO = new StudentRequest();
        studentDTO.setName(person.getName());
        studentDTO.setLastName(person.getLastName());
        studentDTO.setPhoneNumber(person.getPhoneNumber());
        studentDTO.setProgram(person.getProgram().toString());
        if (person instanceof Student s) {
            studentDTO.setStudentCode(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = null;

        studentDTO.setUserRequest(userReq);
        //System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(studentDTO));
        response = restTemplate.postForEntity(
                USER_SERVICE_URL + "/student",
                new HttpEntity<>(studentDTO, headers),
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

    public Student getStudentByEmail(String email) throws Exception {
        try {
            String url = USER_SERVICE_URL + "/StudentByEmail/" + email;
            ResponseEntity<Student> response = restTemplate.getForEntity(url, Student.class);
            return response.getBody();
        } catch (Exception e) {
            throw new Exception("Error al buscar estudiante por email: " + e.getMessage());
        }
    }

}
