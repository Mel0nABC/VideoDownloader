package com.video.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TableInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String url, format_id, ext, resolution, acodec, format_note, statusMsg;
    private long filesize, tbr, abr, vbr;

    public TableInfo() {

    }

    public TableInfo(String url, String format_id, String ext, String resolution, long filesize, long tbr,
            String acodec, long abr, String format_note, long vbr, String statusMsg) {
        this.url = url;
        this.format_id = format_id;
        this.ext = ext;
        this.resolution = resolution;
        this.filesize = filesize;
        this.tbr = tbr;
        this.acodec = acodec;
        this.abr = abr;
        this.format_note = format_note;
        this.vbr = vbr;
        this.statusMsg = statusMsg;
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

    public String getFormat_id() {
        return format_id;
    }

    public void setFormat_id(String format_id) {
        this.format_id = format_id;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getAcodec() {
        return acodec;
    }

    public void setAcodec(String acodec) {
        this.acodec = acodec;
    }

    public String getFormat_note() {
        return format_note;
    }

    public void setFormat_note(String format_note) {
        this.format_note = format_note;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public long getTbr() {
        return tbr;
    }

    public void setTbr(long tbr) {
        this.tbr = tbr;
    }

    public long getAbr() {
        return abr;
    }

    public void setAbr(long abr) {
        this.abr = abr;
    }

    public long getVbr() {
        return vbr;
    }

    public void setVbr(long vbr) {
        this.vbr = vbr;
    }
}
