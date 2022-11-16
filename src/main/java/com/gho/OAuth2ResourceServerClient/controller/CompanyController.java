package com.gho.OAuth2ResourceServerClient.controller;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import com.gho.OAuth2ResourceServerClient.obj.Employee;
import com.gho.OAuth2ResourceServerClient.repository.CompanyRepository;
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
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    CompanyRepository companyRepository;

    @GetMapping("/listAll")
    //@RolesAllowed("ROLE_user")
    //@PreAuthorize("hasAuthority('ROLE_user')")
    @PreAuthorize("hasAnyAuthority('ROLE_user', 'ROLE_admin') AND hasAuthority('SCOPE_read')")
    //Scope read added to client springboot-microservice in keycloak server and set optional
    //read scope is only present in token when requested as scope profile email read ... on token retrieval
    public List<Company> listAll() {
        List<Company> companies = companyRepository.findAll();
        return companies;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_user', 'ROLE_admin') AND hasAuthority('SCOPE_read')")
    @GetMapping("/list")
    public Page<Company> list(@ParameterObject Pageable pageable) {
        Page<Company> companies = companyRepository.findAll(pageable);
        return companies;
    }
}
