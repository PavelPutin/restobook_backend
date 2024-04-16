package ru.vsu.restobook_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChangePasswordDto(
        @JsonProperty("newpassword")
        String newPassword
) {
}
