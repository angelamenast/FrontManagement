package com.unicauca.FrontManagement.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
public class File {

    @Getter @Setter
    private Long id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private EnumFileType type;
    @Getter @Setter
    private int version;
    @Getter @Setter
    private byte[] document;
    @Getter @Setter
    private LocalDate date;

}
