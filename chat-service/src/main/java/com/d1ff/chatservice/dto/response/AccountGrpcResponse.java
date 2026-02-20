package com.d1ff.chatservice.dto.response;

import java.util.UUID;

public record AccountGrpcResponse(String name,
                                  UUID userId,
                                  String iconObjectName,
                                  String bucketName) {

}
