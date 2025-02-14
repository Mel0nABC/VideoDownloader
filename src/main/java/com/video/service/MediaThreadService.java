package com.video.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.video.model.entity.MediaFile;
import com.video.model.entity.TableInfo;
import com.video.model.entity.UpdateInfo;
import com.video.model.repository.MediaRepository;
import com.video.model.repository.TableInfoRepository;
import com.video.model.repository.UpdateRepository;
import com.video.util.MediaThread;

@Service
public class MediaThreadService {

    private final int EXIT_CODE_OK = 0;

    private MediaRepository mediaRepository;
    private UpdateRepository updateRepository;
    private TableInfoRepository tableInfoRepository;

    private List<MediaThread> mediaThreadList = new ArrayList<>();
    private ThreadGroup threadGroup = new ThreadGroup("ThreadGroup");

    public MediaThreadService(MediaRepository mediaRepository, UpdateRepository updateRepository,
            TableInfoRepository tableInfoRepository) {
        this.mediaRepository = mediaRepository;
        this.updateRepository = updateRepository;
        this.tableInfoRepository = tableInfoRepository;
        addAllDbToThreadList();
    }

    public Optional<MediaThread> addDownload(String url, String jsonData, MediaFile newFile, UpdateInfo updateInfo,
            List<TableInfo> tableInfoList) {

        if (!urlExist(url))
            return null;

        if (jsonData == null)
            return null;

        if (updateInfo == null)
            return null;

        if (tableInfoList == null)
            return null;

        Optional<MediaThread> mediaThread = Optional
                .ofNullable(new MediaThread(threadGroup, newFile, "", false, mediaRepository));
        if (!mediaThread.isEmpty())
            mediaThreadList.add(mediaThread.get());

        return mediaThread;
    }

    public MediaFile download(String url, String formatId){
        MediaThread mediaThread = null;
        MediaFile mediaFile = null;

        for (int i = 0; i < mediaThreadList.size(); i++) {

            mediaThread = mediaThreadList.get(i);

            if (mediaThread.getMediaFile().getUrl().equals(url)) {
                mediaFile = mediaThread.getMediaFile();
                mediaThreadList.remove(i);
            }
        }
        mediaFile.setExitCode(EXIT_CODE_OK);
        mediaThread.setFormatId(formatId);
        mediaThread = new MediaThread(threadGroup, mediaFile, formatId, false, mediaRepository);
        mediaThread.start();
        mediaThreadList.add(mediaThread);
        return mediaFile;
    }

    public boolean delByUrlWeb(String url) {

        MediaFile mfToDelete = mediaRepository.findByUrl(url);
        if (mfToDelete != null) {

            MediaThread tmpDelete = null;
            for (MediaThread mtDelete : mediaThreadList) {
                if (mtDelete.getMediaFile().getUrl().equals(mfToDelete.getUrl())) {
                    tmpDelete = mtDelete;
                    break;
                }
            }
            mediaThreadList.remove(tmpDelete);
            mediaRepository.delete(mfToDelete);
            return true;
        }
        return false;
    }

    public boolean delThreadFromList(MediaFile mfToDel) {
        for (MediaThread mt : mediaThreadList) {
            if (mt.getMediaFile().getUrl().equals(mfToDel.getUrl())) {
                mediaThreadList.remove(mt);
                return true;
            }
        }
        return false;
    }

    public Boolean stopThreadWeb(String url) {
        for (MediaThread tr : mediaThreadList) {
            if (tr != null) {
                if (tr.getMediaFile().getUrl().equals(url)) {
                    tr.interrupt();
                    try {
                        tr.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!tr.isAlive()) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    public void addAllDbToThreadList() {
        for (MediaFile mfLoad : mediaRepository.findAll()) {
            mfLoad.setUpdateInfo(updateRepository.findByUrl(mfLoad.getUrl()));
            mfLoad.setTableInfoList(tableInfoRepository.findAll());
            MediaThread mfThread = new MediaThread(threadGroup, mfLoad, "", false, mediaRepository);
            mediaThreadList.add(mfThread);
        }
    }

    public boolean urlExist(String url) {
        MediaFile mediaFile = mediaRepository.findByUrl(url);
        if (mediaFile == null)
            return false;
        return true;
    }

    public List<MediaThread> getMediaThreadList() {
        return mediaThreadList;
    }

    public ThreadGroup getThreadGroup() {
        return threadGroup;
    }

}
