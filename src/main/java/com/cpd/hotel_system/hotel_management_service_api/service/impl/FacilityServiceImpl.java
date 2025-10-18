package com.cpd.hotel_system.hotel_management_service_api.service.impl;

import com.cpd.hotel_system.hotel_management_service_api.dto.request.RequestFacilityDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.ResponseFacilityDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.paginate.FacilityPaginateResponseDto;
import com.cpd.hotel_system.hotel_management_service_api.entity.Facility;
import com.cpd.hotel_system.hotel_management_service_api.entity.Room;
import com.cpd.hotel_system.hotel_management_service_api.repo.FacilityRepo;
import com.cpd.hotel_system.hotel_management_service_api.repo.RoomRepo;
import com.cpd.hotel_system.hotel_management_service_api.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepo facilityRepo;
    private final RoomRepo roomRepo;

    @Override
    public void create(RequestFacilityDto dto) {
        // Validate room exists
        Room room = roomRepo.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException(
                        String.format("Room not found with id: %s", dto.getRoomId())
                ));

        // Check if facility with same name already exists for this room
        if (facilityRepo.existsByNameAndRoom(dto.getName(), room)) {
            throw new RuntimeException(
                    String.format("Facility '%s' already exists for this room", dto.getName())
            );
        }

        // Create new facility
        Facility facility = Facility.builder()
                .name(dto.getName())
                .room(room)
                .build();

        facilityRepo.save(facility);
    }

    @Override
    public void update(RequestFacilityDto dto, String facilityId) {
        // Parse facilityId to Long
        long id;
        try {
            id = Long.parseLong(facilityId);
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    String.format("Invalid facility id format: %s", facilityId)
            );
        }

        // Find existing facility
        Facility facility = facilityRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Facility not found with id: %s", facilityId)
                ));

        // If room is being changed, validate new room
        if (dto.getRoomId() != null &&
                !dto.getRoomId().equals(facility.getRoom().getRoomId())) {
            Room newRoom = roomRepo.findById(dto.getRoomId())
                    .orElseThrow(() -> new RuntimeException(
                            String.format("Room not found with id: %s", dto.getRoomId())
                    ));

            // Check if facility name conflicts in the new room
            if (facilityRepo.existsByNameAndRoom(dto.getName(), newRoom)) {
                throw new RuntimeException(
                        String.format("Facility '%s' already exists for the target room", dto.getName())
                );
            }

            facility.setRoom(newRoom);
        } else {
            // If room is not changing, check if new name conflicts in current room
            if (!dto.getName().equals(facility.getName()) &&
                    facilityRepo.existsByNameAndRoom(dto.getName(), facility.getRoom())) {
                throw new RuntimeException(
                        String.format("Facility '%s' already exists for this room", dto.getName())
                );
            }
        }

        // Update facility fields
        facility.setName(dto.getName());

        facilityRepo.save(facility);
    }

    @Override
    public void delete(String facilityId) {
        // Parse facilityId to Long
        long id;
        try {
            id = Long.parseLong(facilityId);
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    String.format("Invalid facility id format: %s", facilityId)
            );
        }

        // Check if facility exists
        if (!facilityRepo.existsById(id)) {
            throw new RuntimeException(
                    String.format("Facility not found with id: %s", facilityId)
            );
        }

        facilityRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseFacilityDto findById(String facilityId) {
        // Parse facilityId to Long
        long id;
        try {
            id = Long.parseLong(facilityId);
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    String.format("Invalid facility id format: %s", facilityId)
            );
        }

        Facility facility = facilityRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Facility not found with id: %s", facilityId)
                ));

        return mapToResponseDto(facility);
    }

    @Override
    @Transactional(readOnly = true)
    public FacilityPaginateResponseDto findAll(int page, int size, String roomId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Facility> facilityPage;

        if (roomId == null || roomId.trim().isEmpty()) {
            // Get all facilities
            facilityPage = facilityRepo.findAll(pageable);
        } else {
            // Validate room exists
            Room room = roomRepo.findById(roomId)
                    .orElseThrow(() -> new RuntimeException(
                            String.format("Room not found with id: %s", roomId)
                    ));

            // Get facilities for specific room
            facilityPage = facilityRepo.findAllByRoom(room, pageable);
        }

        return buildPaginateResponse(facilityPage);
    }

    /**
     * Helper method to map Facility entity to ResponseFacilityDto
     */
    private ResponseFacilityDto mapToResponseDto(Facility facility) {
        return ResponseFacilityDto.builder()
                .id(facility.getId())
                .name(facility.getName())
                .roomId(facility.getRoom().getRoomId())
                .build();
    }

    /**
     * Helper method to build paginated response
     */
    private FacilityPaginateResponseDto buildPaginateResponse(Page<Facility> facilityPage) {
        List<ResponseFacilityDto> dataList = facilityPage.getContent().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return FacilityPaginateResponseDto.builder()
                .dataList(dataList)
                .dataCount(facilityPage.getTotalElements())
                .build();
    }
}