package com.mini.pasuki.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class ThisProvider {
    private final UUID _prividerUuid;

    public ThisProvider() {
        final var uuid = System.getenv("PROVIDER_UUID");
        _prividerUuid = UUID.fromString(uuid);
    }

    public UUID ProviderUuid() {
        return _prividerUuid;
    }
}
