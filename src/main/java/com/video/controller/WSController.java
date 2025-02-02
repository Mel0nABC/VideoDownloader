package com.video.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.video.service.MediaThreadService;
import com.video.util.MediaThread;

@Controller
public class WSController {

    private MediaThreadService mediaThreadService;

    public WSController(MediaThreadService mediaThreadService) {
        this.mediaThreadService = mediaThreadService;
    }

    @MessageMapping("/getInfo")
    @SendTo("/update/getInfo")
    public List<MediaThread> getList() {
        return mediaThreadService.getMediaThreadList();
    }

}
