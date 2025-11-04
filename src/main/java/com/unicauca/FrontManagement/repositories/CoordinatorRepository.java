package com.unicauca.FrontManagement.repositories;

import com.unicauca.FrontManagement.entity.Coordinator;
import com.unicauca.FrontManagement.entity.Person;
import com.unicauca.FrontManagement.entity.Role;
import com.unicauca.FrontManagement.entity.User;
import com.unicauca.FrontManagement.infra.dto.CoordinatorRequest;
import com.unicauca.FrontManagement.infra.dto.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class CoordinatorRepository implements ICoordinatorRepository{

    private final String USER_SERVICE_URL = "http://localhost:8080/api/usersmanagement";

    @Autowired
    private RestTemplate restTemplate;

    public boolean registerCoordinator(Person person) throws Exception{
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

        CoordinatorRequest coordinatorDTO = new CoordinatorRequest();
        coordinatorDTO.setName(person.getName());
        coordinatorDTO.setLastName(person.getLastName());
        coordinatorDTO.setPhoneNumber(person.getPhoneNumber());
        coordinatorDTO.setProgram(person.getProgram().toString());
        if (person instanceof Coordinator c) {
            coordinatorDTO.setNombredelperro(null);
        }
        coordinatorDTO.setUserRequest(userReq);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = null;

        response = restTemplate.postForEntity(
                USER_SERVICE_URL + "/coordinator",
                new HttpEntity<>(coordinatorDTO, headers),
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
