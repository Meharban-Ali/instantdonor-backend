package com.blood_donor.repository.user_repository;

import com.blood_donor.common.enums.Role;
import com.blood_donor.model.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository
extends JpaRepository<User, Long> {
    public Optional<User> findByEmail(String var1);

    public Optional<User> findByEmailIgnoreCase(String var1);

    public Optional<User> findByPhone(String var1);

    public boolean existsByEmail(String var1);

    public boolean existsByEmailIgnoreCase(String var1);

    public boolean existsByPhone(String var1);

    public Optional<User> findByEmailAndIsActiveTrue(String var1);

    public boolean existsByEmailAndIdNot(String var1, Long var2);

    public List<User> findByIdIn(List<Long> ids);

    public long countByIsActiveTrue();

    public long countByIsActiveFalse();

    public long countByCreatedAtAfter(LocalDateTime var1);

    public long countByIsActiveTrueAndCreatedAtAfter(LocalDateTime var1);

    public long countByCreatedAtBetween(LocalDateTime var1, LocalDateTime var2);

    @Query(value="SELECT COUNT(u) FROM User u WHERE :role MEMBER OF u.roles")
    public long countByRole(@Param(value="role") Role var1);

    @Query(value="SELECT COUNT(u) FROM User u WHERE :role MEMBER OF u.roles AND u.isActive = true")
    public long countByRoleAndIsActiveTrue(@Param(value="role") Role var1);

    @Query(value="SELECT u FROM User u\nWHERE LOWER(u.name)  LIKE LOWER(CONCAT('%', :query, '%'))\n   OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))\n")
    public Page<User> searchUsers(@Param(value="query") String var1, Pageable var2);

    @Query(value="SELECT u FROM User u\nWHERE u.isActive = :isActive\n  AND (LOWER(u.name)  LIKE LOWER(CONCAT('%', :query, '%'))\n    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')))\n")
    public Page<User> searchUsersByStatus(@Param(value="query") String var1, @Param(value="isActive") boolean var2, Pageable var3);

    @Query(value="SELECT u FROM User u\nWHERE :role MEMBER OF u.roles\n  AND (LOWER(u.name)  LIKE LOWER(CONCAT('%', :query, '%'))\n    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')))\n")
    public Page<User> searchUsersByRole(@Param(value="query") String var1, @Param(value="role") Role var2, Pageable var3);

    @Query(value="SELECT u FROM User u\nLEFT JOIN u.donorProfile dp\nWHERE LOWER(dp.city) = LOWER(:city)\n")
    public Page<User> findUsersByCity(@Param(value="city") String var1, Pageable var2);

    @Query(value="SELECT u FROM User u WHERE :role MEMBER OF u.roles")
    public Page<User> findByRoleOrderByCreatedAtDesc(@Param(value="role") Role var1, Pageable var2);

    @Query(value="SELECT u FROM User u WHERE :role MEMBER OF u.roles AND u.isActive = true")
    public Page<User> findActiveUsersByRole(@Param(value="role") Role var1, Pageable var2);

    @Query(value="SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r IN :roles")
    public Page<User> findByRolesIn(@Param(value="roles") List<Role> var1, Pageable var2);

    public Page<User> findByIsActiveTrueOrderByCreatedAtDesc(Pageable var1);

    public Page<User> findByIsActiveFalseOrderByCreatedAtDesc(Pageable var1);

    public Page<User> findAllByOrderByCreatedAtDesc(Pageable var1);

    @Query(value="SELECT u FROM User u WHERE u.isActive = false AND u.updatedAt < :before")
    public List<User> findInactiveUsersBefore(@Param(value="before") LocalDateTime var1);

    @Modifying
    @Transactional
    @Query(value="UPDATE User u SET u.isActive = false, u.updatedAt = :now WHERE u.id IN :userIds")
    public int bulkDeactivateUsers(@Param(value="userIds") List<Long> var1, @Param(value="now") LocalDateTime var2);

    @Modifying
    @Transactional
    @Query(value="UPDATE User u SET u.isActive = true, u.updatedAt = :now WHERE u.id IN :userIds")
    public int bulkActivateUsers(@Param(value="userIds") List<Long> var1, @Param(value="now") LocalDateTime var2);

    @Modifying
    @Transactional
    @Query(value="UPDATE User u SET u.lastLoginAt = :loginTime WHERE u.id = :userId")
    public int updateLastLogin(@Param(value="userId") Long var1, @Param(value="loginTime") LocalDateTime var2);

    @Modifying
    @Transactional
    @Query(value="UPDATE User u SET u.isActive = :status, u.updatedAt = :now WHERE u.id = :userId")
    public int updateUserActiveStatus(@Param(value="userId") Long var1, @Param(value="status") boolean var2, @Param(value="now") LocalDateTime var3);
}

