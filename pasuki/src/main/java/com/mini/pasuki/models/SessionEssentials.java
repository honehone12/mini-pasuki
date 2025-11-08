package com.mini.pasuki.models;

import java.util.UUID;

public interface SessionEssentials {
    Integer getId();

    UUID getUuid();

    byte[] getChallenge();

    User getUser();
}
