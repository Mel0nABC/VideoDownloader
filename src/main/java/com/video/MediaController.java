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
        if (delByUrlFromListAndBBDD(url))
            return ResponseEntity.ok("true");

        return ResponseEntity.ok("false");
    }

    public boolean delByUrlFromListAndBBDD(String url) {
        MediaFile mfToDelete = mediaRepository.findByUrl(url);
        if (mfToDelete != null) {

            for (MediaThread mt : mediaThreadList) {
                if (mt.getMediaFile().getUrl().equals(url)) {
                    mediaThreadList.remove(mt);
                    mediaRepository.deleteById(mfToDelete.getId());
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

        if (!delByUrlFromListAndBBDD(url))
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
                        System.out.println("MUERTO!");
                        return true;
                    }

                }
            }
        }
        return false;
    }

    @PostMapping("/checkYtUpdate")
    public String checkYtUpdate() {

        return "up to date";
    }

}
