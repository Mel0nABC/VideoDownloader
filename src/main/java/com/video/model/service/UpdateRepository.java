package com.video.model.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.video.model.entity.UpdateInfo;

public interface UpdateRepository extends JpaRepository<UpdateInfo, Long> {

    UpdateInfo findByUrl(String url);

}