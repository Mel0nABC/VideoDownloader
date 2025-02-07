package com.video.model.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.video.model.entity.UpdateInfo;

public class UpdateInfoService {

    private static String playlist, playlist_channel, fulltitle, thumbnail;
    private static int playlist_count;

    public static UpdateInfo getFilteredJson(String url, String jsonString) {
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(jsonString);

            if (node.get("playlist") != null)
                playlist = node.get("playlist").textValue();

            if (node.get("playlist_count") != null)
                playlist_count = node.get("playlist_count").asInt();

            if (node.get("playlist_channel") != null)
                playlist_channel = node.get("playlist_channel").textValue();

            if (node.get("fulltitle") != null)
                fulltitle = node.get("fulltitle").textValue();

            if (node.get("thumbnail") != null)
                thumbnail = node.get("thumbnail").textValue();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new UpdateInfo(url, playlist, playlist_count, playlist_channel, fulltitle, thumbnail);
    }
}
