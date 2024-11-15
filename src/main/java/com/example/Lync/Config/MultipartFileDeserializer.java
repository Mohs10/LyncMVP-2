package com.example.Lync.Config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MultipartFileDeserializer extends JsonDeserializer<MultipartFile> {
    @Override
    public MultipartFile deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Logic to convert JSON into a MultipartFile (not typical for file uploads)
        return null; // Placeholder for custom deserialization logic
    }
}

