package com.video.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.video.model.entity.*;
import com.video.model.service.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MediaController {

    private MediaRepository mediaRepository;

    private ThreadGroup threadGroup = new ThreadGroup("ThreadGroup");
    private ArrayList<MediaThread> mediaThreadList = new ArrayList<>();
    private final int EXIT_CODE_OK = 0;

    public MediaController(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
        addAllDBtothreadlist();
    }

    @GetMapping("/favicon.ico")
    public void handleFavicon(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content
    }

    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    public void addAllDBtothreadlist() {
        for (MediaFile mfLoad : mediaRepository.findAll()) {
            MediaThread mfThread = new MediaThread(threadGroup, mfLoad,
                    mediaRepository, "", false);
            mediaThreadList.add(mfThread);
        }
    }

    /**
     * Devuelve lista actualizada
     * 
     * @return lista startedMediaThreadList
     */
    @PostMapping("/getInfo")
    public ResponseEntity<ArrayList<MediaThread>> getList() {
        return ResponseEntity.ok(mediaThreadList);
    }

    @PostMapping("/addDownload")
    public ResponseEntity<Object> addDownload(@RequestParam("url") String url) {

        if (urlExist(url))
            return ResponseEntity.ok("error");

        String jsonData = getVideoMetadata(url);

        if (jsonData.equals("false"))
            return ResponseEntity.ok("error");

        MediaFile newMFile = addUrlBBDD(url, jsonData);

        MediaThread mfThread = new MediaThread(threadGroup, newMFile,
                mediaRepository, "", false);

        mediaThreadList.add(mfThread);

        return ResponseEntity.ok(mfThread);
    }

    public boolean urlExist(String url) {
        MediaFile mediaFile = mediaRepository.findByUrl(url);
        if (mediaFile == null)
            return false;
        return true;
    }

    public String getVideoMetadata(String url) {
        return new ExecuteYtdlp().getVideoMetadata(url);
    }

    public MediaFile addUrlBBDD(String url, String jsonData) {
        MediaFile mfBBDD = new MediaFile(url, false, EXIT_CODE_OK, jsonData);
        mediaRepository.save(mfBBDD);
        mfBBDD = mediaRepository.findByUrl(mfBBDD.getUrl());
        return mfBBDD;
    }

    @PostMapping("/getUrl")
    public ResponseEntity<Object> getUrl(@RequestParam("url") String url) {
        MediaFile mfInfo = mediaRepository.findByUrl(url);
        if (mfInfo == null)
            return ResponseEntity.ok(null);
        return ResponseEntity.ok(mfInfo);
    }

    @PostMapping("/getAllURL")
    public ResponseEntity<List<MediaFile>> firstLoad() {
        List<MediaFile> listaMF = mediaRepository.findAll();
        return ResponseEntity.ok(listaMF);
    }

    @PostMapping("/download")
    public ResponseEntity<Map<String, Object>> download(@RequestParam("url") String url,
            @RequestParam("formatId") String formatId) {

        Map<String, Object> contenido = new HashMap<>();

        MediaThread mediaThread = null;
        MediaFile mediaFile = null;

        for (int i = 0; i < mediaThreadList.size(); i++) {

            mediaThread = mediaThreadList.get(i);

            if (mediaThread.getMediaFile().getUrl().equals(url)) {
                mediaFile = mediaThread.getMediaFile();
                mediaThreadList.remove(i);
            }
        }
        mediaFile.setExitCode(EXIT_CODE_OK);
        mediaThread.setFormatId(formatId);
        mediaThread = new MediaThread(threadGroup, mediaFile, mediaRepository, formatId, false);
        mediaThread.start();

        mediaThreadList.add(mediaThread);

        contenido.put("mediaFile", mediaFile);
        return ResponseEntity.ok(contenido);
    }

    /**
     * Elimina de bbdd y startedMediaThreadList un MediaThread.
     * 
     * @param url url del MediaFile dentro de MediaThread
     * @return true cuando lo ha eliminado, false si ha ocurrido otra cosa.
     */
    @PostMapping("/delByUrl")
    public ResponseEntity<String> delByUrlWeb(@RequestParam("url") String url) {

        MediaFile mfToDelete = mediaRepository.findByUrl(url);
        if (mfToDelete != null) {

            MediaThread tmpDelete = null;
            for (MediaThread mtDelete : mediaThreadList) {
                if (mtDelete.getMediaFile().getUrl().equals(mfToDelete.getUrl())) {
                    tmpDelete = mtDelete;
                    break;
                }
            }
            mediaThreadList.remove(tmpDelete);
            mediaRepository.delete(mfToDelete);
            return ResponseEntity.ok("true");
        }
        return ResponseEntity.ok("false");
    }

    public boolean delThreadFromList(MediaFile mfToDel) {
        for (MediaThread mt : mediaThreadList) {
            if (mt.getMediaFile().getUrl().equals(mfToDel.getUrl())) {
                mediaThreadList.remove(mt);
                return true;
            }
        }
        return false;
    }

    @PostMapping("/stopThread")
    public ResponseEntity<Boolean> stopThreadWeb(@RequestParam("url") String url) {
        System.out.println("STOP THREAD");
        for (MediaThread tr : mediaThreadList) {
            if (tr != null) {
                if (tr.getMediaFile().getUrl().equals(url)) {
                    tr.interrupt();
                    try {
                        tr.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!tr.isAlive()) {
                        return ResponseEntity.ok(true);
                    }

                }
            }
        }
        return ResponseEntity.ok(false);
    }

    @PostMapping("/checkYtUpdate")
    public ResponseEntity<YtdlpUpdateInfo> checkYtUpdate() {
        return ResponseEntity.ok(new ExecuteYtdlp().getRelease());
    }

    @Bean
    public ArrayList<MediaThread> getMediaThreadList() {
        return mediaThreadList;
    }

}
