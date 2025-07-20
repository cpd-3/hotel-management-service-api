package com.cpd.hotel_system.hotel_management_service_api.repo;

import com.cpd.hotel_system.hotel_management_service_api.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepo extends JpaRepository<Facility, Long> {
}
