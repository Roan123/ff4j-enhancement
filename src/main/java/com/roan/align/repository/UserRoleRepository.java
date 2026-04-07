package com.roan.align.repository;

import com.roan.align.entity.FF4jUserRole;
import com.roan.align.entity.FF4jUserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for FF4J user role management.
 *
 * @author Roan
 * @date 2026/4/7
 */
@Repository
public interface UserRoleRepository extends JpaRepository<FF4jUserRole, FF4jUserRoleId> {

    /**
     * Find all roles for a given username.
     *
     * @param username the username
     * @return list of user roles
     */
    List<FF4jUserRole> findByUsername(String username);

    /**
     * Find all distinct role names in the system.
     *
     * @return list of all role names
     */
    @Query("SELECT DISTINCT u.roleName FROM FF4jUserRole u")
    List<String> findAllDistinctRoleNames();
}