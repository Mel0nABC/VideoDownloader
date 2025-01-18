package com.video;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<MediaFile, Long> {
    MediaFile findByUrl(String url);

    // void deleteById(String id);
}
