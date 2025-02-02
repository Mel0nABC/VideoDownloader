package com.video.controller;

import com.video.model.entity.*;
import com.video.service.*;
import com.video.util.MediaThread;
import com.video.util.YtdlpUpdateInfo;

import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MediaController {

    private MediaFileService mediaFileService;
    private MediaThreadService mediaThreadService;
    private UpdateInfoService updateInfoService;
    private ExecuteYtdlpService executeYtdlpService;

    public MediaController(MediaFileService mediaFileService, MediaThreadService mediaThreadService,
            UpdateInfoService updateInfoService, ExecuteYtdlpService executeYtdlpService) {
        this.mediaThreadService = mediaThreadService;
        this.executeYtdlpService = executeYtdlpService;
        this.mediaFileService = mediaFileService;
        this.updateInfoService = updateInfoService;
    }

    @GetMapping("/favicon.ico")
    public void handleFavicon(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @PostMapping("/addDownload")
    public ResponseEntity<MediaThread> addDownload(@RequestParam("url") String url) {
        String jsonData = executeYtdlpService.getVideoMetadata(url);
        UpdateInfo updateInfo = updateInfoService.getInfoDownload(url, jsonData);
        List<TableInfo> tableInfoList = updateInfoService.getTableInfo(url, jsonData);
        MediaFile newMFile = mediaFileService.addUrlBBDD(url, updateInfo, tableInfoList);
        return ResponseEntity.ok(mediaThreadService.addDownload(url, jsonData, newMFile, updateInfo, tableInfoList));
    }

    @PostMapping("/download")
    public ResponseEntity<MediaFile> download(@RequestParam("url") String url,
            @RequestParam("formatId") String formatId) {
        return ResponseEntity.ok(mediaThreadService.download(url, formatId));
    }

    @PostMapping("/getTableInfo")
    public ResponseEntity<List<TableInfo>> getTableInfo(@RequestParam("url") String url) {
        Optional<List<TableInfo>> tableInfo = mediaFileService.getTableInfo(url);
        if (tableInfo.isEmpty()) {
            List<TableInfo> tableErrorList = new ArrayList<>();
            TableInfo tableError = new TableInfo();
            tableError.setStatusMsg("No se encontraron formatos,utilice descarga directa.");
            tableErrorList.add(tableError);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(tableErrorList);
        }

        return ResponseEntity.ok(tableInfo.get());
    }

    @DeleteMapping("/delByUrl")
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
