package com.gho.OAuth2ResourceServerClient.service;

import com.gho.OAuth2ResourceServerClient.obj.Employee;
import com.gho.OAuth2ResourceServerClient.obj.Picture;
import com.gho.OAuth2ResourceServerClient.repository.DocumentRepository;
import com.gho.OAuth2ResourceServerClient.repository.EmployeeRepository;
import com.gho.OAuth2ResourceServerClient.repository.PictureRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private DocumentRepository documentRepository;

    public void delete(Employee employee) {
        Session session = sessionFactory.openSession();
        try {
            session.update(employee);
            Picture picture = employee.getPicture();
            employeeRepository.delete(employee);
            if (picture != null)
                pictureRepository.delete(picture);
//            if (employee.getDocuments() != null) {
//                employee.getDocuments().forEach(document -> {
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
