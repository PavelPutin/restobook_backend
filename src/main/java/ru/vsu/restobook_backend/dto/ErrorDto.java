package ru.vsu.restobook_backend.dto;

import java.time.Instant;
import java.util.List;

public record ErrorDto(
        Instant dateTime,
        List<String> messages
) {}
