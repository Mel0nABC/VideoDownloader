package com.video.model.entity;


import org.springframework.boot.context.properties.bind.ConstructorBinding;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String url;
    private boolean downloaded;
    private int exitCode;
    @Column(columnDefinition = "CLOB")
    private String jsonData;

    @ConstructorBinding
    public MediaFile(long id, String url, boolean downloaded, int exitCode) {
        this.id = id;
        this.url = url;
        this.downloaded = downloaded;
        this.exitCode = exitCode;
    }

    public MediaFile(String url, boolean downloaded, int exitCode, String jsonData) {
        this.url = url;
        this.downloaded = downloaded;
        this.exitCode = exitCode;
        this.jsonData = jsonData;

        System.out.println(toString());
    }

    public MediaFile(String url, boolean downloaded, int exitCode) {
        this.url = url;
        this.downloaded = downloaded;
        this.exitCode = exitCode;
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

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    @Override
    public String toString() {
        return "MediaFile [id=" + id + ", url=" + url + ", downloaded=" + downloaded + ", exitCode=" + exitCode
                + ", jsonData=" + jsonData + "]";
    }

}
