package com.gho.OAuth2ResourceServerClient.repository;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Page<Company> findByNameStartsWithIgnoreCase(String name, Pageable pageable);

    int countByNameStartsWithIgnoreCase(String lastName);
}
