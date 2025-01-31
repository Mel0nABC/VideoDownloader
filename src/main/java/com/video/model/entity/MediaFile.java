package com.video.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String url;
    private boolean downloaded;
    private int exitCode;
    private String progressDownload;
    private String statusDownload;
    private String fileName;
    
    @OneToOne
    @JoinColumn(name = "update_info_id")
    private UpdateInfo updateInfo;
    private int totalSongs;
    private int downloadedSong;

    {
        progressDownload = "0%";
        statusDownload = "Descarga no iniciada";
    }

    public MediaFile(String url, boolean downloaded, int exitCode, UpdateInfo updateInfo) {
        this.url = url;
        this.downloaded = downloaded;
        this.exitCode = exitCode;
        this.updateInfo = updateInfo;
    }

    public MediaFile() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public UpdateInfo getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(UpdateInfo updateInfo) {
        this.updateInfo = updateInfo;
    }

    public String getProgressDownload() {
        return progressDownload;
    }

    public void setProgressDownload(String progressDownload) {
        this.progressDownload = progressDownload;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "MediaFile [id=" + id + ", url=" + url + ", downloaded=" + downloaded + ", exitCode=" + exitCode
                + ", progressDownload=" + progressDownload + ", fileName=" + fileName + "]";
    }

    public String getStatusDownload() {
        return statusDownload;
    }

    public void setStatusDownload(String statusDownload) {
        this.statusDownload = statusDownload;
    }

    public int getTotalSongs() {
        return totalSongs;
    }

    public void setTotalSongs(int totalSongs) {
        this.totalSongs = totalSongs;
    }

    public int getDownloadedSong() {
        return downloadedSong;
    }

    public void setDownloadedSong(int downloadedSong) {
        this.downloadedSong = downloadedSong;
    }

}
