package com.unicauca.FrontManagement.services;

import com.unicauca.FrontManagement.entity.Person;
import com.unicauca.FrontManagement.entity.User;
import com.unicauca.FrontManagement.repositories.IPersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService implements IPersonService {
    @Autowired
    private IPersonRepository personRepository;

    @Override
    public boolean loginPerson(User user)throws Exception
    {
        return personRepository.loginPerson(user);
    }
    @Override
    public boolean registerPerson(Person person) throws Exception{
        return personRepository.registerPerson(person);
    }

    public Person obtainUserByEmail(String email)throws Exception{
        return personRepository.obtainUserByEmail(email);
    }

    public String obtainUserRole(String email){
        return personRepository.obtainUserRole(email);
    }

    public String validateSecurePassword(String password){
        if (password.length() < 6) {
            return "La contraseña debe tener al menos 6 caracteres.";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "La contraseña debe contener al menos una letra mayúscula.";
        }
        if (!password.matches(".*\\d.*")) {
            return "La contraseña debe contener al menos un número.";
        }
        if (!password.matches(".*[!@#$%^&(),.?\":{}|<>].*")) {
            return "La contraseña debe contener al menos un carácter especial.";
        }
        return "OK";
    }

    public String validateEducationalEmail(String email){
        if (email == null || email.trim().isEmpty()) {
            return "El correo no puede estar vacío.";
        }

        // Verifica que tenga al menos un @
        if (!email.contains("@")) {
            return "El correo debe contener el carácter '@'.";
        }

        // Verifica que termine con el dominio institucional
        if (!email.endsWith("@unicauca.edu.co")) {
            return "El correo debe pertenecer al dominio @unicauca.edu.co.";
        }

        return "OK";
    }

}
