package com.unicauca.frontmanagement.services;

import com.unicauca.frontmanagement.entity.Student;
import com.unicauca.frontmanagement.repositories.StudentRepository;
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




