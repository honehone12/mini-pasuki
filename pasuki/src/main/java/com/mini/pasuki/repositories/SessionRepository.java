package com.mini.pasuki.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mini.pasuki.models.Session;

public interface SessionRepository extends JpaRepository<Session, Integer> {

}
