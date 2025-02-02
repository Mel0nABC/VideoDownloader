package com.video.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.video.model.entity.TableInfo;

@Repository
public interface TableInfoRepository extends JpaRepository<TableInfo, Long> {

    TableInfo findByUrl(String url);
    List<TableInfo> findAllByUrl(String url);

}