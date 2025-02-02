package com.video.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.video.model.entity.TableInfo;

@Repository
public interface TableInfoRepository extends JpaRepository<TableInfo, Long> {

    TableInfo findByUrl(String url);

}