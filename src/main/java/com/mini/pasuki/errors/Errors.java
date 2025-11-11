package com.mini.pasuki.errors;

import java.util.function.Supplier;

public class Errors {

    public static final Supplier<BadRequestException> BadRequest
            = () -> new BadRequestException();

    public static final Supplier<InternalServerException> InternalServer
            = () -> new InternalServerException();
}
