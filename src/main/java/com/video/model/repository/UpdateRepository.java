package com.video.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.video.model.entity.UpdateInfo;

@Repository
public interface UpdateRepository extends JpaRepository<UpdateInfo, Long> {

    UpdateInfo findByUrl(String url);
}