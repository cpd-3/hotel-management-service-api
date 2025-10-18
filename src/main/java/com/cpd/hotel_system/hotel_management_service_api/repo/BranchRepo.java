package com.cpd.hotel_system.hotel_management_service_api.repo;

import com.cpd.hotel_system.hotel_management_service_api.entity.Branch;
import com.cpd.hotel_system.hotel_management_service_api.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepo extends JpaRepository<Branch,String> {
    /**
     * Check if a branch with the given name exists for a specific hotel
     * @param branchName the branch name to check
     * @param hotel the hotel entity
     * @return true if branch exists, false otherwise
     */
    boolean existsByBranchNameAndHotel(String branchName, Hotel hotel);

    /**
     * Find all branches with pagination and search by branch name
     * @param branchName the search text for branch name
     * @param pageable pagination information
     * @return paginated list of branches
     */
    Page<Branch> findAllByBranchNameContainingIgnoreCase(String branchName, Pageable pageable);

    /**
     * Find all branches for a specific hotel with pagination
     * @param hotel the hotel entity
     * @param pageable pagination information
     * @return paginated list of branches
     */
    Page<Branch> findAllByHotel(Hotel hotel, Pageable pageable);

    /**
     * Find all branches for a specific hotel with search and pagination
     * @param hotel the hotel entity
     * @param branchName the search text for branch name
     * @param pageable pagination information
     * @return paginated list of branches
     */
    Page<Branch> findAllByHotelAndBranchNameContainingIgnoreCase(
            Hotel hotel, String branchName, Pageable pageable);
}
