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
    private String exitCode;

    @ConstructorBinding
    public MediaFile(long id, String url, String tittle, double size, LocalTime duration, Boolean Downloaded) {
        this.id = id;
        this.url = url;
        this.tittle = tittle;
        this.size = size;
        this.duration = duration;
        this.downloaded = Downloaded;
    }

    public MediaFile(long id, String url, String tittle, double size, Boolean Downloaded) {
        this.id = id;
        this.url = url;
        this.tittle = tittle;
        this.size = size;
        this.downloaded = Downloaded;
    }

    public MediaFile(long id, String url, String tittle, Boolean Downloaded) {
        this.id = id;
        this.url = url;
        this.tittle = tittle;
        this.downloaded = Downloaded;
    }

    public MediaFile(long id, String url, Boolean Downloaded) {
        this.id = id;
        this.url = url;
        this.downloaded = Downloaded;
    }

    public MediaFile(String url, Boolean Downloaded) {
        this.url = url;
        this.downloaded = Downloaded;
    }

    public MediaFile(Boolean Downloaded) {
        this.downloaded = Downloaded;
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

    public boolean getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public String getExitCode() {
        return exitCode;
    }

    public void setExitCode(String exitCode) {
        this.exitCode = exitCode;
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
