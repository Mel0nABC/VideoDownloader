package com.video;

import java.io.IOException;

public class ProcesYtdlp {

    private final String YT_DLP_BIN = System.getProperty("user.dir")
            + "/src/main/resources/yt-dlt_binarie/yt-dlp";

    // dockerfile
    // private final String YT_DLP_BIN = System.getProperty("user.dir") +
    // "/yt-dlp";
    private ProcessBuilder processBuilder;
    private Process process;

    public Process getDownloadProces(Boolean soloAudio, Boolean audioFormatMp3, MediaFile mediaFile) {

        executeProcess(new String[] { YT_DLP_BIN, "-o", "./DownloadedFiles//%(title)s.%(ext)s", mediaFile.getUrl() });

        if (soloAudio == true) {

            executeProcess(new String[] { YT_DLP_BIN, "-o", "./DownloadedFiles//%(title)s.%(ext)s", "-x",
                    mediaFile.getUrl() });

            if (audioFormatMp3)

                executeProcess(new String[] { YT_DLP_BIN, "-o", "./DownloadedFiles//%(title)s.%(ext)s", "-x",
                        "--audio-format", "mp3", mediaFile.getUrl() });
        }

        try {
            process = processBuilder.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return process;
    }

    public void executeProcess(String[] parameters) {
        processBuilder = new ProcessBuilder(parameters);

    }

    public String getRelease() {

        return "";
    }

}
