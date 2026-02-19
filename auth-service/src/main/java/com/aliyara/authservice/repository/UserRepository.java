package com.aliyara.authservice.repository;

import com.aliyara.authservice.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, String> {
    boolean existsAppUsersByUsername(String username);
    boolean existsAppUsersByEmail(String email);

    @Query("SELECT u FROM AppUser u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.authorities WHERE u.username = :username")
    Optional<AppUser> findByUsername(@Param("username") String username);
}