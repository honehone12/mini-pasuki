package com.mini.pasuki.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.springframework.stereotype.Service;

import com.mini.pasuki.models.Session;
import com.mini.pasuki.models.SignatureElements;
import com.mini.pasuki.models.User;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.cbor.CBORMapper;

@Service
public class ProviderService {
    private final ObjectMapper _cbor = new CBORMapper();
    private final UUID _prividerUuid;

    public ProviderService() {
        final var uuid = System.getenv("PROVIDER_UUID");
        _prividerUuid = UUID.fromString(uuid);
    }

    public UUID ProviderUuid() {
        return _prividerUuid;
    }

    public boolean verifySignature(User user, Session session, String encoded)
            throws NoSuchAlgorithmException {
        final var signature = Base64.getDecoder().decode(encoded);

        final var sigelems = new SignatureElements(
                user.getUuid(),
                user.getName(),
                user.getEmail(),
                user.getProvider(),
                user.getNonce(),
                session.getUuid(),
                session.getApplication(),
                session.getChallenge());
        final var b = _cbor.writeValueAsBytes(sigelems);

        final var hasher = MessageDigest.getInstance("SHA3-256");
        final var hash = hasher.digest(b);

        final var pubKey = new Ed25519PublicKeyParameters(user.getPublicKey());
        final var signer = new Ed25519Signer();
        signer.init(false, pubKey);
        signer.update(hash, 0, 32);
        return signer.verifySignature(signature);
    }
}
