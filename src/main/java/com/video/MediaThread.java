package com.video;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaThread extends Thread {

    private MediaRepository mediaRepository;

    private Double segundos = 0.0;
    private String status;
    private Boolean soloAudio, audioFormatMp3;
    private MediaFile mediaFile;
    private ProcessBuilder processBuilder;
    private Process process;
    private BufferedReader reader;
    private int exitCode;
    private final int EXIT_CODE_OK = 0;
    private final int EXIT_CODE_ERROR = 1;
    private final int EXIT_CODE_CANCEL = 2;

    public MediaThread(ThreadGroup threadGroup, MediaFile mediaFile, MediaRepository mediaRepository, Boolean soloAudio,
            Boolean audioFormatMp3) {
        super(threadGroup, "threadgroup");
        this.mediaFile = mediaFile;
        this.mediaRepository = mediaRepository;
        this.soloAudio = soloAudio;
        this.audioFormatMp3 = audioFormatMp3;
    }

    @Override
    public void run() {
        status = "WAIT";
        processBuilder = new ProcessBuilder("yt-dlp", "-o", "./DownloadedFiles//%(title)s.%(ext)s", mediaFile.getUrl());

        if (soloAudio == true) {
            processBuilder = new ProcessBuilder("yt-dlp", "-o", "./DownloadedFiles//%(title)s.%(ext)s", "-x",
                    mediaFile.getUrl());
            if (audioFormatMp3)
                processBuilder = new ProcessBuilder("yt-dlp", "-o", "./DownloadedFiles//%(title)s.%(ext)s", "-x",
                        "--audio-format", "mp3", mediaFile.getUrl());
        }

        try {
            process = processBuilder.start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    String regex = "\\[(.*?)]";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(line);
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
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (this.isInterrupted()) {
                        process.destroy();
                        exitCode = EXIT_CODE_CANCEL;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            exitCode = process.waitFor();

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mediaFile.setDownloaded(true);
            mediaFile.setExitCode(EXIT_CODE_OK);
            status = "FINISH";

            if (exitCode == EXIT_CODE_ERROR) {
                mediaFile.setDownloaded(false);
                mediaFile.setExitCode(EXIT_CODE_ERROR);
                status = "ERROR";
            }

            if (exitCode == EXIT_CODE_CANCEL) {
                mediaFile.setDownloaded(false);
                mediaFile.setExitCode(EXIT_CODE_CANCEL);
                status = "CANCEL";
            }

            if (exitCode != EXIT_CODE_CANCEL)
                mediaRepository.save(mediaFile);
            System.out.println("El proceso terminó con el código de salida: " + exitCode);
        }
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
