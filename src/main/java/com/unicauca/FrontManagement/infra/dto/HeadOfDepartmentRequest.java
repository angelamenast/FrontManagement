package com.unicauca.FrontManagement.infra.dto;

import lombok.Getter;
import lombok.Setter;

public class HeadOfDepartmentRequest {
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String lastName;
    @Getter @Setter
    private String phoneNumber;
    @Getter @Setter
    private String program;
    @Getter @Setter
    private UserRequest userRequest;
    @Getter @Setter
    private String suputamadre;
}
