package com.pruebas;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<MediaFile, Long> {

    MediaFile findByUrl(String url);



}
