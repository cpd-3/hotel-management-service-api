package com.cpd.hotel_system.hotel_management_service_api.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseRoomImageDto {
    private Long id;
    private String directory;
    private String fileName;
    private String hash;
    private String resourceUrl;
    private String roomId;
}
