package com.pruebas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DownloadManager {

    private ProcessBuilder processBuilder;
    private Process process;
    private BufferedReader reader;
    private boolean finish = false;
    private long porcentaje;

    public boolean download(String url) {

        processBuilder = new ProcessBuilder("yt-dlp", "-x", "--audio-format", "mp3", url);
        try {
            process = processBuilder.start();

            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {

                try {
                    // System.out.println(line);
                    porcentaje = Long.parseLong(line.substring(12, 16));

                } catch (NumberFormatException e) {

                } catch (StringIndexOutOfBoundsException e) {

                }

            }

            int exitCode = process.waitFor();
            finish = true;
            System.out.println("El proceso terminó con el código de salida: " + exitCode);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return true;
    }

    public void start() {
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public BufferedReader getReader() {
        return reader;
    }

    public long getPorcentaje() {
        return porcentaje;
    }

}
