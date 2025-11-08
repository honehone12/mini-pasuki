package com.mini.pasuki.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mini.pasuki.models.Session;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    @EntityGraph(attributePaths = { "user" })
    Optional<Session> findByUuid(UUID uuid);
}
