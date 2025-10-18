package com.cpd.hotel_system.hotel_management_service_api.repo;

import com.cpd.hotel_system.hotel_management_service_api.entity.Branch;
import com.cpd.hotel_system.hotel_management_service_api.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepo extends JpaRepository<Room,String> {
    /**
     * Check if a room with the given room number exists in a specific branch
     * @param roomNumber the room number to check
     * @param branch the branch entity
     * @return true if room exists, false otherwise
     */
    boolean existsByRoomNumberAndBranch(String roomNumber, Branch branch);
}
