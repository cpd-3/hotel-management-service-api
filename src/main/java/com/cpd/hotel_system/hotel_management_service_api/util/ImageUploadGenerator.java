package com.cpd.hotel_system.hotel_management_service_api.util;

import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class ImageUploadGenerator {
    public String generateCPDResourceName(String name,String type){
        StringBuilder builder = new StringBuilder();
        builder.append(UUID.randomUUID().toString());
        builder.append("-DS-");
        builder.append(type).append("-");
        builder.append(name);
        return builder.toString();
    }
}
