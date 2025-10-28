package com.unicauca.frontmanagement.repositories;

import com.unicauca.frontmanagement.entity.Person;

public interface IHeadOfDepartmentRepository {

    public boolean registerHeadOfDepartment(Person person) throws Exception;

}
