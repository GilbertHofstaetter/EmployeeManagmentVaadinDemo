package com.gho.OAuth2ResourceServerClient.service;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import com.gho.OAuth2ResourceServerClient.repository.CompanyRepository;
import com.gho.OAuth2ResourceServerClient.repository.EmployeeRepository;
import com.vaadin.flow.component.notification.Notification;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.keycloak.admin.client.CreatedResponseUtil;
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

    public boolean delete(Company company) {
        try {
            Session session = sessionFactory.openSession();
            session.update(company);
            company.getEmployees().forEach(employee -> {
                employee.setCompany(null);
                employeeRepository.save(employee);
            });
            companyRepository.delete(company);
            session.close();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
