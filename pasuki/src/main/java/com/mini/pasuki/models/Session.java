package com.mini.pasuki.models;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;

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
        @Index(name = "idx_session_uuid", unique = true, columnList = "uuid")
})
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Embedded
    private Instants instants;
    @Column(unique = true, nullable = false)
    private UUID uuid;
    @Column(nullable = false)
    private UUID application;
    private Instant loggedInAt;
    private Instant loggedOutAt;
    @Column(columnDefinition = "BINARY(32)", length = 32, nullable = false)
    @Size(min = 32, max = 32)
    private byte[] challenge;
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_session_user"))
    private User user;

    public Session(UUID application, byte[] challenge, User user) {
        final var uuid = UUID.randomUUID();
        this.uuid = uuid;
        this.application = application;
        this.challenge = challenge;
        this.user = user;
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

    public UUID getApplication() {
        return application;
    }

    public void setApplication(UUID application) {
        this.application = application;
    }

    public Instant getLoggedInAt() {
        return loggedInAt;
    }

    public void setLoggedInAt(Instant loggedInAt) {
        this.loggedInAt = loggedInAt;
    }

    public Instant getLoggedOutAt() {
        return loggedOutAt;
    }

    public void setLoggedOutAt(Instant loggedOutAt) {
        this.loggedOutAt = loggedOutAt;
    }

    public byte[] getChallenge() {
        return challenge;
    }

    public void setChallenge(byte[] challenge) {
        this.challenge = challenge;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Session [id=" + id + ", instants=" + instants + ", uuid=" + uuid + ", application=" + application
                + ", loggedInAt=" + loggedInAt + ", loggedOutAt=" + loggedOutAt + ", challenge="
                + Arrays.toString(challenge) + ", user=" + user + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((instants == null) ? 0 : instants.hashCode());
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        result = prime * result + ((application == null) ? 0 : application.hashCode());
        result = prime * result + ((loggedInAt == null) ? 0 : loggedInAt.hashCode());
        result = prime * result + ((loggedOutAt == null) ? 0 : loggedOutAt.hashCode());
        result = prime * result + Arrays.hashCode(challenge);
        result = prime * result + ((user == null) ? 0 : user.hashCode());
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
        Session other = (Session) obj;
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
        if (application == null) {
            if (other.application != null)
                return false;
        } else if (!application.equals(other.application))
            return false;
        if (loggedInAt == null) {
            if (other.loggedInAt != null)
                return false;
        } else if (!loggedInAt.equals(other.loggedInAt))
            return false;
        if (loggedOutAt == null) {
            if (other.loggedOutAt != null)
                return false;
        } else if (!loggedOutAt.equals(other.loggedOutAt))
            return false;
        if (!Arrays.equals(challenge, other.challenge))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }
}
