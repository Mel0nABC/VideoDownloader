package com.video.controller;

import com.video.model.entity.*;
import com.video.model.service.*;

import jakarta.servlet.http.HttpServletResponse;

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
        return "index";
    }

    @PostMapping("/addDownload")
    public ResponseEntity<Object> addDownload(@RequestParam("url") String url) {
        if (urlExist(url))
            return ResponseEntity.ok("error");

        String jsonData = getVideoMetadata(url);

        if (jsonData.equals("false"))
            return ResponseEntity.ok("error");

        return ResponseEntity.ok(addUrlBBDD(url, jsonData));
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
        List<String> aditionalParamList = new ArrayList<>();

        Map<String, Object> contenido = new HashMap<>();
        MediaFile mfBBDD = mediaRepository.findByUrl(url);

        if (mfBBDD == null) {
            contenido.put("mediaFile", "Error, url no existe");
            return ResponseEntity.ok(contenido);
        }

        if (mfBBDD.getDownloaded() == true) {
            contenido.put("mediaFile", "Error, contenido ya descargado");
            return ResponseEntity.ok(contenido);
        }

        MediaThread mfThread = new MediaThread(threadGroup, mfBBDD,
                mediaRepository, formatId, this);

        for (MediaThread mtCheck : mediaThreadList) {
            if (mtCheck.getMediaFile().getUrl().equals(url)) {
                contenido.put("mediaFile", "Error, descarga ya iniciada.");
                return ResponseEntity.ok(contenido);
            }
        }

        mfBBDD.setExitCode(EXIT_CODE_OK);
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
        return ResponseEntity.ok(stopThreadTotal(url));
    }

    public String stopThreadTotal(String url) {
        if (!stopThread(url)) {
            return "false";
        }

        if (!delByUrlFromThreadList(url)) {
            return "false";
        }

        return "true";
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

    @PostMapping("/checkYtUpdate")
    public ResponseEntity<YtdlpUpdateInfo> checkYtUpdate() {
        return ResponseEntity.ok(new ExecuteYtdlp().getRelease());
    }
}
