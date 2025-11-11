package com.mini.pasuki;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.context.SpringBootTest;

import com.mini.pasuki.controllers.SessionController;
import com.mini.pasuki.controllers.UserController;
import com.mini.pasuki.models.SignatureElements;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.cbor.CBORMapper;

// @SpringBootTest
class PaskiApplicationTests {

    @SuppressWarnings("null")
    @Test
    void apiTest() throws URISyntaxException, InterruptedException,
            ExecutionException, NoSuchAlgorithmException, RuntimeException {
        final var json = new JsonMapper();
        final var cbor = new CBORMapper();

        final var httpClient = HttpClient.newHttpClient();

        final var keyGen = new Ed25519KeyPairGenerator();
        keyGen.init(new KeyGenerationParameters(new SecureRandom(), 256));
        final var keyPair = keyGen.generateKeyPair();
        final var signer = new Ed25519Signer();
        signer.init(true, keyPair.getPrivate());

        final var pubKey = ((Ed25519PublicKeyParameters) keyPair.getPublic()).getEncoded();
        final var pubEnc = Base64.getEncoder().encodeToString(pubKey);

        final var hasher = MessageDigest.getInstance("SHA3-256");

        final var name = "Java Taro";
        final var email = "javataro@spring.com";

        final var application = UUID.randomUUID();

        UUID user;
        UUID session;
        UUID provider;
        byte[] challenge;
        int nonce = 0;
        {
            final var claim = new UserController.ClaimRequest(pubEnc, name, email, application);
            final var body = json.writeValueAsBytes(claim);
            final var url = new URI("http://127.0.0.1:8080/user/claim");
            final var req = HttpRequest.newBuilder(url)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofByteArray(body))
                    .build();
            final var fut = httpClient.sendAsync(req, BodyHandlers.ofByteArray());
            final var res = fut.get();
            if (res.statusCode() != 200) {
                throw new RuntimeException(String.format("status not ok: %d", res.statusCode()));
            }
            final var claimed = json.readValue(res.body(), UserController.ClaimResponse.class);
            user = claimed.uuid();
            session = claimed.session();
            provider = claimed.provider();
            challenge = Base64.getDecoder().decode(claimed.challenge());
        }

        {
            final var sigElems = new SignatureElements(
                    user,
                    name,
                    email,
                    provider,
                    nonce,
                    session,
                    application,
                    challenge);
            final var b = cbor.writeValueAsBytes(sigElems);
            final var hash = hasher.digest(b);
            signer.update(hash, 0, 32);
            final var signature = signer.generateSignature();
            final var sigEnc = Base64.getEncoder().encodeToString(signature);
            final var verify = new SessionController.VerifyRequest(session, sigEnc);
            final var body = json.writeValueAsBytes(verify);
            final var url = new URI("http://127.0.0.1:8080/session/verify");
            final var req = HttpRequest.newBuilder(url)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofByteArray(body))
                    .build();
            final var fut = httpClient.sendAsync(req, BodyHandlers.ofByteArray());
            final var res = fut.get();
            if (res.statusCode() != 200) {
                throw new RuntimeException(String.format("status not ok: %d", res.statusCode()));
            }
            final var verified = json.readValue(res.body(), SessionController.VerifyResponse.class);
            System.out.println(verified.loggedInAt());
            signer.reset();
        }

        {
            final var claim = new SessionController.ClaimRequest(user, application);
            final var body = json.writeValueAsBytes(claim);
            final var url = new URI("http://127.0.0.1:8080/session/claim");
            final var req = HttpRequest.newBuilder(url)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofByteArray(body))
                    .build();
            final var fut = httpClient.sendAsync(req, BodyHandlers.ofByteArray());
            final var res = fut.get();
            if (res.statusCode() != 200) {
                throw new RuntimeException(String.format("status not ok: %d", res.statusCode()));
            }
            final var claimed = json.readValue(res.body(), SessionController.ClaimResponse.class);
            session = claimed.uuid();
            challenge = Base64.getDecoder().decode(claimed.challenge());
        }

        {
            final var claim = new UserController.NonceRequest(user);
            final var body = json.writeValueAsBytes(claim);
            final var url = new URI("http://127.0.0.1:8080/user/nonce");
            final var req = HttpRequest.newBuilder(url)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofByteArray(body))
                    .build();
            final var fut = httpClient.sendAsync(req, BodyHandlers.ofByteArray());
            final var res = fut.get();
            if (res.statusCode() != 200) {
                throw new RuntimeException(String.format("status not ok: %d", res.statusCode()));
            }
            final var claimed = json.readValue(res.body(), UserController.NonceResponse.class);
            nonce = claimed.nonce();
        }

        {
            final var sigElems = new SignatureElements(
                    user,
                    name,
                    email,
                    provider,
                    nonce,
                    session,
                    application,
                    challenge);
            final var b = cbor.writeValueAsBytes(sigElems);
            final var hash = hasher.digest(b);
            signer.update(hash, 0, 32);
            final var signature = signer.generateSignature();
            final var sigEnc = Base64.getEncoder().encodeToString(signature);
            final var verify = new SessionController.VerifyRequest(session, sigEnc);
            final var body = json.writeValueAsBytes(verify);
            final var url = new URI("http://127.0.0.1:8080/session/verify");
            final var req = HttpRequest.newBuilder(url)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofByteArray(body))
                    .build();
            final var fut = httpClient.sendAsync(req, BodyHandlers.ofByteArray());
            final var res = fut.get();
            if (res.statusCode() != 200) {
                throw new RuntimeException(String.format("status not ok: %d", res.statusCode()));
            }
            final var verified = json.readValue(res.body(), SessionController.VerifyResponse.class);
            System.out.println(verified.loggedInAt());
            signer.reset();
        }
    }
}
