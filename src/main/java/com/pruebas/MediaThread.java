package com.pruebas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaThread extends Thread {
    private Double segundos = 0.0;
    private String status;
    private Boolean soloAudio, audioFormatMp3;
    private MediaFile mediaFile;
    private ProcessBuilder processBuilder;
    private Process process;
    private BufferedReader reader;
    private int exitCode;

    private MediaRepository mediaRepository;

    public MediaThread(ThreadGroup threadGroup, MediaFile mediaFile, MediaRepository mediaRepository, Boolean soloAudio,
            Boolean audioFormatMp3) {
        super(threadGroup, mediaFile.getUrl());

        this.mediaFile = mediaFile;
        this.mediaRepository = mediaRepository;
        this.soloAudio = soloAudio;
        this.audioFormatMp3 = audioFormatMp3;
    }

    @Override
    public void run() {
        processBuilder = new ProcessBuilder("yt-dlp", mediaFile.getUrl());

        if (soloAudio == true) {
            processBuilder = new ProcessBuilder("yt-dlp", "-x", mediaFile.getUrl());
            if (audioFormatMp3)
                processBuilder = new ProcessBuilder("yt-dlp", "-x", "--audio-format", "mp3", mediaFile.getUrl());
        }

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

                                status = line.substring(11, 14).strip() + "%";

                                if (status.isBlank() | status.isEmpty())
                                    status = "1%";

                                if (status.equals("100%"))
                                    status = "Recoding";


                                    System.out.println("ESTATUS -> "+status);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            exitCode = process.waitFor();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mediaFile.setDownloaded(true);
        mediaFile.setExitCode("ok");
        status = "FINISH";

        if (exitCode == 1) {
            mediaFile.setDownloaded(false);
            mediaFile.setExitCode("error en la descarga");
            status = "ERROR";
        }
        System.out.println("El proceso terminó con el código de salida: " + exitCode);
        mediaRepository.save(mediaFile);

    }

    public Double getSegundos() {
        return segundos;
    }

    public MediaFile getMediaFile() {
        return mediaFile;
    }

    public String getStatus() {
        return status;
    }

    public void setMediaFile(MediaFile mediaFile) {
        this.mediaFile = mediaFile;
    }

}
