package com.cpd.hotel_system.hotel_management_service_api.service.impl;

import com.cpd.hotel_system.hotel_management_service_api.dto.request.RequestHotelDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.ResponseHotelDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.paginate.HotelPaginateResponseDto;
import com.cpd.hotel_system.hotel_management_service_api.entity.Hotel;
import com.cpd.hotel_system.hotel_management_service_api.repo.HotelRepo;
import com.cpd.hotel_system.hotel_management_service_api.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService
{
    private final HotelRepo hotelRepo;

    @Override
    public void create(RequestHotelDto dto) {

    }

    @Override
    public void update(RequestHotelDto dto, String hotelId) {

    }

    @Override
    public void delete(String hotelId) {

    }

    @Override
    public ResponseHotelDto findById(String hotelId) {
        return null;
    }

    @Override
    public HotelPaginateResponseDto findAll(int page, int size, String searchText) {
        return null;
    }

    // map-structs, model-mappers

    private Hotel toHotel(RequestHotelDto dto) {
        return dto==null?null:
                Hotel.builder()
                        .hotelName(dto.getHotelName())
                        .hotelId(UUID.randomUUID().toString())
                        .starRating(dto.getStarRating())
                        .description(dto.getDescription())
                        .build();
    }

}
