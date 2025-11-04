package com.unicauca.FrontManagement.services;

import com.unicauca.FrontManagement.entity.Person;
import com.unicauca.FrontManagement.entity.User;

public interface IPersonService {
    boolean registerPerson(Person person) throws Exception;
    boolean loginPerson(User user) throws Exception;
}
