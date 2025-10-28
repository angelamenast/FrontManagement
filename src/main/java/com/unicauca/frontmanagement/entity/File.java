package com.unicauca.frontmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class File {

    @Getter @Setter
    private String name;
    @Getter @Setter
    private EnumFileType type;
    @Getter @Setter
    private int version;
    @Getter@Setter
    private byte[] document;

}
