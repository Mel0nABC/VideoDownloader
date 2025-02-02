package com.video.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.video.model.entity.MediaFile;

@Repository
public interface MediaRepository extends JpaRepository<MediaFile, Long> {
    MediaFile findByUrl(String url);
}
