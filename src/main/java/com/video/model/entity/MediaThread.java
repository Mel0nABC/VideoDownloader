package com.video.model.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.video.controller.MediaController;
import com.video.model.service.ExecuteYtdlp;
import com.video.model.service.MediaRepository;

public class MediaThread extends Thread {

    private MediaRepository mediaRepository;
    private MediaFile mediaFile;
    private BufferedReader reader;
    private int exitCode;
    private boolean downloadInProgress;
    private String formatId;
    private MediaController mediaController;
    private final int EXIT_CODE_OK = 0;
    private final int EXIT_CODE_ERROR = 1;
    private final int EXIT_CODE_CANCEL = 2;

    public MediaThread(ThreadGroup threadGroup, MediaFile mediaFile, MediaRepository mediaRepository, String formatId,
            MediaController mediaController) {
        super(threadGroup, "threadgroup");
        this.mediaFile = mediaFile;
        this.mediaRepository = mediaRepository;
        this.mediaController = mediaController;
        this.formatId = formatId;
    }

    @Override
    public void run() {
        mediaFile.setStatusDownload("Iniciando descarga ...");
        mediaFile.setProgressDownload("0%");
        try {
            ExecuteYtdlp procesYtdlp = new ExecuteYtdlp();
            Process process = procesYtdlp.getDownloadProces(formatId, mediaFile);

            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            try {
                boolean finish = false;
                while ((line = reader.readLine()) != null && !finish) {
                    downloadInProgress = true;
                    System.out.println(line);
                    String regex = "\\d+\\.\\d+%";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(line);

                    while (matcher.find()) {
                        mediaFile.setProgressDownload(matcher.group(0));
                    }
                    mediaFile.setStatusDownload(line);

                    if (this.isInterrupted()) {
                        finish = true;
                        process.destroy();
                        exitCode = EXIT_CODE_CANCEL;
                    }

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

            if (exitCode == EXIT_CODE_ERROR) {
                mediaFile.setDownloaded(false);
                mediaFile.setExitCode(EXIT_CODE_ERROR);
                mediaFile.setStatusDownload("Ha ocurrido algún error: " + exitCode);
            }

            if (exitCode == EXIT_CODE_CANCEL) {
                mediaFile.setDownloaded(false);
                mediaFile.setExitCode(EXIT_CODE_CANCEL);
                mediaFile.setStatusDownload("Se ha cancelado la descarga: " + exitCode);
            }

            mediaRepository.save(mediaFile);

            System.out.println("El proceso terminó con el código de salida: " + exitCode);

            if (exitCode != EXIT_CODE_CANCEL)
                mediaController.delByUrlFromThreadList(mediaFile.getUrl());

        }
    }

    public boolean isNumber(String num) {
        try {
            Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public boolean isDownloadInProgress() {
        return downloadInProgress;
    }

    public MediaFile getMediaFile() {
        return mediaFile;
    }

    public String getFormatId() {
        return formatId;
    }

    public void setFormatId(String formatId) {
        this.formatId = formatId;
    }

    
}
