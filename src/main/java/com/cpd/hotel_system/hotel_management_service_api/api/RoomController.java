package com.cpd.hotel_system.hotel_management_service_api.api;

import com.cpd.hotel_system.hotel_management_service_api.dto.request.RequestRoomDto;
import com.cpd.hotel_system.hotel_management_service_api.service.RoomService;
import com.cpd.hotel_system.hotel_management_service_api.util.StandardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hotel-management/api/v1/rooms")
public class RoomController {
    private final RoomService roomService;

    @PostMapping("/user/create")
    public ResponseEntity<StandardResponseDto> create(
            @RequestBody RequestRoomDto dto) {
        roomService.create(dto);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        201, "Room Saved!", null
                ),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<StandardResponseDto> update(
            @PathVariable("id") String roomId,
            @RequestBody RequestRoomDto dto) throws SQLException {
        roomService.update(dto, roomId);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        201, "Room Updated!", null
                ),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/host/delete/{id}")
    public ResponseEntity<StandardResponseDto> delete(
            @PathVariable("id") String roomId) throws SQLException {
        roomService.delete(roomId);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        204, "Room deleted!", null
                ),
                HttpStatus.NO_CONTENT
        );
    }

    @GetMapping("/visitor/find-by-id/{id}")
    public ResponseEntity<StandardResponseDto> findById(
            @PathVariable("id") String roomId) throws SQLException {
        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Room found!", roomService.findById(roomId)
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/visitor/find-all")
    public ResponseEntity<StandardResponseDto> findAll(
            @RequestParam int page,
            @RequestParam int size
    ) throws SQLException {
        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Room list!", roomService.findAll(page, size)
                ),
                HttpStatus.OK
        );
    }
}