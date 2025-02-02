package com.video.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.video.model.entity.MediaFile;
import com.video.model.entity.TableInfo;
import com.video.model.entity.UpdateInfo;
import com.video.model.repository.MediaRepository;
import com.video.model.repository.TableInfoRepository;
import com.video.model.repository.UpdateRepository;

@Service
public class MediaFileService {

    private final int EXIT_CODE_OK = 0;

    private UpdateRepository updateRepository;
    private MediaRepository mediaRepository;
    private TableInfoRepository tableInfoRepository;

    public MediaFileService(UpdateRepository updateRepository, MediaRepository mediaRepository, TableInfoRepository tableInfoRepository) {
        this.updateRepository = updateRepository;
        this.mediaRepository = mediaRepository;
        this.tableInfoRepository = tableInfoRepository;
    }

    public MediaFile addUrlBBDD(String url, UpdateInfo updateInfo, List<TableInfo> tableInfoList) {
        MediaFile mfBBDD = new MediaFile(url, false, EXIT_CODE_OK, updateInfo);
        mfBBDD.setTotalSongs(updateInfo.getPlaylist_count());

        mfBBDD.setTableInfoList(tableInfoList);

        updateRepository.save(updateInfo);
        mediaRepository.save(mfBBDD);
        mfBBDD = mediaRepository.findByUrl(mfBBDD.getUrl());

        return mfBBDD;
    }

    public Optional<List<TableInfo>> getTableInfo(@RequestParam("url") String url) {
        return Optional.ofNullable(tableInfoRepository.findAllByUrl(url));
    }
}
