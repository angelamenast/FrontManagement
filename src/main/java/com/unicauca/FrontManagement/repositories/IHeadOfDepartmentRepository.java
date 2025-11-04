package com.unicauca.FrontManagement.repositories;

import com.unicauca.FrontManagement.entity.Person;

public interface IHeadOfDepartmentRepository {

    public boolean registerHeadOfDepartment(Person person) throws Exception;

}
