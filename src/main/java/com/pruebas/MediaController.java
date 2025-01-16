package com.pruebas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

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
    public ResponseEntity<List<MediaFile>> firstLoad() {
        List<MediaFile> listaMF = mediaRepository.findAll();
        return ResponseEntity.ok(listaMF);
    }

    @PostMapping("/download")
    public ResponseEntity<Map<String, Object>> download(@RequestParam("url") String url,
            @RequestParam("soloAudio") Boolean soloAudio,
            @RequestParam("audioFormatMp3") Boolean audioFormatMp3) {

        Map<String, Object> contenido = new HashMap<>();
        MediaFile mediaFileBBDD = mediaRepository.findByUrl(url);
        if (mediaFileBBDD != null) {
            mediaFileBBDD.setExitCode("Archivo en lista");
            System.out.println("Downloaded -> "+mediaFileBBDD.getDownloaded());
            contenido.put("respuesta", false);
            contenido.put("mediaFile", mediaFileBBDD);
            return ResponseEntity.ok(contenido);
        }

        mediaRepository.save(new MediaFile(url, false));
        mediaFileBBDD = mediaRepository.findByUrl(url);

        MediaThread mfThread = new MediaThread(threadGroup, mediaFileBBDD, mediaRepository, soloAudio, audioFormatMp3);
        mfThread.start();

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

    @PostMapping("/delByUrl")
    public ResponseEntity<Boolean> postMethodName(@RequestParam("url") String url) {
        System.out.println("URL -> " + url);

        MediaFile mfToDelete = mediaRepository.findByUrl(url);

        if (mfToDelete != null) {
            mediaRepository.deleteById(mfToDelete.getId());
            return ResponseEntity.ok(true);
        }

        return ResponseEntity.ok(false);
    }

}
