package com.kraken.api.exception.model;

import lombok.Builder;
import org.springframework.http.HttpStatusCode;

@Builder
public record Error (
        String timestamp,
        String status,
        String error,
        String path
) {
}
