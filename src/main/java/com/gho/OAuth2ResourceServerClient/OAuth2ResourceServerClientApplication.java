package com.gho.OAuth2ResourceServerClient;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import com.gho.OAuth2ResourceServerClient.obj.Document;
import com.gho.OAuth2ResourceServerClient.obj.Employee;
import com.gho.OAuth2ResourceServerClient.obj.Picture;
import com.gho.OAuth2ResourceServerClient.repository.CompanyRepository;
import com.gho.OAuth2ResourceServerClient.repository.DocumentRepository;
import com.gho.OAuth2ResourceServerClient.repository.EmployeeRepository;
import com.gho.OAuth2ResourceServerClient.repository.PictureRepository;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashSet;

//https://www.djamware.com/post/6225b66ba88c55c95abca0b6/spring-boot-security-postgresql-and-keycloak-rest-api-oauth2
//https://stackoverflow.com/questions/72719400/how-to-configure-oauth2-in-spring-boot-be-spring-boot-fe-keycloak
//https://stackoverflow.com/questions/58205510/spring-security-mapping-oauth2-claims-with-roles-to-secure-resource-server-endp
@Push
@SpringBootApplication
@ComponentScan({"com.gho.OAuth2ResourceServerClient"})
public class OAuth2ResourceServerClientApplication extends SpringBootServletInitializer implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(OAuth2ResourceServerClientApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(EmployeeRepository employeeRepository, CompanyRepository companyRepository, PictureRepository pictureRepository, DocumentRepository documentRepository, SessionFactory sessionFactory) {
        return (args) -> {
            Employee employee0 = new Employee();
            employee0.setFirstName("Max0");
            employee0.setLastName("Mustermann0");
            employee0.setBirthDate(new Date());
            employee0.setEmail("m0.m0@company.com");
            employee0 = employeeRepository.save(employee0);

            Picture picture0 = new Picture();
            picture0 = pictureRepository.save(picture0);
            Session session = sessionFactory.openSession();
            session.update(employee0);
            employee0.setPicture(picture0);
            employee0.setDocuments(new HashSet<Document>());
            Document document0 = documentRepository.save(new Document());
            employee0.getDocuments().add(document0);
            employee0 = employeeRepository.save(employee0);
            session.close();

            Employee employee1 = new Employee();
            employee1.setFirstName("Max1");
            employee1.setLastName("Mustermann1");
            employee1.setBirthDate(new Date());
            employee1.setEmail("m1.m1@company.com");
            employee1 = employeeRepository.save(employee1);

            Picture picture1 = new Picture();
            picture1 = pictureRepository.save(picture1);
            session = sessionFactory.openSession();
            session.update(employee1);
            employee1.setPicture(picture1);
            employee1.setDocuments(new HashSet<Document>());
            Document document1 = documentRepository.save(new Document());
            employee1.getDocuments().add(document1);
            employee1 = employeeRepository.save(employee1);
            session.close();

            Employee employee2 = new Employee();
            employee2.setFirstName("Max2");
            employee2.setLastName("Mustermann2");
            employee2.setBirthDate(new Date());
            employee2.setEmail("m2.m2@company.com");
            employee2 = employeeRepository.save(employee2);

            Picture picture2 = new Picture();
            picture2 = pictureRepository.save(picture2);
            session = sessionFactory.openSession();
            session.update(employee2);
            employee2.setPicture(picture2);
            employee2.setDocuments(new HashSet<Document>());
            Document document2 = documentRepository.save(new Document());
            employee2.getDocuments().add(document2);
            employee2 = employeeRepository.save(employee2);
            session.close();

            Company company = new Company();
            company.setName("Company");
            company = companyRepository.save(company);
            company.setEmployees(new HashSet<Employee>());
            company.getEmployees().add(employee0);
            company.getEmployees().add(employee1);
            company.getEmployees().add(employee2);
            company.setDocuments(new HashSet<Document>());
            Document document3 = documentRepository.save(new Document());
            company.getDocuments().add(document3);
            company = companyRepository.save(company);

            employee0.setCompany(company);
            employeeRepository.save(employee0);

            employee1.setCompany(company);
            employeeRepository.save(employee1);

            employee2.setCompany(company);
            employeeRepository.save(employee2);

            //keycloakService.createClient();
        };
    }

}
