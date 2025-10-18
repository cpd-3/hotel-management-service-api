package com.cpd.hotel_system.hotel_management_service_api.service.impl;

import com.cpd.hotel_system.hotel_management_service_api.dto.request.RequestRoomDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.ResponseFacilityDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.ResponseRoomDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.ResponseRoomImageDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.paginate.RoomPaginateResponseDto;
import com.cpd.hotel_system.hotel_management_service_api.entity.Branch;
import com.cpd.hotel_system.hotel_management_service_api.entity.Facility;
import com.cpd.hotel_system.hotel_management_service_api.entity.Room;
import com.cpd.hotel_system.hotel_management_service_api.entity.RoomImage;
import com.cpd.hotel_system.hotel_management_service_api.repo.BranchRepo;
import com.cpd.hotel_system.hotel_management_service_api.repo.RoomRepo;
import com.cpd.hotel_system.hotel_management_service_api.service.RoomService;
import com.cpd.hotel_system.hotel_management_service_api.util.FileDataExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepo roomRepo;
    private final BranchRepo branchRepo;
    private final FileDataExtractor fileDataExtractor;

    @Override
    public void create(RequestRoomDto dto) {
        // Validate branch exists
        Branch branch = branchRepo.findById(dto.getBranchId())
                .orElseThrow(() -> new RuntimeException(
                        String.format("Branch not found with id: %s", dto.getBranchId())
                ));

        // Check if room number already exists in this branch
        if (roomRepo.existsByRoomNumberAndBranch(dto.getRoomNumber(), branch)) {
            throw new RuntimeException(
                    String.format("Room number '%s' already exists in this branch", dto.getRoomNumber())
            );
        }

        // Create new room
        Room room = Room.builder()
                .roomId(UUID.randomUUID().toString())
                .roomNumber(dto.getRoomNumber())
                .type(dto.getRoomType())
                .bedCount(dto.getBedCount())
                .price(dto.getPrice())
                .isAvailable(dto.isAvailable())
                .branch(branch)
                .build();

        roomRepo.save(room);
    }

    @Override
    public void update(RequestRoomDto dto, String roomId) {
        // Find existing room
        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Room not found with id: %s", roomId)
                ));

        // If branch is being changed, validate new branch
        if (dto.getBranchId() != null &&
                !dto.getBranchId().equals(room.getBranch().getBranchId())) {
            Branch newBranch = branchRepo.findById(dto.getBranchId())
                    .orElseThrow(() -> new RuntimeException(
                            String.format("Branch not found with id: %s", dto.getBranchId())
                    ));

            // Check if room number conflicts in the new branch
            if (roomRepo.existsByRoomNumberAndBranch(dto.getRoomNumber(), newBranch)) {
                throw new RuntimeException(
                        String.format("Room number '%s' already exists in the target branch", dto.getRoomNumber())
                );
            }

            room.setBranch(newBranch);
        } else {
            // If branch is not changing, check if new room number conflicts in current branch
            if (!dto.getRoomNumber().equals(room.getRoomNumber()) &&
                    roomRepo.existsByRoomNumberAndBranch(dto.getRoomNumber(), room.getBranch())) {
                throw new RuntimeException(
                        String.format("Room number '%s' already exists in this branch", dto.getRoomNumber())
                );
            }
        }

        // Update room fields
        room.setRoomNumber(dto.getRoomNumber());
        room.setType(dto.getRoomType());
        room.setBedCount(dto.getBedCount());
        room.setPrice(dto.getPrice());
        room.setAvailable(dto.isAvailable());

        roomRepo.save(room);
    }

    @Override
    public void delete(String roomId) {
        // Find room to check if it exists
        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Room not found with id: %s", roomId)
                ));

        // Check if room has associated facilities
        if (room.getFacilities() != null && !room.getFacilities().isEmpty()) {
            throw new RuntimeException(
                    String.format("Cannot delete room with id: %s. It has associated facilities.", roomId)
            );
        }

        // Check if room has associated images
        if (room.getRoomImages() != null && !room.getRoomImages().isEmpty()) {
            throw new RuntimeException(
                    String.format("Cannot delete room with id: %s. It has associated images.", roomId)
            );
        }

        roomRepo.deleteById(roomId);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseRoomDto findById(String roomId) {
        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Room not found with id: %s", roomId)
                ));

        return mapToResponseDto(room);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomPaginateResponseDto findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Room> roomPage = roomRepo.findAll(pageable);

        return buildPaginateResponse(roomPage);
    }

    /**
     * Helper method to map Room entity to ResponseRoomDto
     */
    private ResponseRoomDto mapToResponseDto(Room room) {
        // Map facilities
        List<ResponseFacilityDto> facilities = Collections.emptyList();
        if (room.getFacilities() != null && !room.getFacilities().isEmpty()) {
            facilities = room.getFacilities().stream()
                    .map(this::mapFacilityToDto)
                    .collect(Collectors.toList());
        }

        // Map images
        List<ResponseRoomImageDto> images = Collections.emptyList();
        if (room.getRoomImages() != null && !room.getRoomImages().isEmpty()) {
            images = room.getRoomImages().stream()
                    .map(this::mapImageToDto)
                    .collect(Collectors.toList());
        }

        return ResponseRoomDto.builder()
                .roomId(room.getRoomId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getType())
                .bedCount(room.getBedCount())
                .price(room.getPrice())
                .isAvailable(room.isAvailable())
                .branchId(room.getBranch().getBranchId())
                .facilities(facilities)
                .images(images)
                .build();
    }

    /**
     * Helper method to map Facility entity to ResponseFacilityDto
     */
    private ResponseFacilityDto mapFacilityToDto(Facility facility) {
        return ResponseFacilityDto.builder()
                .id(facility.getId())
                .name(facility.getName())
                .roomId(facility.getRoom().getRoomId())
                .build();
    }

    /**
     * Helper method to map RoomImage entity to ResponseRoomImageDto
     */
    private ResponseRoomImageDto mapImageToDto(RoomImage image) {
        return ResponseRoomImageDto.builder()
                .id(image.getId())
                .directory(fileDataExtractor.byteArrayToString(image.getFileFormatter().getDirectory()))
                .fileName(fileDataExtractor.byteArrayToString(image.getFileFormatter().getFileName()))
                .hash(fileDataExtractor.byteArrayToString(image.getFileFormatter().getHash()))
                .resourceUrl(fileDataExtractor.byteArrayToString(image.getFileFormatter().getResourceUrl()))
                .roomId(image.getRoom().getRoomId())
                .build();
    }

    /**
     * Helper method to build paginated response
     */
    private RoomPaginateResponseDto buildPaginateResponse(Page<Room> roomPage) {
        List<ResponseRoomDto> dataList = roomPage.getContent().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return RoomPaginateResponseDto.builder()
                .dataList(dataList)
                .dataCount(roomPage.getTotalElements())
                .build();
    }
}