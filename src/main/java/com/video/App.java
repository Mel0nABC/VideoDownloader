package com.video;

import java.io.File;

import javax.print.attribute.standard.Media;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Mel0n
 * 
 *         This is only a UI with use yt-dlt to download videos.
 *
 * 
 *         https://github.com/yt-dlp/yt-dlp
 * 
 * 
 */
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        createDownloadDir();
        SpringApplication.run(App.class, args);
    }

    public static void createDownloadDir() {
        File downDir = new File("./DownloadedFiles");

        if (!downDir.exists())
            downDir.mkdirs();
    }

}
