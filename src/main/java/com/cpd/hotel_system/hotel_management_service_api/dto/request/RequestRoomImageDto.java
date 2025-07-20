package com.cpd.hotel_system.hotel_management_service_api.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestRoomImageDto {
    private MultipartFile file;
    private String roomId;
}
