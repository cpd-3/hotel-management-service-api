package com.cpd.hotel_system.hotel_management_service_api.service;

import com.cpd.hotel_system.hotel_management_service_api.dto.request.RequestHotelDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.ResponseBranchDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.ResponseHotelDto;
import com.cpd.hotel_system.hotel_management_service_api.dto.response.paginate.HotelPaginateResponseDto;

import java.sql.SQLException;
import java.util.List;

public interface HotelService {
    public void create(RequestHotelDto dto);
    public void update(RequestHotelDto dto, String hotelId) throws SQLException;
    public void delete(String hotelId);
    public ResponseHotelDto findById(String hotelId);
    public HotelPaginateResponseDto findAll(int page, int size, String searchText);
}
