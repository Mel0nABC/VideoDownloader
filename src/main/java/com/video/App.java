package com.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.video.model.service.CheckFolderFiles;

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
        CheckFolderFiles.CheckOrCreateDownloadDir();
        CheckFolderFiles.checkOrDownloadYtdlp();
        SpringApplication.run(App.class, args);

        // ExecuteYtdlp executeYtdlp = new ExecuteYtdlp();
        // executeYtdlp.getVideoFromats("https://www.youtube.com/watch?v=V8KruVKM7vw");
    }
}