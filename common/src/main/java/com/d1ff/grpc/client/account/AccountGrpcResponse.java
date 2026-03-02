package com.d1ff.grpc.client.account;

import java.util.UUID;

public record AccountGrpcResponse(
        String name,
        UUID userId,
        String iconObjectName,
        String bucketName
) {}
