package com.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Mel0n
 * 
 * This is only a UI with use yt-dlt to download videos.
 *
 * 
 * https://github.com/yt-dlp/yt-dlp
 * 
 * 
 */
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
