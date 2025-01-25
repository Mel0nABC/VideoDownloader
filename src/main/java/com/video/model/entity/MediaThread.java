package com.video.model.entity;

import com.video.controller.MediaController;
import com.video.model.service.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaThread extends Thread {

    private MediaRepository mediaRepository;

    private Double segundos = 0.0;
    private String status;
    private Boolean soloAudio, audioFormatMp3;
    private MediaFile mediaFile;
    private BufferedReader reader;
    private int exitCode;
    private List<String> aditionalParamList;
    private boolean downloadInProgress;
    private MediaController mediaController;
    private final int EXIT_CODE_OK = 0;
    private final int EXIT_CODE_ERROR = 1;
    private final int EXIT_CODE_CANCEL = 2;

    public MediaThread(ThreadGroup threadGroup, MediaFile mediaFile, MediaRepository mediaRepository, Boolean soloAudio,
            Boolean audioFormatMp3, List<String> aditionalParamList, MediaController mediaController) {
        super(threadGroup, "threadgroup");
        this.mediaFile = mediaFile;
        this.mediaRepository = mediaRepository;
        this.soloAudio = soloAudio;
        this.audioFormatMp3 = audioFormatMp3;
        this.aditionalParamList = aditionalParamList;
        this.mediaController = mediaController;
    }

    @Override
    public void run() {
        status = "WAIT";
        try {
            ExecuteYtdlp procesYtdlp = new ExecuteYtdlp();
            Process process = procesYtdlp.getDownloadProces(soloAudio, audioFormatMp3, mediaFile, aditionalParamList);

            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            try {
                boolean finish = false;
                while ((line = reader.readLine()) != null && !finish) {
                    String regex = "\\[(.*?)]";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find()) {
                        String statusString = matcher.group(1);
                        if (statusString.equals("download")) {
                            try {
                                System.out.println(line);
                                if (line.contains("Destination")) {
                                    String[] downDesti = line.split("/");
                                    mediaFile.setFileName(downDesti[downDesti.length - 1]);
                                }

                                if (line.length() > 16) {
                                    status = line.substring(11, 14).strip() + "%";
                                    mediaFile.setProgressDownload(status);
                                    mediaRepository.save(mediaFile);
                                    downloadInProgress = true;
                                    if (status.isBlank() | status.isEmpty()) {
                                        status = "1%";
                                    }

                                    if (status.equals("100%"))
                                        status = "Recoding";
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

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
            status = "FINISH";
            downloadInProgress = false;

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

            mediaRepository.save(mediaFile);

            System.out.println("El proceso terminó con el código de salida: " + exitCode);

            if (exitCode != EXIT_CODE_CANCEL)
                mediaController.delByUrlFromThreadList(mediaFile.getUrl());

        }
    }

    public boolean isDownloadInProgress() {
        return downloadInProgress;
    }

    public MediaFile getMediaFile() {
        return mediaFile;
    }

    public String getStatus() {
        return status;
    }
}
