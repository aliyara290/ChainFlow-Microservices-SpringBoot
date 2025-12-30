package com.aliyara.authservice.repository;


import com.aliyara.authservice.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AppUser, String> {
    boolean existsAppUsersByUsername(String username);
    boolean existsAppUsersByEmail(String email);
    java.util.Optional<AppUser> findByUsername(String username);
}
