package ru.vsu.restobook_backend.service;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationError extends RuntimeException {
    private final List<String> errors;

    public ValidationError(List<String> errors) {
        this.errors = errors;
    }

}
