package com.gho.OAuth2ResourceServerClient.controller;

import com.gho.OAuth2ResourceServerClient.obj.Employee;
import com.gho.OAuth2ResourceServerClient.obj.Picture;
import com.gho.OAuth2ResourceServerClient.repository.EmployeeRepository;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    EmployeeRepository employeeRepository;

    @GetMapping("/listAll")
    public List<Employee> listAll() {
        List<Employee> employees = employeeRepository.findAll();
        return employees;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_user', 'ROLE_admin') AND hasAuthority('SCOPE_read')")
    @GetMapping("/list")
    public Page<Employee> list(@ParameterObject Pageable pageable) {
        Page<Employee> employees = employeeRepository.findAll(pageable);
        return employees;
    }
}
