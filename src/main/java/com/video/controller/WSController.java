package com.video.controller;

import java.util.ArrayList;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.video.model.entity.MediaThread;

@Controller
public class WSController {
    private ArrayList<MediaThread> mediaThreadList;

    public WSController(ArrayList<MediaThread> mediaThreadList) {
        this.mediaThreadList = mediaThreadList;
    }

    @MessageMapping("/getInfo")
    @SendTo("/update/getInfo")
    public ArrayList<MediaThread> getList() {
        return mediaThreadList;
    }

}
