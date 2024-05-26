package ru.vsu.restobook_backend.service;

import lombok.Getter;

import java.util.List;

@Getter
public class NotFoundException extends RuntimeException {
    private final List<String> errors;

    public NotFoundException(List<String> errors) {
        this.errors = errors;
    }
}
