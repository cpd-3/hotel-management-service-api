package com.cpd.hotel_system.hotel_management_service_api.service.impl;

import com.cpd.hotel_system.hotel_management_service_api.dto.request.RequestBranchDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.ResponseBranchDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.paginate.BranchPaginateResponseDto;
import com.cpd.hotel_system.hotel_management_service_api.entity.Branch;
import com.cpd.hotel_system.hotel_management_service_api.entity.Hotel;
import com.cpd.hotel_system.hotel_management_service_api.repo.BranchRepo;
import com.cpd.hotel_system.hotel_management_service_api.repo.HotelRepo;
import com.cpd.hotel_system.hotel_management_service_api.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BranchServiceImpl implements BranchService {

    private final BranchRepo branchRepo;
    private final HotelRepo hotelRepo;

    @Override
    public void create(RequestBranchDto dto) {
        // Validate hotel exists
        Hotel hotel = hotelRepo.findById(dto.getHotelId())
                .orElseThrow(() -> new RuntimeException(
                        String.format("Hotel not found with id: %s", dto.getHotelId())
                ));

        // Check if branch name already exists for this hotel
        if (branchRepo.existsByBranchNameAndHotel(dto.getBranchName(), hotel)) {
            throw new RuntimeException(
                    String.format("Branch with name '%s' already exists for this hotel", dto.getBranchName())
            );
        }

        // Create new branch
        Branch branch = Branch.builder()
                .branchId(UUID.randomUUID().toString())
                .branchName(dto.getBranchName())
                .branchType(dto.getBranchType())
                .roomCount(dto.getRoomCount())
                .hotel(hotel)
                .build();

        branchRepo.save(branch);
    }

    @Override
    public void update(RequestBranchDto dto, String branchId) {
        // Find existing branch
        Branch branch = branchRepo.findById(branchId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Branch not found with id: %s", branchId)
                ));

        // If hotel is being changed, validate new hotel
        if (dto.getHotelId() != null &&
                !dto.getHotelId().equals(branch.getHotel().getHotelId())) {
            Hotel newHotel = hotelRepo.findById(dto.getHotelId())
                    .orElseThrow(() -> new RuntimeException(
                            String.format("Hotel not found with id: %s", dto.getHotelId())
                    ));
            branch.setHotel(newHotel);
        }

        // Check if new branch name conflicts with existing branches in the same hotel
        if (!dto.getBranchName().equals(branch.getBranchName()) &&
                branchRepo.existsByBranchNameAndHotel(dto.getBranchName(), branch.getHotel())) {
            throw new RuntimeException(
                    String.format("Branch with name '%s' already exists for this hotel", dto.getBranchName())
            );
        }

        // Update branch fields
        branch.setBranchName(dto.getBranchName());
        branch.setBranchType(dto.getBranchType());
        branch.setRoomCount(dto.getRoomCount());

        branchRepo.save(branch);
    }

    @Override
    public void delete(String branchId) {
        // Find branch to check if it exists
        Branch branch = branchRepo.findById(branchId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Branch not found with id: %s", branchId)
                ));

        // Check if branch has associated rooms
        if (branch.getRooms() != null && !branch.getRooms().isEmpty()) {
            throw new RuntimeException(
                    String.format("Cannot delete branch with id: %s. It has associated rooms.", branchId)
            );
        }

        // Check if branch has an associated address
        if (branch.getAddress() != null) {
            throw new RuntimeException(
                    String.format("Cannot delete branch with id: %s. It has an associated address.", branchId)
            );
        }

        branchRepo.deleteById(branchId);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseBranchDto findById(String branchId) {
        Branch branch = branchRepo.findById(branchId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Branch not found with id: %s", branchId)
                ));

        return mapToResponseDto(branch);
    }

    @Override
    @Transactional(readOnly = true)
    public BranchPaginateResponseDto findAll(int page, int size, String searchText) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Branch> branchPage;

        if (searchText == null || searchText.trim().isEmpty()) {
            branchPage = branchRepo.findAll(pageable);
        } else {
            branchPage = branchRepo.findAllByBranchNameContainingIgnoreCase(searchText.trim(), pageable);
        }

        return buildPaginateResponse(branchPage);
    }

    @Override
    @Transactional(readOnly = true)
    public BranchPaginateResponseDto findAllByHotelId(int page, int size, String hotelId, String searchText) {
        // Validate hotel exists
        Hotel hotel = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Hotel not found with id: %s", hotelId)
                ));

        Pageable pageable = PageRequest.of(page, size);
        Page<Branch> branchPage;

        if (searchText == null || searchText.trim().isEmpty()) {
            branchPage = branchRepo.findAllByHotel(hotel, pageable);
        } else {
            branchPage = branchRepo.findAllByHotelAndBranchNameContainingIgnoreCase(
                    hotel, searchText.trim(), pageable);
        }

        return buildPaginateResponse(branchPage);
    }

    /**
     * Helper method to map Branch entity to ResponseBranchDto
     */
    private ResponseBranchDto mapToResponseDto(Branch branch) {
        return ResponseBranchDto.builder()
                .branchId(branch.getBranchId())
                .branchName(branch.getBranchName())
                .branchType(branch.getBranchType())
                .roomCount(branch.getRoomCount())
                .hotelId(branch.getHotel().getHotelId())
                .build();
    }

    /**
     * Helper method to build paginated response
     */
    private BranchPaginateResponseDto buildPaginateResponse(Page<Branch> branchPage) {
        List<ResponseBranchDto> dataList = branchPage.getContent().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return BranchPaginateResponseDto.builder()
                .dataList(dataList)
                .dataCount(branchPage.getTotalElements())
                .build();
    }
}