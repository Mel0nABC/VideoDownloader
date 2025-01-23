package com.video.controller;

import com.video.model.entity.*;
import com.video.model.service.*;

import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.Pipe.SourceChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    }

    @GetMapping("/favicon.ico")
    public void handleFavicon(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content
    }

    @GetMapping("/")
    public String inicio() {
        printAllURLS();
        return "index";
    }

    @PostMapping("/checkUrlExist")
    public ResponseEntity<Boolean> checkUrlExist(@RequestParam("url") String url) {
        System.out.println("/checkUrlExist --> " + url);
        MediaFile mediaFile = mediaRepository.findByUrl(url);

        if (mediaFile == null)
            return ResponseEntity.ok(false);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/getAllURL")
    public ResponseEntity<List<MediaFile>> firstLoad() {
        List<MediaFile> listaMF = mediaRepository.findAll();
        return ResponseEntity.ok(listaMF);
    }

    @PostMapping("/getVideoMetada")
    public ResponseEntity<String> getVideoMetadata(@RequestParam("url") String url) {
        System.out.println("/getVideoMetada --> " + url);
        return ResponseEntity.ok(new ExecuteYtdlp().getVideoMetadata(url));
    }

    @PostMapping("/addUrlBBDD")
    public ResponseEntity<Map<String, Object>> addUrlBBDD(@RequestParam("url") String url,
            @RequestParam("jsonData") String jsonData) {
        System.out.println("/addUrlBBDD --> " + url);
        Map<String, Object> contenido = new HashMap<>();
        MediaFile mfBBDD = mediaRepository.findByUrl(url);
        if (mfBBDD != null) {
            mfBBDD.setExitCode(EXIT_CODE_OK);
            mfBBDD.setDownloaded(true);
            contenido.put("mediaFile", mfBBDD);
            return ResponseEntity.ok(contenido);
        }

        mfBBDD = new MediaFile(url, false, EXIT_CODE_OK, jsonData);
        mediaRepository.save(mfBBDD);
        mfBBDD = mediaRepository.findByUrl(mfBBDD.getUrl());
        contenido.put("mediaFile", mfBBDD);
        return ResponseEntity.ok(contenido);
    }

    @PostMapping("/download")
    public ResponseEntity<Map<String, Object>> download(@RequestParam("url") String url) {
        List<String> aditionalParamList = new ArrayList<>();

        // if (idDownload != "null") {
        // String idDownloadFiltered = idDownload;
        // // if (idDownload.contains(" "))
        // idDownloadFiltered = idDownload.split(" ")[0];

        // aditionalParamList.add("-f");
        // aditionalParamList.add(idDownloadFiltered);
        // }

        Map<String, Object> contenido = new HashMap<>();
        MediaFile mfBBDD = mediaRepository.findByUrl(url);
        mfBBDD.setExitCode(EXIT_CODE_OK);

        contenido.put("mediaFile", mfBBDD);

        MediaThread mfThread = new MediaThread(threadGroup, mfBBDD,
                mediaRepository, null, null, aditionalParamList);

        mediaThreadList.add(mfThread);
        mfThread.start();

        contenido.put("mediaFile", mfBBDD);

        return ResponseEntity.ok(contenido);
    }

    /**
     * Devuelve lista actualizada
     * 
     * @return lista mediaThreadList
     */
    @PostMapping("/getInfo")
    public ResponseEntity<ArrayList<MediaThread>> getList() {
        return ResponseEntity.ok(mediaThreadList);
    }

    /**
     * Elimina de bbdd y mediaThreadList un MediaThread.
     * 
     * @param url url del MediaFile dentro de MediaThread
     * @return true cuando lo ha eliminado, false si ha ocurrido otra cosa.
     */
    @PostMapping("/delByUrl")
    public ResponseEntity<String> delByUrlWeb(@RequestParam("url") String url) {

        MediaFile mfToDelete = mediaRepository.findByUrl(url);

        if (mfToDelete != null) {
            mediaRepository.delete(mfToDelete);
            return ResponseEntity.ok("true");
        }
        return ResponseEntity.ok("false");
    }

    public boolean delByUrlFromThreadList(String url) {
        MediaFile mfToDelete = mediaRepository.findByUrl(url);
        if (mfToDelete != null) {

            for (MediaThread mt : mediaThreadList) {
                if (mt.getMediaFile().getUrl().equals(url)) {
                    mediaThreadList.remove(mt);
                    return true;
                }
            }
        }
        return false;
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
    public ResponseEntity<String> stopThreadWeb(@RequestParam("url") String url) {

        if (!stopThread(url))
            return ResponseEntity.ok("false");

        if (!delByUrlFromThreadList(url))
            return ResponseEntity.ok("false");

        return ResponseEntity.ok("true");
    }

    public boolean stopThread(String url) {
        MediaThread[] lista = new MediaThread[threadGroup.activeCount()];
        threadGroup.enumerate(lista);

        for (MediaThread tr : lista) {
            if (tr != null) {
                if (tr.getMediaFile().getUrl().equals(url)) {
                    tr.interrupt();
                    try {
                        tr.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!tr.isAlive()) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    @PostMapping("/checkYtUpdate")
    public ResponseEntity<YtdlpUpdateInfo> checkYtUpdate() {
        return ResponseEntity.ok(new ExecuteYtdlp().getRelease());
    }

    public void printAllURLS() {
        System.out.println("################ URLS AÑADIDOS ################");
        for (MediaFile mf : mediaRepository.findAll()) {
            System.out.println(mf.getId() + " - " + mf.getUrl());
        }
        System.out.println("################################################");
    }
}
