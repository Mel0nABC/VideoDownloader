package com.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.video.model.service.CheckFolderFiles;
import com.video.model.service.ExecuteYtdlp;

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
        // executeYtdlp.getVideoMetadata("https://www.youtube.com/watch?v=AWdU2gxIquo   ");

    }
}