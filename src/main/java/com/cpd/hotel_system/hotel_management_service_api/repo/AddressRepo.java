package com.cpd.hotel_system.hotel_management_service_api.repo;

import com.cpd.hotel_system.hotel_management_service_api.entity.Address;
import com.cpd.hotel_system.hotel_management_service_api.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepo extends JpaRepository<Address,String> {
    /**
     * Check if an address exists for a given branch
     * @param branch the branch entity
     * @return true if address exists, false otherwise
     */
    boolean existsByBranch(Branch branch);

    /**
     * Find an address by branch
     * @param branch the branch entity
     * @return Optional containing the address if found
     */
    Optional<Address> findByBranch(Branch branch);
}
