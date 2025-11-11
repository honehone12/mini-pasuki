package com.mini.pasuki.models;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// import jakarta.persistence.Embeddable;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

//@Embeddable
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class Model {
    @CreatedDate
    protected Instant createdAt;
    @LastModifiedDate
    protected Instant updatedAt;
    protected Instant deletedAt;

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedA) {
        this.deletedAt = deletedA;
    }
}
