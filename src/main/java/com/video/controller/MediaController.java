package com.video.controller;

import com.video.model.entity.*;
import com.video.service.*;
import com.video.util.MediaThread;
import com.video.util.YtdlpUpdateInfo;

import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MediaController {

    private MediaFileService mediaFileService;
    private MediaThreadService mediaThreadService;
    private UpdateInfoService updateInfoService;
    private ExecuteYtdlpService executeYtdlpService;
    private LocalIPService localIPService;

    public MediaController(MediaFileService mediaFileService, MediaThreadService mediaThreadService,
            UpdateInfoService updateInfoService, ExecuteYtdlpService executeYtdlpService,
            LocalIPService localIPService) {
        this.mediaThreadService = mediaThreadService;
        this.executeYtdlpService = executeYtdlpService;
        this.mediaFileService = mediaFileService;
        this.updateInfoService = updateInfoService;
        this.localIPService = localIPService;
    }

    @GetMapping("/favicon.ico")
    public void handleFavicon(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    // @GetMapping("/")
    // public String inicio() {
    //     return "index";
    // }

    @PostMapping("/addDownload")
    public ResponseEntity<Object> addDownload(@RequestParam("url") String url) {
        String jsonData = executeYtdlpService.getVideoMetadata(url);
        UpdateInfo updateInfo = updateInfoService.getInfoDownload(url, jsonData);
        ArrayList<TableInfo> tableInfoList = updateInfoService.getTableInfo(url, jsonData);
        MediaFile newMFile = mediaFileService.addUrlBBDD(url, updateInfo, tableInfoList);
        return ResponseEntity.ok(mediaThreadService.addDownload(url, jsonData, newMFile, updateInfo, tableInfoList));
    }

    @PostMapping("/download")
    public ResponseEntity<Map<String, Object>> download(@RequestParam("url") String url,
            @RequestParam("formatId") String formatId) {
        return ResponseEntity.ok(mediaThreadService.download(url, formatId));
    }

    @PostMapping("/getUrl")
    public ResponseEntity<Object> getUrl(@RequestParam("url") String url) {
        return ResponseEntity.ok(mediaFileService.getUrl(url));
    }

    @PostMapping("/getAllURL")
    public ResponseEntity<List<MediaFile>> getAllUrl() {
        return ResponseEntity.ok(mediaFileService.getAllUrl());
    }

    @PostMapping("/getInfo")
    public ResponseEntity<ArrayList<MediaThread>> getList() {
        return ResponseEntity.ok(mediaThreadService.getList());
    }

    @PostMapping("/getLocalIp")
    public ResponseEntity<String> getLocalIp() {
        return ResponseEntity.ok(localIPService.getLocalIp());
    }

    @PostMapping("/delByUrl")
    public ResponseEntity<Boolean> delByUrlWeb(@RequestParam("url") String url) {
        return ResponseEntity.ok(mediaThreadService.delByUrlWeb(url));
    }

    @PostMapping("/stopThread")
    public ResponseEntity<Boolean> stopThreadWeb(@RequestParam("url") String url) {
        return ResponseEntity.ok(mediaThreadService.stopThreadWeb(url));
    }

    @PostMapping("/checkYtUpdate")
    public ResponseEntity<YtdlpUpdateInfo> checkYtUpdate() {
        return ResponseEntity.ok(new ExecuteYtdlpService().getRelease());
    }

}
