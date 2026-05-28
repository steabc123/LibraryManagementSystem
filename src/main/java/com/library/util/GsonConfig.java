package com.library.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GsonConfig {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonSerializer<LocalDateTime>) (localDateTime, type, jsonSerializationContext) -> {
                return new com.google.gson.JsonPrimitive(localDateTime.format(FORMATTER));
            })
            .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonDeserializer<LocalDateTime>) (jsonElement, type, jsonDeserializationContext) -> {
                return LocalDateTime.parse(jsonElement.getAsString(), FORMATTER);
            })
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    
    public static Gson getGson() {
        return gson;
    }
}
