package com.pruebas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaThread extends Thread {
    private Double segundos = 0.0;
    private String porcentaje;
    private MediaFile mediaFile;
    private ProcessBuilder processBuilder;
    private Process process;
    private BufferedReader reader;

    private MediaRepository mediaRepository;

    public MediaThread(ThreadGroup threadGroup, MediaFile mediaFile, MediaRepository mediaRepository) {
        super(threadGroup, mediaFile.getUrl());
        this.mediaFile = mediaFile;
        this.mediaRepository = mediaRepository;
    }

    @Override
    public void run() {
        processBuilder = new ProcessBuilder("yt-dlp", "-x", "--audio-format", "mp3", mediaFile.getUrl());
        try {
            process = processBuilder.start();
            System.out.println("START");
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            System.out.println("READER");
            String line;
            while ((line = reader.readLine()) != null) {

                String regex = "\\[(.*?)]";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(line);
                System.out.println(line);
                while (matcher.find()) {
                    String statusString = matcher.group(1);
                    if (statusString.equals("download")) {
                        try {
                            if (line.length() > 16) {

                                porcentaje = line.substring(11, 14).strip() + "%";

                                if (porcentaje.isBlank() | porcentaje.isEmpty())
                                    porcentaje = "1%";

                                if (porcentaje.equals("100%"))
                                    porcentaje = "Recoding";
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            int exitCode = process.waitFor();
            System.out.println("El proceso terminó con el código de salida: " + exitCode);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

    public String getPorcentaje() {
        return porcentaje;
    }

    public void setMediaFile(MediaFile mediaFile) {
        this.mediaFile = mediaFile;
    }

}
