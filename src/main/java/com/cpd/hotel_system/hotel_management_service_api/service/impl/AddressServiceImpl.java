package com.cpd.hotel_system.hotel_management_service_api.service.impl;

import com.cpd.hotel_system.hotel_management_service_api.dto.request.RequestAddressDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.ResponseAddressDto;
import com.cpd.hotel_system.hotel_management_service_api.entity.Address;
import com.cpd.hotel_system.hotel_management_service_api.entity.Branch;
import com.cpd.hotel_system.hotel_management_service_api.repo.AddressRepo;
import com.cpd.hotel_system.hotel_management_service_api.repo.BranchRepo;
import com.cpd.hotel_system.hotel_management_service_api.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepo addressRepo;
    private final BranchRepo branchRepo;

    @Override
    public void create(RequestAddressDto dto) {
        // Validate branch exists
        Branch branch = branchRepo.findById(dto.getBranchId())
                .orElseThrow(() -> new RuntimeException(
                        String.format("Branch not found with id: %s", dto.getBranchId())
                ));

        // Check if address already exists for this branch
        if (addressRepo.existsByBranch(branch)) {
            throw new RuntimeException(
                    String.format("Address already exists for branch id: %s", dto.getBranchId())
            );
        }

        // Create new address
        Address address = Address.builder()
                .addressId(UUID.randomUUID().toString())
                .addressLine(dto.getAddressLine())
                .city(dto.getCity())
                .country(dto.getCountry())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .branch(branch)
                .build();

        addressRepo.save(address);
    }

    @Override
    public void update(RequestAddressDto dto, String addressId) {
        // Find existing address
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Address not found with id: %s", addressId)
                ));

        // If branch is being changed, validate new branch
        if (dto.getBranchId() != null &&
                !dto.getBranchId().equals(address.getBranch().getBranchId())) {
            Branch newBranch = branchRepo.findById(dto.getBranchId())
                    .orElseThrow(() -> new RuntimeException(
                            String.format("Branch not found with id: %s", dto.getBranchId())
                    ));

            // Check if new branch already has an address
            if (addressRepo.existsByBranch(newBranch)) {
                throw new RuntimeException(
                        String.format("Address already exists for branch id: %s", dto.getBranchId())
                );
            }

            address.setBranch(newBranch);
        }

        // Update address fields
        address.setAddressLine(dto.getAddressLine());
        address.setCity(dto.getCity());
        address.setCountry(dto.getCountry());
        address.setLatitude(dto.getLatitude());
        address.setLongitude(dto.getLongitude());

        addressRepo.save(address);
    }

    @Override
    public void delete(String addressId) {
        // Check if address exists
        if (!addressRepo.existsById(addressId)) {
            throw new RuntimeException(
                    String.format("Address not found with id: %s", addressId)
            );
        }

        addressRepo.deleteById(addressId);
    }

    @Override
    public ResponseAddressDto findById(String addressId) {
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Address not found with id: %s", addressId)
                ));

        return mapToResponseDto(address);
    }

    @Override
    public ResponseAddressDto findByBranchId(String branchId) {
        // Validate branch exists
        Branch branch = branchRepo.findById(branchId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Branch not found with id: %s", branchId)
                ));

        // Find address by branch
        Address address = addressRepo.findByBranch(branch)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Address not found for branch id: %s", branchId)
                ));

        return mapToResponseDto(address);
    }

    /**
     * Helper method to map Address entity to ResponseAddressDto
     */
    private ResponseAddressDto mapToResponseDto(Address address) {
        return ResponseAddressDto.builder()
                .addressLine(address.getAddressLine())
                .city(address.getCity())
                .country(address.getCountry())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .branchId(address.getBranch().getBranchId())
                .build();
    }
}