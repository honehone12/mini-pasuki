package com.mini.pasuki.models;

import java.util.UUID;

public record SignatureElements(
        UUID uuid,
        String name,
        String email,
        UUID provider,
        int nonce,
        UUID session,
        UUID application,
        byte[] challenge) {
}
