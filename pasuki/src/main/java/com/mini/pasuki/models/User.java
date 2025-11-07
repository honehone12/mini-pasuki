package com.mini.pasuki.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.lang.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@SQLRestriction("deleted_at IS NULL")
@Table(indexes = {
        @Index(name = "idx_user_uuid", unique = true, columnList = "uuid"),
        @Index(name = "idx_user_name", columnList = "name"),
        @Index(name = "idx_user_email", unique = true, columnList = "email") })
public record User(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Integer id,
        @Embedded Instants instants,
        @Column(unique = true, nullable = false) UUID uuid,
        @Size(min = 32, max = 32) byte[] publicKey,
        @Column(nullable = false) String name,
        @Column(unique = true, nullable = false) String email,
        @Column(nullable = false) UUID provider,
        Integer nonce,
        @OneToMany(mappedBy = "user") List<Session> sessions) {

    @NonNull
    public static User fromUuid(UUID uuid) {
        return new User(null, null, uuid, null, null, null, null, null, null);
    }

    @NonNull
    public static User create(byte[] publicKey, String name, String email, UUID provider) {
        final var uuid = UUID.randomUUID();
        return new User(null, null, uuid, publicKey, name, email, provider, 0, new ArrayList<Session>());
    }
}
