package com.unicauca.frontmanagement.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicauca.frontmanagement.entity.HeadOfDepartment;
import com.unicauca.frontmanagement.entity.Person;
import com.unicauca.frontmanagement.entity.Role;
import com.unicauca.frontmanagement.entity.User;
import com.unicauca.frontmanagement.infra.dto.HeadOfDepartmentRequest;
import com.unicauca.frontmanagement.infra.dto.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class HeadOfDepartmentRepository implements IHeadOfDepartmentRepository {

    private final String USER_SERVICE_URL = "http://localhost:8080/api/usersmanagement";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public boolean registerHeadOfDepartment(Person person) throws Exception {

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

        HeadOfDepartmentRequest headDTO = new HeadOfDepartmentRequest();
        headDTO.setName(person.getName());
        headDTO.setLastName(person.getLastName());
        headDTO.setPhoneNumber(person.getPhoneNumber());
        headDTO.setProgram(person.getProgram().toString());
        if (person instanceof HeadOfDepartment h) {
            headDTO.setSuputamadre(null);
        }
        headDTO.setUserRequest(userReq);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = null;

        response = restTemplate.postForEntity(
                USER_SERVICE_URL + "/head",
                new HttpEntity<>(headDTO, headers),
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
