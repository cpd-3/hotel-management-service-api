package com.cpd.hotel_system.hotel_management_service_api.api;

import com.cpd.hotel_system.hotel_management_service_api.dto.request.RequestAddressDto;
import com.cpd.hotel_system.hotel_management_service_api.service.AddressService;
import com.cpd.hotel_system.hotel_management_service_api.util.StandardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hotel-management/api/v1/addresses")
public class AddressController {
    private final AddressService addressService;

    @PostMapping("/user/create")
    public ResponseEntity<StandardResponseDto> create(
            @RequestBody RequestAddressDto dto) {
        addressService.create(dto);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        201, "Address Saved!", null
                ),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<StandardResponseDto> update(
            @PathVariable("id") String addressId,
            @RequestBody RequestAddressDto dto) throws SQLException {
        addressService.update(dto, addressId);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        201, "Address Updated!", null
                ),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/host/delete/{id}")
    public ResponseEntity<StandardResponseDto> delete(
            @PathVariable("id") String addressId) throws SQLException {
        addressService.delete(addressId);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        204, "Address deleted!", null
                ),
                HttpStatus.NO_CONTENT
        );
    }

    @GetMapping("/visitor/find-by-id/{id}")
    public ResponseEntity<StandardResponseDto> findById(
            @PathVariable("id") String addressId) throws SQLException {
        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Address found!", addressService.findById(addressId)
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/visitor/find-by-branch/{branchId}")
    public ResponseEntity<StandardResponseDto> findByBranchId(
            @PathVariable("branchId") String branchId) throws SQLException {
        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Address found by branch!", addressService.findByBranchId(branchId)
                ),
                HttpStatus.OK
        );
    }
}