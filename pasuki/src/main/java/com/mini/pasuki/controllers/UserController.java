package com.mini.pasuki.controllers;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mini.pasuki.errors.BadRequestException;
import com.mini.pasuki.errors.InternalServerException;
import com.mini.pasuki.models.Session;
import com.mini.pasuki.models.User;
import com.mini.pasuki.models.SignatureElements;
import com.mini.pasuki.repositories.SessionRepository;
import com.mini.pasuki.repositories.UserRepository;
import com.mini.pasuki.services.ThisProvider;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.cbor.CBORMapper;

@RestController
@RequestMapping("/user")
public class UserController {
    private final Logger _log = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper _cbor = new CBORMapper();
    private final SecureRandom _rand;
    private final UserRepository _userRepository;
    private final SessionRepository _sessionRepository;
    private final ThisProvider _thisProvider;

    public UserController(
            SecureRandom rand,
            UserRepository userRepository,
            SessionRepository sessionRepository,
            ThisProvider thisProvider) {
        _rand = rand;
        _userRepository = userRepository;
        _sessionRepository = sessionRepository;
        _thisProvider = thisProvider;
    }

    public record ClaimRequest(
            @NonNull String publicKey,
            @NonNull String name,
            @NonNull @Email String email,
            @NonNull UUID application) {
    }

    public record ClaimResponse(
            UUID uuid,
            UUID session,
            UUID provider,
            String challenge) {
    }

    public record RegisterRequest(
            @NonNull UUID uuid,
            @NonNull UUID session,
            @NonNull String signature) {
    }

    public record RegisterResponse(Instant loggedInAt) {
    }

    public record NonceRequest(@NonNull UUID uuid) {
    }

    public record NonceResponse(int nonce) {
    }

    @Async
    @PostMapping(value = "/claim", consumes = "application/json")
    public CompletableFuture<ClaimResponse> claim(@Valid @RequestBody ClaimRequest req)
            throws BadRequestException, InternalServerException {
        try {
            if (req.publicKey().length() != 43) {
                throw new BadRequestException();
            }
            final var pubKey = Base64.getDecoder().decode(req.publicKey());
            if (pubKey.length != 32) {
                throw new BadRequestException();
            }

            final var random = new byte[32];
            _rand.nextBytes(random);
            final var provider = _thisProvider.ProviderUuid();

            final var newUser = new User(
                    pubKey,
                    req.name(),
                    req.email(),
                    provider);
            final var newSession = new Session(
                    req.application(),
                    random,
                    newUser);
            newUser.getSessions().add(newSession);
            _userRepository.save(newUser);

            final var challenge = Base64.getEncoder().encodeToString(random);
            final var res = new ClaimResponse(
                    newUser.getUuid(),
                    newSession.getUuid(),
                    provider,
                    challenge);
            return CompletableFuture.completedFuture(res);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            _log.error(e.toString());
            throw new InternalServerException();
        }
    }

    @Async
    @PostMapping(value = "/register", consumes = "application/json")
    public CompletableFuture<RegisterResponse> register(@Valid @RequestBody RegisterRequest req)
            throws InternalServerException, BadRequestException {
        try {
            final var session = _sessionRepository.findByUserUuidAndUuid(
                    req.uuid(),
                    req.session());
            final var user = session.getUser();
            final var signature = Base64.getDecoder().decode(req.signature());

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
            if (!signer.verifySignature(signature)) {
                throw new BadRequestException();
            }

            final var now = Instant.now();
            session.setLoggedInAt(now);
            _sessionRepository.save(session);
            final var res = new RegisterResponse(now);
            return CompletableFuture.completedFuture(res);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            _log.warn(e.toString());
            throw new InternalServerException();
        }
    }

    @Async
    @PostMapping(value = "/nonce", consumes = "application/json")
    public CompletableFuture<NonceResponse> nonce(@Valid @RequestBody NonceRequest req)
            throws InternalServerException {
        try {
            final var user = _userRepository.findByUuid(req.uuid());
            final var res = new NonceResponse(user.getNonce());
            return CompletableFuture.completedFuture(res);
        } catch (Exception e) {
            _log.error(e.toString());
            throw new InternalServerException();
        }
    }
}
