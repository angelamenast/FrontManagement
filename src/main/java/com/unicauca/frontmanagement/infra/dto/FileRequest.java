package com.unicauca.frontmanagement.infra.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FileRequest {
    private String name;
    private String type;
    private int version;
    private String document;

}
