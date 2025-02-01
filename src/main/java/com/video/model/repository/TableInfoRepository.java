package com.video.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.video.model.entity.TableInfo;

public interface TableInfoRepository extends JpaRepository<TableInfo, Long> {

    TableInfo findByUrl(String url);

}