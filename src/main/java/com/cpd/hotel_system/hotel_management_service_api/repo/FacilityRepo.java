package com.cpd.hotel_system.hotel_management_service_api.repo;

import com.cpd.hotel_system.hotel_management_service_api.entity.Facility;
import com.cpd.hotel_system.hotel_management_service_api.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepo extends JpaRepository<Facility, Long> {
    /**
     * Check if a facility with the given name exists for a specific room
     * @param name the facility name to check
     * @param room the room entity
     * @return true if facility exists, false otherwise
     */
    boolean existsByNameAndRoom(String name, Room room);

    /**
     * Find all facilities for a specific room with pagination
     * @param room the room entity
     * @param pageable pagination information
     * @return paginated list of facilities
     */
    Page<Facility> findAllByRoom(Room room, Pageable pageable);
}
