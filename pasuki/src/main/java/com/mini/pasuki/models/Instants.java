package com.mini.pasuki.models;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EntityListeners;

@EntityListeners(AuditingEntityListener.class)
@Embeddable
public record Instants(
        @CreatedDate
        Instant createdAt,
        @LastModifiedDate
        Instant updatedAt,
        Instant deletedAt) {

}
