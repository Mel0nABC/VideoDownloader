package com.video;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExecuteYtdlp {

    // dockerfile
    private final String YT_DLP_BIN = CheckFolderFiles.YT_DLP_BIN;
    private ProcessBuilder processBuilder;
    private Process process;
    private YtdlpUpdateInfo ytstatus = new YtdlpUpdateInfo();

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

    public YtdlpUpdateInfo getRelease() {
        executeProcess(new String[] { YT_DLP_BIN, "-U" });
        try {
            process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader readerError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            readProcessResult(reader);
            readProcessResult(readerError);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ytstatus;
    }

    public void readProcessResult(BufferedReader reader) {
        String line;
        try {
            while ((line = reader.readLine()) != null) {

                if (line.contains("ERROR")) {
                    ytstatus.setError(true);
                }

                if (!ytstatus.isError()) {
                    if (line.contains("Latest version:")) {
                        ytstatus.setLatestVersion(getDates(line));
                    }

                    if (line.contains("yt-dlp is up to date") || line.contains("Current version:")) {
                        ytstatus.setActualVersion(getDates(line));
                    }

                    if (line.contains("Updated"))
                        ytstatus.setUpdated(true);

                }

            }
            if (!ytstatus.isError())
                if (ytstatus.getActualVersion().equals(ytstatus.getLatestVersion()))
                    ytstatus.setUpToDate(true);

        } catch (IOException e) {
            System.out.println("ERROR EN ExecuteYtdlp.readProcessResult()");
        }
    }

    public String getDates(String line) {
        Pattern pattern = Pattern.compile("(\\w+@\\d{4}\\.\\d{2}\\.\\d{2})");
        Matcher matcher = pattern.matcher(line);
        while (matcher.find())
            return matcher.group();
        return "";
    }

}