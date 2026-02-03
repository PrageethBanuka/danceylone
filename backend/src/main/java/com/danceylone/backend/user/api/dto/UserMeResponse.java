package com.danceylone.backend.user.api.dto;

import java.util.UUID;

public record UserMeResponse (
    UUID id,
    String email
) {}
