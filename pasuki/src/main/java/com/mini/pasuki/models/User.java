package com.mini.pasuki.models;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;

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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Embedded
    private Instants instants;
    @Column(unique = true, nullable = false)
    private UUID uuid;
    @Column(nullable = false)
    @Size(min = 32, max = 32)
    private byte[] publicKey;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private UUID provider;
    @Column(nullable = false)
    private Integer nonce;
    @OneToMany(mappedBy = "user")
    private List<Session> sessions;

    public User(byte[] publicKey, String name, String email, UUID provider) {
        final var uuid = UUID.randomUUID();
        this.uuid = uuid;
        this.publicKey = publicKey;
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.nonce = 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instants getInstants() {
        return instants;
    }

    public void setInstants(Instants instants) {
        this.instants = instants;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getProvider() {
        return provider;
    }

    public void setProvider(UUID provider) {
        this.provider = provider;
    }

    public Integer getNonce() {
        return nonce;
    }

    public void setNonce(Integer nonce) {
        this.nonce = nonce;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", instants=" + instants + ", uuid=" + uuid + ", publicKey="
                + Arrays.toString(publicKey) + ", name=" + name + ", email=" + email + ", provider=" + provider
                + ", nonce=" + nonce + ", sessions=" + sessions + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((instants == null) ? 0 : instants.hashCode());
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        result = prime * result + Arrays.hashCode(publicKey);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((provider == null) ? 0 : provider.hashCode());
        result = prime * result + ((nonce == null) ? 0 : nonce.hashCode());
        result = prime * result + ((sessions == null) ? 0 : sessions.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (instants == null) {
            if (other.instants != null)
                return false;
        } else if (!instants.equals(other.instants))
            return false;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        if (!Arrays.equals(publicKey, other.publicKey))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (provider == null) {
            if (other.provider != null)
                return false;
        } else if (!provider.equals(other.provider))
            return false;
        if (nonce == null) {
            if (other.nonce != null)
                return false;
        } else if (!nonce.equals(other.nonce))
            return false;
        if (sessions == null) {
            if (other.sessions != null)
                return false;
        } else if (!sessions.equals(other.sessions))
            return false;
        return true;
    }
}
