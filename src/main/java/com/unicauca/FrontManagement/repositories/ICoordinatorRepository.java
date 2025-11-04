package com.unicauca.FrontManagement.repositories;

import com.unicauca.FrontManagement.entity.Person;

public interface ICoordinatorRepository {

    public boolean registerCoordinator(Person person) throws Exception;

}
