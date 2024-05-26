package ru.vsu.restobook_backend.dto;

public record ChangePasswordDto(
        String newPassword,
        String oldPassword
) {
}
