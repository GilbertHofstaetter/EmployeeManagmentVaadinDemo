package com.gho.OAuth2ResourceServerClient.controller;

import com.gho.OAuth2ResourceServerClient.obj.Picture;
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
@RequestMapping("/api/picture")
public class PictureController {

    @Autowired
    PictureRepository pictureRepository;

    // Single File download
    //http://localhost:8082/api/employeePicture_Content/rest/download?id=1027
    @PreAuthorize("hasAnyAuthority('ROLE_user', 'ROLE_admin') AND hasAuthority('SCOPE_read')")
    @RequestMapping(path = "/download", method = RequestMethod.GET)
    @Operation(summary = "Download a File")
    public ResponseEntity<Resource> downloadPhoto(long id) throws IOException {
        Optional<Picture> picture = pictureRepository.findById(id);
        if (!picture.isEmpty()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + picture.get().getFileName());
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            ByteArrayResource resource = new ByteArrayResource(picture.get().getPhoto());
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(picture.get().getPhoto().length)
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
    public ResponseEntity<Picture> upload(@PathVariable("id") final Long id, @RequestParam("file") MultipartFile uploadfile) {
        //   logger.debug("Single file upload!");
        Optional<Picture> employeePictureContent = pictureRepository.findById(id);
        if (uploadfile.isEmpty()) {
            return new ResponseEntity("You have to select a file!", HttpStatus.OK);
        }
        try {

            if (!uploadfile.isEmpty() && !employeePictureContent.isEmpty()) {
                byte[] bytes = uploadfile.getBytes();

                Picture picture = employeePictureContent.get();
                picture.setPhoto(bytes);
                picture.setFileName(uploadfile.getOriginalFilename());
                picture = pictureRepository.save(picture);
                return new ResponseEntity(picture, new HttpHeaders(), HttpStatus.OK);
            } else
                throw new IOException("File is empty");

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @PreAuthorize("hasAnyAuthority('ROLE_user', 'ROLE_admin') AND hasAuthority('SCOPE_read')")
    @GetMapping("/list")
    public Page<Picture> list(@ParameterObject Pageable pageable) {
        Page<Picture> pictures = pictureRepository.findAll(pageable);
        return pictures;
    }

}
