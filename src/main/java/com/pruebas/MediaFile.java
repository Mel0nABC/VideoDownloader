package com.pruebas;

import java.time.LocalTime;

import org.springframework.boot.context.properties.bind.ConstructorBinding;

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
    private String tittle;
    private double size;
    private LocalTime duration;
    private boolean downloaded;

    @ConstructorBinding
    public MediaFile(long id, String url, String tittle, double size, LocalTime duration) {
        this.id = id;
        this.url = url;
        this.tittle = tittle;
        this.size = size;
        this.duration = duration;
        this.downloaded = false;
    }

    public MediaFile(long id, String url, String tittle, double size) {
        this.id = id;
        this.url = url;
        this.tittle = tittle;
        this.size = size;
        this.downloaded = false;
    }

    public MediaFile(long id, String url, String tittle) {
        this.id = id;
        this.url = url;
        this.tittle = tittle;
        this.downloaded = false;
    }

    public MediaFile(long id, String url) {
        this.id = id;
        this.url = url;
        this.downloaded = false;
    }

    public MediaFile(String url) {
        this.url = url;
        this.downloaded = false;
    }

    public MediaFile() {
        this.downloaded = false;
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

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public LocalTime getDuration() {
        return duration;
    }

    public void setDuration(LocalTime duration) {
        this.duration = duration;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", tittle='" + tittle + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                '}';
    }

}
