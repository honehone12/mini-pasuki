package com.mini.pasuki.controllers;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mini.pasuki.errors.BadRequestException;
import com.mini.pasuki.errors.Errors;
import com.mini.pasuki.models.User;
import com.mini.pasuki.repositories.UserRepository;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository _userRepository;

    public UserController(UserRepository userRepository) {
        _userRepository = userRepository;
    }

    public record NonceRequest(UUID uuid) {

    }

    public record NonceResponse(int nonce) {

    }

    @Async
    @PostMapping(value = "/nonce", consumes = "application/json")
    public CompletableFuture<NonceResponse> nonce(@RequestBody NonceRequest req)
            throws BadRequestException {
        var ex = Example.of(User.fromUuid(req.uuid));
        var user = _userRepository.findOne(ex).orElseThrow(Errors.BadRequest);
        return CompletableFuture.completedFuture(new NonceResponse(user.nonce()));
    }
}
