package com.video.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UpdateInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String url, playlist, playlist_channel, fulltitle, thumbnail;
    private int playlist_count;

    public UpdateInfo() {
    }

    public UpdateInfo(String url, String playlist, int playlist_count, String playlist_channel, String fulltitle,
            String thumbnail) {
        this.url = url;
        this.playlist = playlist;
        this.playlist_count = playlist_count;
        this.playlist_channel = playlist_channel;
        this.fulltitle = fulltitle;
        this.thumbnail = thumbnail;
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

    public String getPlaylist() {
        return playlist;
    }

    public void setPlaylist(String playlist) {
        this.playlist = playlist;
    }

    public String getPlaylist_channel() {
        return playlist_channel;
    }

    public void setPlaylist_channel(String playlist_channel) {
        this.playlist_channel = playlist_channel;
    }

    public String getFulltitle() {
        return fulltitle;
    }

    public void setFulltitle(String fulltitle) {
        this.fulltitle = fulltitle;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getPlaylist_count() {
        return playlist_count;
    }

    public void setPlaylist_count(int playlist_count) {
        this.playlist_count = playlist_count;
    }

}
