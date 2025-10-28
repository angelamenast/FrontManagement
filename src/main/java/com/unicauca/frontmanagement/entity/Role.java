package com.unicauca.frontmanagement.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    private EnumRole roleType ;

}
