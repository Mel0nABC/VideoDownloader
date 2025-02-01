package com.video.service;

import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.video.model.entity.TableInfo;
import com.video.model.entity.UpdateInfo;


@Service
public class UpdateInfoService {

    private String playlist, playlist_channel, fulltitle, thumbnail;
    private int playlist_count;
    private String format_id, ext, resolution, acodec, format_note;
    private double filesize, tbr, abr, vbr;

    public UpdateInfo getInfoDownload(String url, String jsonString) {

        JsonNode node = getObjectMapper(jsonString);

        if (node == null)
            return null;

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

        return new UpdateInfo(url, playlist, playlist_count, playlist_channel, fulltitle, thumbnail);
    }

    public ArrayList<TableInfo> getTableInfo(String url, String jsonString) {

        JsonNode node = getObjectMapper(jsonString);
        ArrayList<TableInfo> listaTableInfo = new ArrayList<>();

        if (node == null)
            return null;

        Iterator<JsonNode> itera = node.get("formats").iterator();

        while (itera.hasNext()) {
            JsonNode linea = (JsonNode) itera.next();

            if (linea.get("format_id") != null)
                format_id = linea.get("format_id").textValue();

            if (linea.get("ext") != null)
                ext = linea.get("ext").textValue();

            if (linea.get("resolution") != null)
                resolution = linea.get("resolution").textValue();

            if (linea.get("filesize") != null)
                filesize = linea.get("filesize").intValue();

            if (linea.get("tbr") != null)
                tbr = linea.get("tbr").intValue();

            if (linea.get("acodec") != null)
                acodec = linea.get("acodec").textValue();

            if (linea.get("abr") != null)
                abr = linea.get("abr").intValue();

            if (linea.get("format_note") != null)
                format_note = linea.get("format_note").textValue();

            if (linea.get("vbr") != null)
                vbr = linea.get("vbr").intValue();

            TableInfo newTableInfo = new TableInfo(url, format_id, ext, resolution, filesize, tbr, acodec, abr,
                    format_note, vbr);
            listaTableInfo.add(newTableInfo);
        }

        return listaTableInfo;

    }

    public JsonNode getObjectMapper(String jsonString) {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = objectMapper.readTree(jsonString);
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return null;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

        return node;

    }
}
