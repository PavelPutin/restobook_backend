package ru.vsu.restobook_backend.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JwtUserUUIDConverter implements Converter<Jwt, UUID> {
    @Override
    public UUID convert(Jwt jwt) {
        String subClaim = jwt.getClaim(JwtClaimNames.SUB);
        return UUID.fromString(subClaim);
    }
}
