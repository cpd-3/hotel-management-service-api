package com.cpd.hotel_system.hotel_management_service_api.repo;

import com.cpd.hotel_system.hotel_management_service_api.entity.Room;
import com.cpd.hotel_system.hotel_management_service_api.entity.RoomImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomImageRepo extends JpaRepository<RoomImage,Long> {

    /**
     * Find all images for a specific room with pagination
     * @param room the room entity
     * @param pageable pagination information
     * @return paginated list of room images
     */
    Page<RoomImage> findAllByRoom(Room room, Pageable pageable);
}
