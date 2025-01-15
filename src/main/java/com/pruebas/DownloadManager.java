package com.pruebas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DownloadManager {

    public void download(String url) {
        ProcessBuilder processBuilder = new ProcessBuilder("yt-dlp", "-x", "--audio-format", "mp3", url);
        Process process;
        try {
            process = processBuilder.start();

            // Leer la salida del comando ejecutado
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Imprimir la salida del proceso línea por línea
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Esperar a que el proceso termine
            int exitCode = process.waitFor();
            System.out.println("El proceso terminó con el código de salida: " + exitCode);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
