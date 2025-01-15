package com.pruebas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MediaController {

    @Autowired
    private MediaRepository mediaRepository;

    private ThreadGroup threadGroup = new ThreadGroup("ThreadGroup");

    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    @PostMapping("/firstLoad")
    public ResponseEntity<ArrayList<MediaThread>> firstLoad() {

        MediaThread[] threads = new MediaThread[threadGroup.activeCount()];
        threadGroup.enumerate(threads);
        ArrayList<MediaThread> listaString = new ArrayList<>();

        for (MediaThread t : threads) {
            if (t != null) {
                listaString.add(t);
            }

        }
        return ResponseEntity.ok(listaString);
    }

    @PostMapping("/download")
    public ResponseEntity<Map<String, Object>> download(@RequestParam("url") String url) {

        mediaRepository.save(new MediaFile(url));
        MediaFile mediaFileBBDD = mediaRepository.findByUrl(url);

        MediaThread mfThread = new MediaThread(threadGroup, mediaFileBBDD, mediaRepository);
        mfThread.start();

        Map<String, Object> contenido = new HashMap<>();
        contenido.put("respuesta", true);
        contenido.put("mediaFile", mediaFileBBDD);

        return ResponseEntity.ok(contenido);
    }

    @PostMapping("/getInfo")
    public ResponseEntity<ArrayList<MediaThread>> getList() {

        MediaThread[] threads = new MediaThread[threadGroup.activeCount()];
        threadGroup.enumerate(threads);
        ArrayList<MediaThread> listaString = new ArrayList<>();

        for (MediaThread t : threads) {
            if (t != null) {
                listaString.add(t);
            }

        }
        return ResponseEntity.ok(listaString);
    }

}
