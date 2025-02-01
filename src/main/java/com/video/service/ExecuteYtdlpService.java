package com.video.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.video.model.entity.MediaFile;
import com.video.util.CheckFolderFiles;
import com.video.util.YtdlpUpdateInfo;


@Service
public class ExecuteYtdlpService {
    private CheckFolderFiles checkFolderFiles = new CheckFolderFiles();
    private final String YT_DLP_BIN = checkFolderFiles.YT_DLP_BIN;
    private ProcessBuilder processBuilder;
    private Process process;
    private YtdlpUpdateInfo ytstatus = new YtdlpUpdateInfo();
    private final int EXIT_CODE_ERROR = 1;

    public Process getDownloadProces(String formatId, MediaFile mediaFile) {

        String titlePath = "";
        int downloadedSong = 0;

        titlePath = mediaFile.getUpdateInfo().getPlaylist();
        int totalSongInt = mediaFile.getUpdateInfo().getPlaylist_count();

        if (totalSongInt != 0) {
            mediaFile.setTotalSongs(totalSongInt);
            mediaFile.setDownloadedSong(downloadedSong);
        }

        List<String> totalParams = new ArrayList<>();
        totalParams.add(YT_DLP_BIN);
        totalParams.add("-o");

        if (titlePath != null) {
            totalParams.add("./DownloadedFiles/" + titlePath + "/%(title)s.%(ext)s");
        } else {
            totalParams.add("./DownloadedFiles/%(title)s.%(ext)s");
        }

        if (!formatId.equals("direct")) {

            totalParams.add("-f");
            totalParams.add(formatId);
        }

        totalParams.add(mediaFile.getUrl());

        executeProcess(totalParams);

        try {
            process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return process;
    }

    public void executeProcess(List<String> parameters) {
        processBuilder = new ProcessBuilder(parameters);
    }

    public String getVideoMetadata(String url) {

        if (url.contains("list")) {
            executeProcess(Arrays.asList(YT_DLP_BIN, "--flat-playlist", "-j", url));
        } else {
            executeProcess(Arrays.asList(YT_DLP_BIN, "-j", url));
        }

        int exitCode = 100;
        String jsonResult = "";
        String line = "";
        try {

            process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader readerError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            while ((line = reader.readLine()) != null) {
                jsonResult = line;

            }
            while ((line = readerError.readLine()) != null) {

            }

            exitCode = process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            System.out.println("EXIT CODE -> " + exitCode);
        }

        if (exitCode == EXIT_CODE_ERROR) {
            jsonResult = null;

        }

        return jsonResult;

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
