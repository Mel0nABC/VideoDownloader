package com.video.model.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.video.model.entity.MediaFile;

public interface MediaRepository extends JpaRepository<MediaFile, Long> {
    MediaFile findByUrl(String url);
}
