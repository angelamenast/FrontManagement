package com.unicauca.frontmanagement.repositories;

import com.unicauca.frontmanagement.entity.Person;

public interface ICoordinatorRepository {

    public boolean registerCoordinator(Person person) throws Exception;

}
