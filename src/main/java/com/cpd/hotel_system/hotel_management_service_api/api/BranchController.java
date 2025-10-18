package com.cpd.hotel_system.hotel_management_service_api.api;

import com.cpd.hotel_system.hotel_management_service_api.dto.request.RequestBranchDto;
import com.cpd.hotel_system.hotel_management_service_api.service.BranchService;
import com.cpd.hotel_system.hotel_management_service_api.util.StandardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hotel-management/api/v1/branches")
public class BranchController {
    private final BranchService branchService;

    @PostMapping("/user/create")
    public ResponseEntity<StandardResponseDto> create(
            @RequestBody RequestBranchDto dto) {
        branchService.create(dto);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        201, "Branch Saved!", null
                ),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<StandardResponseDto> update(
            @PathVariable("id") String branchId,
            @RequestBody RequestBranchDto dto) throws SQLException {
        branchService.update(dto, branchId);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        201, "Branch Updated!", null
                ),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/host/delete/{id}")
    public ResponseEntity<StandardResponseDto> delete(
            @PathVariable("id") String branchId) throws SQLException {
        branchService.delete(branchId);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        204, "Branch deleted!", null
                ),
                HttpStatus.NO_CONTENT
        );
    }

    @GetMapping("/visitor/find-by-id/{id}")
    public ResponseEntity<StandardResponseDto> findById(
            @PathVariable("id") String branchId) throws SQLException {
        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Branch found!", branchService.findById(branchId)
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
                        200, "Branch list!", branchService.findAll(page, size, searchText)
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/visitor/find-all-by-hotel/{hotelId}")
    public ResponseEntity<StandardResponseDto> findAllByHotelId(
            @PathVariable("hotelId") String hotelId,
            @RequestParam String searchText,
            @RequestParam int page,
            @RequestParam int size
    ) throws SQLException {
        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Branch list by hotel!", branchService.findAllByHotelId(page, size, hotelId, searchText)
                ),
                HttpStatus.OK
        );
    }
}