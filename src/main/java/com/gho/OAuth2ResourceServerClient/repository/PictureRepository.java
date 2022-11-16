package com.gho.OAuth2ResourceServerClient.repository;

import com.gho.OAuth2ResourceServerClient.obj.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PictureRepository extends JpaRepository<Picture, Long>  {
}
