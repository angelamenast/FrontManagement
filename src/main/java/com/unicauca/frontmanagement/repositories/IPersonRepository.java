package com.unicauca.frontmanagement.repositories;

import com.unicauca.frontmanagement.entity.*;

public interface IPersonRepository {
    boolean loginPerson(User user)throws Exception;
    boolean registerPerson(Person person) throws Exception;
    Person obtainUserByEmail(String email) throws Exception;
    String obtainUserRole(String email);



}
