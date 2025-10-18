package com.cpd.hotel_system.hotel_management_service_api.api;

import com.cpd.hotel_system.hotel_management_service_api.dto.request.RequestFacilityDto;
import com.cpd.hotel_system.hotel_management_service_api.service.FacilityService;
import com.cpd.hotel_system.hotel_management_service_api.util.StandardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hotel-management/api/v1/facilities")
public class FacilityController {
    private final FacilityService facilityService;

    @PostMapping("/user/create")
    public ResponseEntity<StandardResponseDto> create(
            @RequestBody RequestFacilityDto dto) {
        facilityService.create(dto);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        201, "Facility Saved!", null
                ),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<StandardResponseDto> update(
            @PathVariable("id") String facilityId,
            @RequestBody RequestFacilityDto dto) throws SQLException {
        facilityService.update(dto, facilityId);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        201, "Facility Updated!", null
                ),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/host/delete/{id}")
    public ResponseEntity<StandardResponseDto> delete(
            @PathVariable("id") String facilityId) throws SQLException {
        facilityService.delete(facilityId);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        204, "Facility deleted!", null
                ),
                HttpStatus.NO_CONTENT
        );
    }

    @GetMapping("/visitor/find-by-id/{id}")
    public ResponseEntity<StandardResponseDto> findById(
            @PathVariable("id") String facilityId) throws SQLException {
        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Facility found!", facilityService.findById(facilityId)
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
                        200, "Facility list!", facilityService.findAll(page, size, roomId)
                ),
                HttpStatus.OK
        );
    }
}