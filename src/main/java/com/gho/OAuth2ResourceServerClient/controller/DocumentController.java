package com.gho.OAuth2ResourceServerClient.controller;

import com.gho.OAuth2ResourceServerClient.obj.Document;
import com.gho.OAuth2ResourceServerClient.obj.Picture;
import com.gho.OAuth2ResourceServerClient.repository.DocumentRepository;
import com.gho.OAuth2ResourceServerClient.repository.PictureRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/document")
public class DocumentController {

    @Autowired
    DocumentRepository documentRepository;

    // Single File download
    //http://localhost:8082/api/employeePicture_Content/rest/download?id=1027
    @PreAuthorize("hasAnyAuthority('ROLE_user', 'ROLE_admin') AND hasAuthority('SCOPE_read')")
    @RequestMapping(path = "/download", method = RequestMethod.GET)
    @Operation(summary = "Download a File")
    public ResponseEntity<Resource> downloadPhoto(long id) throws IOException {
        Optional<Document> document = documentRepository.findById(id);
        if (!document.isEmpty()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + document.get().getFileName());
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            ByteArrayResource resource = new ByteArrayResource(document.get().getDocument());
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(document.get().getDocument().length)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } else
            return ResponseEntity.notFound().build();
    }

    // Single File upload
    @PreAuthorize("hasAnyAuthority('ROLE_user', 'ROLE_admin') AND hasAuthority('SCOPE_read')")
    @PostMapping(value = "/upload/{id}", consumes = {
            "multipart/form-data"
    })
    @Operation(summary = "Upload a single File")
    public ResponseEntity<Document> upload(@PathVariable("id") final Long id, @RequestParam("file") MultipartFile uploadfile) {
        //   logger.debug("Single file upload!");
        Optional<Document> documentContent = documentRepository.findById(id);
        if (uploadfile.isEmpty()) {
            return new ResponseEntity("You have to select a file!", HttpStatus.OK);
        }
        try {

            if (!uploadfile.isEmpty() && !documentContent.isEmpty()) {
                byte[] bytes = uploadfile.getBytes();

                Document document = documentContent.get();
                document.setDocument(bytes);
                document.setFileName(uploadfile.getOriginalFilename());
                document = documentRepository.save(document);
                return new ResponseEntity(document, new HttpHeaders(), HttpStatus.OK);
            } else
                throw new IOException("File is empty");

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @PreAuthorize("hasAnyAuthority('ROLE_user', 'ROLE_admin') AND hasAuthority('SCOPE_read')")
    @GetMapping("/list")
    public Page<Document> list(@ParameterObject Pageable pageable) {
        Page<Document> documents = documentRepository.findAll(pageable);
        return documents;
    }

}
