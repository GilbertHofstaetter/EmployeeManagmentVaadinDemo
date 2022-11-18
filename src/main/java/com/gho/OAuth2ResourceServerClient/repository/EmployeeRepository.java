package com.gho.OAuth2ResourceServerClient.repository;

import com.gho.OAuth2ResourceServerClient.obj.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Page<Employee> findByLastNameStartsWithIgnoreCase(String lastName, Pageable pageable);

    int countByLastNameStartsWithIgnoreCase(String lastName);

    Page<Employee> findByCompanyId(long id, Pageable pageable);

    int countByCompanyId(long id);

}
