package com.video;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExecuteYtdlp {

    // dockerfile
    private final String YT_DLP_BIN = CheckFolderFiles.YT_DLP_BIN;
    private ProcessBuilder processBuilder;
    private Process process;
    private YtdlpUpdateInfo ytstatus = new YtdlpUpdateInfo();

    public Process getDownloadProces(Boolean soloAudio, Boolean audioFormatMp3, MediaFile mediaFile,
            List<String> aditionalParamList) {

        List<String> totalProces = new ArrayList<>();
        totalProces.add(YT_DLP_BIN);
        totalProces.add("-o");
        totalProces.add("./DownloadedFiles//%(title)s.%(ext)s");

        if (soloAudio == true) {
            totalProces.add("-x");
            if (audioFormatMp3) {
                totalProces.add("--audio-format");
                totalProces.add("mp3");
            }
        }

        if (!aditionalParamList.contains("null"))
            totalProces.addAll(aditionalParamList);

        totalProces.add(mediaFile.getUrl());

        executeProcess(totalProces);

        try {
            process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return process;
    }

    public ArrayList<String> getVideoFromats(String url) {
        executeProcess(Arrays.asList(YT_DLP_BIN, "-F", url));
        ArrayList<String> listRows = new ArrayList<>();
        String line;

        try {
            process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader readerError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.contains("[youtube]") == false && line.contains("------------") == false
                        && line.contains("[info]") == false || line.contains("[generic]") != false) {
                    listRows.add(line);
                }

                if (line.contains("[generic]"))
                    listRows.add("ERROR, no se ha logrado obtener la información.");

            }

        } catch (IOException e) {
            System.out.println("ERROR");
            return null;
        }
        return listRows;
    }

    public void executeProcess(List<String> parameters) {
        processBuilder = new ProcessBuilder(parameters);
    }

    public YtdlpUpdateInfo getRelease() {
        executeProcess(Arrays.asList(YT_DLP_BIN, "-U"));
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
                System.out.println(line);

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

            String actualVersion = ytstatus.getActualVersion();
            String latestVersion = ytstatus.getLatestVersion();

            if (!ytstatus.isError())
                if (actualVersion != null && latestVersion != null)
                    if (actualVersion.equals(latestVersion))
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
