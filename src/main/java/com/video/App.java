package com.video;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.video.service.UpdateInfoService;
import com.video.util.CheckFolderFiles;

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
        CheckFolderFiles checkFolderFiles = new CheckFolderFiles();
        checkFolderFiles.CheckOrCreateDownloadDir();
        checkFolderFiles.checkOrDownloadYtdlp();
        SpringApplication.run(App.class, args);
    }
}