package com.cpd.hotel_system.hotel_management_service_api.api;

import com.cpd.hotel_system.hotel_management_service_api.dto.request.RequestRoomImageDto;
import com.cpd.hotel_system.hotel_management_service_api.service.RoomImageService;
import com.cpd.hotel_system.hotel_management_service_api.util.StandardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hotel-management/api/v1/images")
public class RoomImageController {
    private final RoomImageService roomImageService;

    @PostMapping("/user/create")
    public ResponseEntity<StandardResponseDto> create(
            @RequestBody RequestRoomImageDto dto) {
        roomImageService.create(dto);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        201, "Room Image Saved!", null
                ),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<StandardResponseDto> update(
            @PathVariable("id") String imageId,
            @RequestBody RequestRoomImageDto dto) throws SQLException {
        roomImageService.update(dto, imageId);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        201, "Room Image Updated!", null
                ),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/host/delete/{id}")
    public ResponseEntity<StandardResponseDto> delete(
            @PathVariable("id") String imageId) throws SQLException {
        roomImageService.delete(imageId);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        204, "Room Image deleted!", null
                ),
                HttpStatus.NO_CONTENT
        );
    }

    @GetMapping("/visitor/find-by-id/{id}")
    public ResponseEntity<StandardResponseDto> findById(
            @PathVariable("id") String imageId) throws SQLException {
        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Room Image found!", roomImageService.findById(imageId)
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/visitor/find-all")
    public ResponseEntity<StandardResponseDto> findAll(
            @RequestParam String roomId,
            @RequestParam int page,
            @RequestParam int size
    ) throws SQLException {
        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Room Image list!", roomImageService.findAll(page, size, roomId)
                ),
                HttpStatus.OK
        );
    }
}