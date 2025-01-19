package com.video;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class CheckFolderFiles {

    public static final String YT_DLP_BIN = System.getProperty("user.dir") + "/yt-dlp";

    public static void CheckOrCreateDownloadDir() {
        File downDir = new File("./DownloadedFiles");

        if (!downDir.exists())
            downDir.mkdirs();
    }

    public static void checkOrDownloadYtdlp() {

        File ytFile = new File(YT_DLP_BIN);

        if (!ytFile.exists()) {
            String fileUrl = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_linux";
            String outputPath = "yt-dlp";
            try (InputStream inputStream = new URL(fileUrl).openStream();
                    FileOutputStream outputStream = new FileOutputStream(outputPath)) {

                byte[] buffer = new byte[4096];
                int bytesRead;

                // Leer desde la URL y escribir al archivo
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                System.out.println("Archivo descargado en: " + outputPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
