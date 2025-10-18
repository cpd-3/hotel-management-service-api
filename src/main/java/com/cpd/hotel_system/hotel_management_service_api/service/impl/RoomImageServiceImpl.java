package com.cpd.hotel_system.hotel_management_service_api.service.impl;

import com.amazonaws.services.accessanalyzer.model.InternalServerException;
import com.cpd.hotel_system.hotel_management_service_api.dto.request.RequestRoomImageDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.ResponseRoomImageDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.paginate.RoomImagePaginateResponseDto;
import com.cpd.hotel_system.hotel_management_service_api.entity.FileFormatter;
import com.cpd.hotel_system.hotel_management_service_api.entity.Room;
import com.cpd.hotel_system.hotel_management_service_api.entity.RoomImage;
import com.cpd.hotel_system.hotel_management_service_api.exceptions.EntryNotFoundException;
import com.cpd.hotel_system.hotel_management_service_api.repo.RoomImageRepo;
import com.cpd.hotel_system.hotel_management_service_api.repo.RoomRepo;
import com.cpd.hotel_system.hotel_management_service_api.service.FileService;
import com.cpd.hotel_system.hotel_management_service_api.service.RoomImageService;
import com.cpd.hotel_system.hotel_management_service_api.util.CommonFileSavedBinaryDataDTO;
import com.cpd.hotel_system.hotel_management_service_api.util.FileDataExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomImageServiceImpl implements RoomImageService {

    private final RoomImageRepo roomImageRepo;
    private final RoomRepo roomRepo;
    private final FileService fileService;
    private final FileDataExtractor fileDataExtractor;

    @Value("${bucketName}")
    private String bucketName;

    @Override
    public void create(RequestRoomImageDto dto) {
        CommonFileSavedBinaryDataDTO resource = null;

        // Validate room exists
        Optional<Room> selectedRoom = roomRepo.findById(dto.getRoomId());
        if (selectedRoom.isEmpty()) {
            throw new EntryNotFoundException("Room not found.");
        }

        try {
            // Create resource in S3 or file system
            resource = fileService.createResource(
                    dto.getFile(),
                    "room/" + selectedRoom.get().getRoomId() + "/images/",
                    bucketName
            );

            // Build and save room image entity
            RoomImage roomImage = RoomImage.builder()
                    .fileFormatter(
                            new FileFormatter(fileDataExtractor.blobToByteArray(resource.getFileName()),
                                    fileDataExtractor.blobToByteArray(resource.getResourceUrl()),
                                    resource.getDirectory().getBytes(),
                                    fileDataExtractor.blobToByteArray(resource.getHash()))
                    )
                    .room(selectedRoom.get())
                    .build();

            roomImageRepo.save(roomImage);

        } catch (Exception e) {
            // Rollback: delete uploaded resource if database save fails
            if (resource != null) {
                try {
                    fileService.deleteResource(
                            bucketName,
                            resource.getDirectory(),
                            fileDataExtractor.extractActualFileName(
                                    new InputStreamReader(resource.getFileName().getBinaryStream())
                            )
                    );
                } catch (Exception ex) {
                    // Log the error but don't throw
                    System.err.println("Failed to delete resource during rollback: " + ex.getMessage());
                }
            }
            throw new InternalServerException("Failed to create room image: " + e.getMessage());
        }
    }

    @Override
    public void update(RequestRoomImageDto dto, String imageId) {
        CommonFileSavedBinaryDataDTO resource = null;

        // Parse imageId to Long
        long id;
        try {
            id = Long.parseLong(imageId);
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    String.format("Invalid image id format: %s", imageId)
            );
        }

        // Find existing room image
        Optional<RoomImage> selectedImage = roomImageRepo.findById(id);
        if (selectedImage.isEmpty()) {
            throw new EntryNotFoundException("Room image not found.");
        }

        // Validate room exists if changing room
        Room room;
        if (dto.getRoomId() != null &&
                !dto.getRoomId().equals(selectedImage.get().getRoom().getRoomId())) {
            Optional<Room> newRoom = roomRepo.findById(dto.getRoomId());
            if (newRoom.isEmpty()) {
                throw new EntryNotFoundException("Room not found.");
            }
            room = newRoom.get();
        } else {
            room = selectedImage.get().getRoom();
        }

        try {
            // Delete old resource
            try {
                fileService.deleteResource(
                        bucketName,
                        fileDataExtractor.byteArrayToString(selectedImage.get().getFileFormatter().getDirectory()),
                        fileDataExtractor.byteArrayToString(selectedImage.get().getFileFormatter().getFileName())
                );
            } catch (Exception e) {
                throw new InternalServerException("Failed to delete existing image resource");
            }

            // Create new resource
            resource = fileService.createResource(
                    dto.getFile(),
                    "room/" + room.getRoomId() + "/images/",
                    bucketName
            );

            // Update room image entity
            selectedImage.get().getFileFormatter().setDirectory(resource.getDirectory().getBytes());
            selectedImage.get().getFileFormatter().setFileName(fileDataExtractor.blobToByteArray(resource.getFileName()));
            selectedImage.get().getFileFormatter().setHash(fileDataExtractor.blobToByteArray(resource.getHash()));
            selectedImage.get().getFileFormatter().setResourceUrl(fileDataExtractor.blobToByteArray(resource.getResourceUrl()));
            selectedImage.get().setRoom(room);

            roomImageRepo.save(selectedImage.get());

        } catch (Exception e) {
            // Rollback: delete new resource and restore old one if possible
            if (resource != null) {
                try {
                    fileService.deleteResource(
                            bucketName,
                            resource.getDirectory(),
                            fileDataExtractor.extractActualFileName(
                                    new InputStreamReader(resource.getFileName().getBinaryStream())
                            )
                    );
                } catch (Exception ex) {
                    System.err.println("Failed to delete resource during rollback: " + ex.getMessage());
                }
            }
            throw new InternalServerException("Failed to update room image: " + e.getMessage());
        }
    }

    @Override
    public void delete(String imageId) {
        // Parse imageId to Long
        long id;
        try {
            id = Long.parseLong(imageId);
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    String.format("Invalid image id format: %s", imageId)
            );
        }

        // Find existing room image
        Optional<RoomImage> selectedImage = roomImageRepo.findById(id);
        if (selectedImage.isEmpty()) {
            throw new EntryNotFoundException("Room image not found.");
        }

        try {
            // Delete resource from storage
            fileService.deleteResource(
                    bucketName,
                    fileDataExtractor.byteArrayToString(selectedImage.get().getFileFormatter().getDirectory()),
                    fileDataExtractor.byteArrayToString(selectedImage.get().getFileFormatter().getFileName())
            );

            // Delete from database
            roomImageRepo.deleteById(id);

        } catch (Exception e) {
            throw new InternalServerException("Failed to delete room image: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseRoomImageDto findById(String imageId) {
        // Parse imageId to Long
        long id;
        try {
            id = Long.parseLong(imageId);
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    String.format("Invalid image id format: %s", imageId)
            );
        }

        RoomImage roomImage = roomImageRepo.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Room image not found."));

        return mapToResponseDto(roomImage);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomImagePaginateResponseDto findAll(int page, int size, String roomId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoomImage> imagePage;

        if (roomId == null || roomId.trim().isEmpty()) {
            // Get all room images
            imagePage = roomImageRepo.findAll(pageable);
        } else {
            // Validate room exists
            Room room = roomRepo.findById(roomId)
                    .orElseThrow(() -> new EntryNotFoundException("Room not found."));

            // Get images for specific room
            imagePage = roomImageRepo.findAllByRoom(room, pageable);
        }

        return buildPaginateResponse(imagePage);
    }

    /**
     * Helper method to map RoomImage entity to ResponseRoomImageDto
     */
    private ResponseRoomImageDto mapToResponseDto(RoomImage roomImage) {
        return ResponseRoomImageDto.builder()
                .id(roomImage.getId())
                .directory(fileDataExtractor.byteArrayToString(roomImage.getFileFormatter().getDirectory()))
                .fileName(fileDataExtractor.byteArrayToString(roomImage.getFileFormatter().getFileName()))
                .hash(fileDataExtractor.byteArrayToString(roomImage.getFileFormatter().getHash()))
                .resourceUrl(fileDataExtractor.byteArrayToString(roomImage.getFileFormatter().getResourceUrl()))
                .roomId(roomImage.getRoom().getRoomId())
                .build();
    }

    /**
     * Helper method to build paginated response
     */
    private RoomImagePaginateResponseDto buildPaginateResponse(Page<RoomImage> imagePage) {
        List<ResponseRoomImageDto> dataList = imagePage.getContent().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return RoomImagePaginateResponseDto.builder()
                .dataList(dataList)
                .dataCount(imagePage.getTotalElements())
                .build();
    }
}