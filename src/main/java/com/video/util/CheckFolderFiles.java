package com.video.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class CheckFolderFiles {

    public final String YT_DLP_BIN = System.getProperty("user.dir") + "/yt-dlp";
    public final String PATH_TO_DOWNLOAD = "./DownloadedFiles".replaceAll("./", "");

    public void CheckOrCreateDownloadDir() {
        File downDir = new File(PATH_TO_DOWNLOAD);

        if (!downDir.exists())
            downDir.mkdirs();
    }

    public void checkOrDownloadYtdlp() {

        File ytFile = new File(YT_DLP_BIN);

        if (!ytFile.exists()) {
            String fileUrl = getOsLink();
            String outputPath = "yt-dlp";
            try (InputStream inputStream = new URL(fileUrl).openStream();
                    FileOutputStream outputStream = new FileOutputStream(outputPath)) {

                byte[] buffer = new byte[4096];
                int bytesRead;

                System.out.println("Downloading YT-DLP .....");
                // Leer desde la URL y escribir al archivo
                while ((bytesRead = inputStream.read(buffer)) != -1) {

                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public String getOsLink() {

        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("windows"))
            osName = "windows";

        if (osName.contains("mac"))
            osName = "mac";

        String fileUrl = "";
        switch (osName) {
            case "linux":
                fileUrl = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_linux";
                break;

            case "windows":
                fileUrl = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_x86.exe";
                break;

            case "mac":
                fileUrl = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_macos";
                break;

            default:
                break;
        }
        return fileUrl;
    }

}
