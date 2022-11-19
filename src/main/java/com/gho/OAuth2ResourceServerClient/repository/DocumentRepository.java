package com.gho.OAuth2ResourceServerClient.repository;

import com.gho.OAuth2ResourceServerClient.obj.Document;
import com.gho.OAuth2ResourceServerClient.obj.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    Page<Document> findByCompanyIdAndFileNameLike(long id, String fileName, Pageable pageable);

    int countByCompanyIdAndFileNameLike(long id, String fileName);

    Page<Document> findByEmployeeIdAndFileNameLike(long id, String fileName, Pageable pageable);

    int countByEmployeeIdAndFileNameLike(long id, String fileName);
}
