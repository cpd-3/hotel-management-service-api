package com.cpd.hotel_system.hotel_management_service_api.api;

import com.cpd.hotel_system.hotel_management_service_api.dto.request.RequestHotelDto;
import com.cpd.hotel_system.hotel_management_service_api.service.HotelService;
import com.cpd.hotel_system.hotel_management_service_api.util.StandardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hotel-management/api/v1/hotels")
public class HotelController {
    private final HotelService hotelService;

    @PostMapping("/user/create")
    public ResponseEntity<StandardResponseDto> create(
            @RequestBody RequestHotelDto dto) {
        hotelService.create(dto);
        return new ResponseEntity<>(
                new StandardResponseDto(
                    201, "Hotel Saved!",null
                ),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<StandardResponseDto> update(
            @PathVariable("id") String hotelId,
            @RequestBody RequestHotelDto dto) throws SQLException {
        hotelService.update(dto, hotelId);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        201, "Hotel Updated!",null
                ),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/host/delete/{id}")
    public ResponseEntity<StandardResponseDto> delete(
            @PathVariable("id") String hotelId) throws SQLException {
        hotelService.delete(hotelId);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        204, "Hotel deleted!",null
                ),
                HttpStatus.NO_CONTENT
        );
    }

    @GetMapping("/visitor/find-by-id/{id}")
    public ResponseEntity<StandardResponseDto> findById(
            @PathVariable("id") String hotelId) throws SQLException {
        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Hotel found!",hotelService.findById(hotelId)
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/visitor/find-all")
    public ResponseEntity<StandardResponseDto> findAll(
            @RequestParam String searchText,
            @RequestParam int page,
            @RequestParam int size
    ) throws SQLException {
        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Hotel list!",hotelService.findAll(page,size,searchText)
                ),
                HttpStatus.OK
        );
    }
}
