package com.mini.pasuki.models;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@SQLRestriction("deleted_at IS NULL")
@Table(indexes = {
        @Index(name = "idx_post_uuid", unique = true, columnList = "uuid")
})
public record Session(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Integer id,
        @Embedded Instants instants,
        @Column(unique = true, nullable = false) UUID uuid,
        @Column(nullable = false) UUID application,
        Instant loggedInAt,
        Instant loggedOutAt,
        @Column(nullable = false) @Size(min = 32, max = 32) byte[] challenge,
        @ManyToOne @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_session_user")) User user) {

    @NonNull
    public static Session create(UUID application, byte[] challenge, User user) {
        final var uuid = UUID.randomUUID();
        return new Session(null, null, uuid, application, null, null, challenge, user);
    }
}
