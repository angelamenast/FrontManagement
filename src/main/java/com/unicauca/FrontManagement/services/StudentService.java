package com.unicauca.FrontManagement.services;

import com.unicauca.FrontManagement.entity.Student;
import com.unicauca.FrontManagement.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public Student obtainUserByEmail(String email)throws Exception{
        return studentRepository.getStudentByEmail(email);
    }

}




