package ru.vsu.restobook_backend.service;

import lombok.Getter;

import java.util.List;

@Getter
public class RestaurantForbiddenException extends RuntimeException {
    private final List<String> errors;

    public RestaurantForbiddenException(List<String> errors) {
        this.errors = errors;
    }
}
