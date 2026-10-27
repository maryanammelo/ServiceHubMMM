package com.servicehub.servicehub_api.dto;

public record LoginResponse(String token, String tipo, long expiresInSeconds) { }
