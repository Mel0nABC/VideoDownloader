package com.pruebas;

import org.springframework.beans.factory.annotation.Autowired;

public class MediaThread extends Thread {
    private Double segundos = 0.0;
    private long porcentaje = 0;
    private MediaFile mediaFile;

    private MediaRepository mediaRepository;

    public MediaThread(ThreadGroup threadGroup, MediaFile mediaFile, MediaRepository mediaRepository) {
        super(threadGroup, mediaFile.getUrl());
        this.mediaFile = mediaFile;
        this.mediaRepository = mediaRepository;
    }

    @Override
    public void run() {
        while (segundos != 120) {
            try {
                Thread.sleep(1000);
                segundos++;
                porcentaje = Math.round(segundos / 1.2);
                // System.out.println("Porcentaje -> " + porcentaje);

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        mediaFile.setDownloaded(true);
        mediaRepository.delete(mediaFile);
        System.out.println("TAMAÑO DE BBDD --> " + mediaRepository.findAll().size());

    }

    public Double getSegundos() {
        return segundos;
    }

    public MediaFile getMediaFile() {
        return mediaFile;
    }

    public long getPorcentaje() {
        return porcentaje;
    }

    public void setMediaFile(MediaFile mediaFile) {
        this.mediaFile = mediaFile;
    }

}
