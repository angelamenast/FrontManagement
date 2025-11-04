package com.unicauca.FrontManagement.factories;
import com.unicauca.FrontManagement.entity.*;
public class PersonFactoryProvider {

    public static PersonFactory getFactory(EnumRole role) {
        return switch (role) {
            case Estudiante -> new StudentFactory();
            case Profesor -> new ProfessorFactory();
            case Coordinador -> new CoordinatorFactory();
            case JefeDeDepartamento -> new HeadOfDepartamentFactory();
            default -> throw new IllegalArgumentException("No factory defined for role: " + role);
        };
    }

}

