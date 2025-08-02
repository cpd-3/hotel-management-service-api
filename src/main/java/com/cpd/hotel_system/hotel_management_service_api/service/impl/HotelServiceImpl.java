package com.cpd.hotel_system.hotel_management_service_api.service.impl;

import com.cpd.hotel_system.hotel_management_service_api.dto.request.RequestHotelDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.ResponseBranchDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.ResponseHotelDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.ResponseRoomDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.paginate.HotelPaginateResponseDto;
import com.cpd.hotel_system.hotel_management_service_api.entity.Branch;
import com.cpd.hotel_system.hotel_management_service_api.entity.Hotel;
import com.cpd.hotel_system.hotel_management_service_api.entity.Room;
import com.cpd.hotel_system.hotel_management_service_api.exceptions.EntryNotFoundException;
import com.cpd.hotel_system.hotel_management_service_api.repo.HotelRepo;
import com.cpd.hotel_system.hotel_management_service_api.service.HotelService;
import com.cpd.hotel_system.hotel_management_service_api.util.ByteCodeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {
    private final HotelRepo hotelRepo;
    private final ByteCodeHandler byteCodeHandler;

    @Override
    public void create(RequestHotelDto dto) {
        try {
            hotelRepo.save(toHotel(dto));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(RequestHotelDto dto, String hotelId) throws SQLException {
        Hotel selectedHotel = hotelRepo.findById(hotelId).orElseThrow(()->new EntryNotFoundException("Hotel not found!"));
        selectedHotel.setHotelName(dto.getHotelName());
        selectedHotel.setUpdatedAt(LocalDateTime.now());
        selectedHotel.setDescription(byteCodeHandler.stringToBlob(dto.getDescription()));
        selectedHotel.setStartingFrom(dto.getStartingFrom());
        selectedHotel.setStarRating(dto.getStarRating());
        hotelRepo.save(selectedHotel);
    }

    @Override
    public void delete(String hotelId) {
        hotelRepo.findById(hotelId).orElseThrow(()->new EntryNotFoundException("Hotel not found!"));
        hotelRepo.deleteById(hotelId);
    }

    @Override
    public ResponseHotelDto findById(String hotelId) throws SQLException {
        Hotel selectedHotel = hotelRepo.findById(hotelId).orElseThrow(()->new EntryNotFoundException("Hotel not found!"));
        return toResponseHotelDto(selectedHotel);
    }

    @Override
    public HotelPaginateResponseDto findAll(int page, int size, String searchText) {
        return null;
    }

    // map-structs, model-mappers

    private Hotel toHotel(RequestHotelDto dto) throws SQLException {
        return dto == null ? null :
                Hotel.builder()
                        .hotelName(dto.getHotelName())
                        .hotelId(UUID.randomUUID().toString())
                        .starRating(dto.getStarRating())
                        .description(byteCodeHandler.stringToBlob(dto.getDescription()))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .activeStatus(true)
                        .startingFrom(dto.getStartingFrom())
                        .build();
    }

    private ResponseHotelDto toResponseHotelDto(Hotel hotel) throws SQLException {
        return hotel == null ? null :
                ResponseHotelDto.builder()
                        .hotelId(hotel.getHotelId())
                        .hotelName(hotel.getHotelName())
                        .activeStatus(hotel.isActiveStatus())
                        .startingFrom(hotel.getStartingFrom())
                        .updatedAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .description(byteCodeHandler.blobToString(hotel.getDescription()))
                        .branches(
                                hotel.getBranches().stream().map(e-> {
                                    try {
                                        return toResponseBranchDto(e);
                                    } catch (SQLException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }).toList()
                        )
                        .build();
    }

    private ResponseBranchDto toResponseBranchDto(Branch branch) throws SQLException {
        return branch==null?null:
                ResponseBranchDto.builder()
                        .branchId(branch.getBranchId())
                        .branchName(branch.getBranchName())
                        .roomCount(branch.getRoomCount())
                        .branchType(branch.getBranchType())
                        .build();
    }

}
