package com.video;

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

    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    @PostMapping("/firstLoad")
    public ResponseEntity<List<MediaFile>> firstLoad() {
        List<MediaFile> listaMF = mediaRepository.findAll();
        return ResponseEntity.ok(listaMF);
    }

    @PostMapping("/download")
    public ResponseEntity<Map<String, Object>> download(@RequestParam("url") String url,
            @RequestParam("soloAudio") Boolean soloAudio,
            @RequestParam("audioFormatMp3") Boolean audioFormatMp3) {

        Map<String, Object> contenido = new HashMap<>();
        MediaFile mfBBDD = mediaRepository.findByUrl(url);
        if (mfBBDD != null) {
            mfBBDD.setExitCode(EXIT_CODE_OK);
            mfBBDD.setDownloaded(true);
            contenido.put("mediaFile", mfBBDD);
            return ResponseEntity.ok(contenido);
        }

        mfBBDD = new MediaFile(url, false, EXIT_CODE_OK);
        mediaRepository.save(mfBBDD);
        mfBBDD = mediaRepository.findByUrl(mfBBDD.getUrl());

        MediaThread mfThread = new MediaThread(threadGroup, mfBBDD,
                mediaRepository, soloAudio, audioFormatMp3);

        mediaThreadList.add(mfThread);
        mfThread.start();

        contenido.put("mediaFile", mfBBDD);

        return ResponseEntity.ok(contenido);
    }

    @PostMapping("/getInfo")
    public ResponseEntity<ArrayList<MediaThread>> getList() {
        return ResponseEntity.ok(mediaThreadList);
    }

    @PostMapping("/delByUrl")
    public ResponseEntity<String> delByUrl(@RequestParam("url") String url) {

        MediaFile mfToDelete = mediaRepository.findByUrl(url);

        if (mfToDelete != null) {
            mediaRepository.deleteById(mfToDelete.getId());
            return ResponseEntity.ok("true");
        }

        return ResponseEntity.ok("false");
    }

    public void deleteMediaThread(MediaFile mfToDelete) {

        Thread toDelete = null;

        for (MediaThread mt : mediaThreadList)
            if (mt.getMediaFile().getUrl().equals(mfToDelete.getUrl())) {
                toDelete = mt;
            }

        mediaThreadList.remove(toDelete);

    }

    @PostMapping("/stopThread")
    public ResponseEntity<String> stopThread(@RequestParam("url") String url) {
        threadGroup.activeCount();
        Thread[] lista = new Thread[threadGroup.activeCount()];
        threadGroup.enumerate(lista);

        for (Thread tr : lista) {
            if (tr != null) {
                tr.interrupt();
                try {
                    tr.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!tr.isAlive())
                    mediaThreadList.remove(tr);
            }
        }
        return ResponseEntity.ok("true");
    }

}
