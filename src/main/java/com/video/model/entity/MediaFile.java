package com.video.model.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
    private int totalSongs;
    private int downloadedSong;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "update_info_id")
    private UpdateInfo updateInfo;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "table_info_id")
    private List<TableInfo> tableInfoList;

    {
        progressDownload = "0%";
        statusDownload = "Descarga no iniciada";
    }

    public MediaFile() {

    }

    public MediaFile(String url, boolean downloaded, int exitCode, UpdateInfo updateInfo) {
        this.url = url;
        this.downloaded = downloaded;
        this.exitCode = exitCode;
        this.updateInfo = updateInfo;
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

    public boolean isDownloaded() {
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

    public String getProgressDownload() {
        return progressDownload;
    }

    public void setProgressDownload(String progressDownload) {
        this.progressDownload = progressDownload;
    }

    public String getStatusDownload() {
        return statusDownload;
    }

    public void setStatusDownload(String statusDownload) {
        this.statusDownload = statusDownload;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public UpdateInfo getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(UpdateInfo updateInfo) {
        this.updateInfo = updateInfo;
    }

    public List<TableInfo> getTableInfoList() {
        return tableInfoList;
    }

    public void setTableInfoList(List<TableInfo> tableInfoList) {
        this.tableInfoList = tableInfoList;
    }

}
