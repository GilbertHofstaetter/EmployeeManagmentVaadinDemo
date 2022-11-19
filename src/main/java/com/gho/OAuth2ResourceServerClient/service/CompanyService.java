package com.gho.OAuth2ResourceServerClient.service;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import com.gho.OAuth2ResourceServerClient.repository.CompanyRepository;
import com.gho.OAuth2ResourceServerClient.repository.DocumentRepository;
import com.gho.OAuth2ResourceServerClient.repository.EmployeeRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DocumentRepository documentRepository;

    public void delete(Company company) {
        Session session = sessionFactory.openSession();
        try {
            session.update(company);
            company.getEmployees().forEach(employee -> {
                employee.setCompany(null);
                employeeRepository.save(employee);
            });

            companyRepository.delete(company);
//            if (company.getDocuments() != null) {
//                company.getDocuments().forEach(document -> {
//                    documentRepository.delete(document);
//                });
//            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            session.close();
        }
    }
}
