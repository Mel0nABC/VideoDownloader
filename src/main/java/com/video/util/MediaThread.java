package com.video.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.video.model.entity.MediaFile;
import com.video.model.repository.MediaRepository;
import com.video.service.ExecuteYtdlpService;

public class MediaThread extends Thread {

    private MediaRepository mediaRepository;
    private MediaFile mediaFile;
    private BufferedReader reader;
    private int exitCode;
    private boolean downloadInProgress;
    private String formatId;
    private final int EXIT_CODE_OK = 0;
    private final int EXIT_CODE_ERROR = 1;
    private final int EXIT_CODE_CANCEL = 2;

    public MediaThread(ThreadGroup threadGroup, MediaFile mediaFile, String formatId,
            Boolean downloadInProgress, MediaRepository mediaRepository) {
        super(threadGroup, "threadgroup");
        this.mediaFile = mediaFile;
        this.mediaRepository = mediaRepository;
        this.formatId = formatId;
        this.downloadInProgress = downloadInProgress;
        this.mediaRepository = mediaRepository;
    }

    @Override
    public void run() {
        mediaFile.setStatusDownload("Iniciando descarga ...");
        try {
            ExecuteYtdlpService procesYtdlp = new ExecuteYtdlpService();
            Process process = procesYtdlp.getDownloadProces(formatId, mediaFile);

            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = "";
            try {
                boolean finish = false;
                downloadInProgress = true;

                while ((line = reader.readLine()) != null && !finish) {

                    System.out.println(line);

                    String regex = "\\d+\\.\\d+%";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(line);

                    while (matcher.find()) {
                        mediaFile.setProgressDownload(matcher.group(0));
                    }
                    mediaFile.setStatusDownload(line);

                    if (line.contains("[download] Downloading item")) {
                        regex = "(\\d+).+?(\\d+)";
                        pattern = Pattern.compile(regex);
                        matcher = pattern.matcher(line);
                        while (matcher.find()) {
                            int actualDownloadedSong = Integer.parseInt(matcher.group(1));
                            mediaFile.setDownloadedSong(actualDownloadedSong);
                        }
                    }

                    if (this.isInterrupted()) {
                        finish = true;
                        process.destroy();
                        exitCode = EXIT_CODE_CANCEL;
                    }
                    mediaRepository.save(mediaFile);
                }

                reader.close();
            } catch (Exception e) {
                System.out.println("Se ha detenido el thread de la url -> " + mediaFile.getUrl());
            }

            exitCode = process.waitFor();

        } catch (InterruptedException e) {
            System.out.println("Se ha detenido el thread de la url -> " + mediaFile.getUrl());
        } finally {

            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            mediaFile.setDownloaded(true);
            mediaFile.setExitCode(EXIT_CODE_OK);
            mediaFile.setStatusDownload("Descarga finalizada");
            downloadInProgress = false;

            if (exitCode == EXIT_CODE_CANCEL) {
                mediaFile.setDownloaded(false);
                mediaFile.setExitCode(EXIT_CODE_CANCEL);
                mediaFile.setStatusDownload("Se ha cancelado la descarga: " + exitCode);
            }

            if (exitCode != EXIT_CODE_CANCEL && exitCode != EXIT_CODE_OK) {
                mediaFile.setDownloaded(false);
                mediaFile.setExitCode(EXIT_CODE_ERROR);
                mediaFile.setStatusDownload("Ha ocurrido algún error: " + exitCode);
            }

            mediaRepository.save(mediaFile);

            System.out.println("El proceso terminó con el código de salida: " + exitCode);

        }
    }

    public MediaFile getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(MediaFile mediaFile) {
        this.mediaFile = mediaFile;
    }

    public String getFormatId() {
        return formatId;
    }

    public void setFormatId(String formatId) {
        this.formatId = formatId;
    }

    public boolean isDownloadInProgress() {
        return downloadInProgress;
    }

    public void setDownloadInProgress(boolean downloadInProgress) {
        this.downloadInProgress = downloadInProgress;
    }

}
