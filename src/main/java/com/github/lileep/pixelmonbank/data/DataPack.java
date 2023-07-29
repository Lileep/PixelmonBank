package com.github.lileep.pixelmonbank.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DataPack {

    private Map<String, String> dataMap = new HashMap<>();
    private static final Type TYPE = new TypeToken<Map<String, String>>() {
    }.getType();
    private static final Gson GSON = new Gson();

    public DataPack() {

    }

    public void putData(String key, String value) {
        dataMap.put(key, value);
    }

    @Nullable
    public String getData(String key) {
        return dataMap.getOrDefault(key, null);
    }

    public InputStream toStream() {
        String data = GSON.toJson(dataMap, TYPE);
        return IOUtils.toInputStream(data, StandardCharsets.UTF_8);
    }

    public static DataPack toDataPack(InputStream stream) {
        DataPack dataPack = new DataPack();
        try {
            String data = IOUtils.toString(stream, StandardCharsets.UTF_8);
            dataPack.dataMap = GSON.fromJson(data, TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataPack;
    }

    public Map<String, String> getDataMap() {
        return dataMap;
    }
}
