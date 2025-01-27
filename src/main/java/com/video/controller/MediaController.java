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
    private ArrayList<MediaThread> startedMediaThreadList = new ArrayList<>();
    private ArrayList<MediaThread> stopedMediaThreadList = new ArrayList<>();
    private final int EXIT_CODE_OK = 0;

    public MediaController(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
        addAllDBtothreadlist();
    }

    public void addAllDBtothreadlist() {
        for (MediaFile mfLoad : mediaRepository.findAll()) {
            MediaThread mfThread = new MediaThread(threadGroup, mfLoad,
                    mediaRepository, "", this);
            stopedMediaThreadList.add(mfThread);
        }
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

        MediaFile newMFile = addUrlBBDD(url, jsonData);

        MediaThread mfThread = new MediaThread(threadGroup, newMFile,
                mediaRepository, "", this);

        stopedMediaThreadList.add(mfThread);

        return ResponseEntity.ok(newMFile);
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
        // MediaFile mfBBDD = mediaRepository.findByUrl(url);

        // if (mfBBDD == null) {
        // contenido.put("mediaFile", "Error, url no existe");
        // return ResponseEntity.ok(contenido);
        // }

        // if (mfBBDD.getDownloaded() == true) {
        // contenido.put("mediaFile", "Error, contenido ya descargado");
        // return ResponseEntity.ok(contenido);
        // }

        MediaThread delOnStopeds = null;
        MediaThread toStart = null;
        for (MediaThread mtCheck : stopedMediaThreadList) {
            if (mtCheck.getMediaFile().getUrl().equals(url)) {
                delOnStopeds = mtCheck;
                toStart = mtCheck;
                startedMediaThreadList.add(mtCheck);
            }
        }

        stopedMediaThreadList.remove(delOnStopeds);
        MediaFile mfBBDD = toStart.getMediaFile();
        mfBBDD.setExitCode(EXIT_CODE_OK);
        toStart.setFormatId(formatId);
        toStart.start();

        contenido.put("mediaFile", mfBBDD);

        return ResponseEntity.ok(contenido);
    }

    /**
     * Devuelve lista actualizada
     * 
     * @return lista startedMediaThreadList
     */
    @PostMapping("/getInfo")
    public ResponseEntity<ArrayList<ArrayList<MediaThread>>> getList() {
        ArrayList<ArrayList<MediaThread>> threadsContainer = new ArrayList<>();
        threadsContainer.add(startedMediaThreadList);
        threadsContainer.add(stopedMediaThreadList);
        return ResponseEntity.ok(threadsContainer);
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
            for (MediaThread mtDelete : stopedMediaThreadList) {
                if (mtDelete.getMediaFile().getUrl().equals(mfToDelete.getUrl())) {
                    tmpDelete = mtDelete;
                    break;
                }
            }
            stopedMediaThreadList.remove(tmpDelete);
            mediaRepository.delete(mfToDelete);
            return ResponseEntity.ok("true");
        }
        return ResponseEntity.ok("false");
    }

    public boolean delThreadFromList(MediaFile mfToDel) {
        for (MediaThread mt : startedMediaThreadList) {
            if (mt.getMediaFile().getUrl().equals(mfToDel.getUrl())) {
                startedMediaThreadList.remove(mt);
                stopedMediaThreadList.add(mt);
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

            for (MediaThread mt : startedMediaThreadList) {
                if (mt.getMediaFile().getUrl().equals(url)) {
                    startedMediaThreadList.remove(mt);
                    stopedMediaThreadList.add(mt);
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
