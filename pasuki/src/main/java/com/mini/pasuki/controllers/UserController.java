package com.mini.pasuki.controllers;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mini.pasuki.errors.BadRequestException;
import com.mini.pasuki.errors.Errors;
import com.mini.pasuki.errors.InternalServerException;
import com.mini.pasuki.models.Session;
import com.mini.pasuki.models.User;
import com.mini.pasuki.repositories.UserRepository;
import com.mini.pasuki.services.ThisProvider;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

@RestController
@RequestMapping("/user")
public class UserController {
    private final org.slf4j.Logger _log = LoggerFactory.getLogger(this.getClass());
    private final SecureRandom _rand;
    private final UserRepository _userRepository;
    private final ThisProvider _thisProvider;

    public UserController(
            SecureRandom rand,
            UserRepository userRepository,
            ThisProvider thisProvider) {
        _rand = rand;
        _userRepository = userRepository;
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
            String challenge) {
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

            final var newUser = User.create(
                    pubKey,
                    req.name(),
                    req.email(),
                    _thisProvider.ProviderUuid());
            final var newSession = Session.create(
                    req.application(),
                    random,
                    newUser);
            newUser.sessions().add(newSession);
            _userRepository.save(newUser);

            final var challenge = Base64.getEncoder().encodeToString(random);
            final var res = new ClaimResponse(
                    newUser.uuid(),
                    newSession.uuid(),
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
    @PostMapping(value = "/nonce", consumes = "application/json")
    public CompletableFuture<NonceResponse> nonce(@Valid @RequestBody NonceRequest req)
            throws BadRequestException, InternalServerException {
        try {
            final var u = User.fromUuid(req.uuid);
            final var user = _userRepository.findOne(Example.of(u))
                    .orElseThrow(Errors.BadRequest);
            final var res = new NonceResponse(user.nonce());
            return CompletableFuture.completedFuture(res);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            _log.error(e.toString());
            throw new InternalServerException();
        }
    }
}
