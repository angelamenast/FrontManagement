package com.unicauca.FrontManagement.services;

import com.unicauca.FrontManagement.entity.Person;
import com.unicauca.FrontManagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary //indica que este proxy se inyecta en lugar del servicio real
public class PersonServiceProxy implements IPersonService {

    @Autowired
    private PersonService realService; // El servicio real al que delega

    @Override
    public boolean registerPerson(Person person) throws Exception {
        System.out.println("[Proxy] Validando datos antes de registrar...");

        if (person.getUser().getEmail() == null || person.getUser().getEmail().isEmpty()) {
            System.out.println("[Proxy] Email inválido, cancelando registro");
            return false;
        }

        try {
            boolean result = realService.registerPerson(person);
            System.out.println("[Proxy] Registro completado exitosamente.");
            return result;
        } catch (Exception e) {
            System.out.println("[Proxy] Error al registrar persona: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean loginPerson(User user) throws Exception {
        System.out.println("[Proxy] Intentando login para: " + user.getEmail());
        return realService.loginPerson(user);
    }
}
