package com.mini.pasuki.models;

import java.util.UUID;

public interface UserEssential {
    Integer getId();

    UUID getUuid();

    byte[] getPublicKey();

    String getName();

    String getEmail();

    Integer getNonce();
}
