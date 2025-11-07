package com.mini.pasuki.controllers;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.mini.pasuki.repositories.SessionRepository;
import com.mini.pasuki.repositories.UserRepository;

import jakarta.validation.Valid;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.cbor.CBORMapper;

@RestController
@RequestMapping("/session")
public class SessionController {
    private final Logger _log = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper _cbor = new CBORMapper();
    private final SecureRandom _rand;
    private final UserRepository _userRepository;
    private final SessionRepository _sessionRepository;

    public SessionController(
            SecureRandom rand,
            UserRepository userRepository,
            SessionRepository sessionRepository) {
        _rand = rand;
        _userRepository = userRepository;
        _sessionRepository = sessionRepository;
    }

    public record ClaimRequest(
            @NonNull UUID user,
            @NonNull UUID application) {
    }

    public record ClaimResponse(
            UUID uuid,
            String challenge) {
    }

    public record VerifyRequest() {
    }

    public record VerifyResponse() {
    }

    @Async
    @PostMapping(value = "/claim", consumes = "application/json")
    public CompletableFuture<ClaimResponse> claim(@Valid @RequestBody ClaimRequest req)
            throws BadRequestException, InternalServerException {
        try {
            final var user = _userRepository.findByUuid(req.user())
                    .orElseThrow(Errors.BadRequest);

            final var random = new byte[32];
            _rand.nextBytes(random);
            final var newSession = new Session(req.application(), random, user);
            _sessionRepository.save(newSession);

            final var challenge = Base64.getEncoder().encodeToString(random);
            final var res = new ClaimResponse(newSession.getUuid(), challenge);
            return CompletableFuture.completedFuture(res);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            _log.warn(e.toString());
            throw new InternalServerException();
        }
    }
}
