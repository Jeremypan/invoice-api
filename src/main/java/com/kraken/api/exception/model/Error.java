package com.kraken.api.exception.model;

import lombok.Builder;

@Builder
public record Error (
        String timestamp,
        String status,
        String error,
        String detail,
        String path
) {
}
