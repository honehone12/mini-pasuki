package com.mini.pasuki.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mini.pasuki.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUuid(UUID uuid);
}
