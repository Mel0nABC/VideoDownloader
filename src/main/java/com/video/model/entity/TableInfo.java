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

    private String url, format_id, ext, resolution, acodec, format_note;
    private double filesize, tbr, abr, vbr;

    public TableInfo() {

    }

    public TableInfo(String url, String format_id, String ext, String resolution, double filesize, double tbr,
            String acodec,
            double abr, String format_note, double vbr) {
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

    public double getFilesize() {
        return filesize;
    }

    public void setFilesize(double filesize) {
        this.filesize = filesize;
    }

    public double getTbr() {
        return tbr;
    }

    public void setTbr(double tbr) {
        this.tbr = tbr;
    }

    public String getAcodec() {
        return acodec;
    }

    public void setAcodec(String acodec) {
        this.acodec = acodec;
    }

    public double getAbr() {
        return abr;
    }

    public void setAbr(double abr) {
        this.abr = abr;
    }

    public String getFormat_note() {
        return format_note;
    }

    public void setFormat_note(String format_note) {
        this.format_note = format_note;
    }

    @Override
    public String toString() {
        return "TableInfo [id=" + id + ", format_id=" + format_id + ", ext=" + ext + ", resolution=" + resolution
                + ", filesize=" + filesize + ", tbr=" + tbr + ", acodec=" + acodec + ", abr=" + abr + ", format_note="
                + format_note + "]";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getVbr() {
        return vbr;
    }

    public void setVbr(double vbr) {
        this.vbr = vbr;
    }
}
