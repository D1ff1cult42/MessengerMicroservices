package com.d1ff.grpc.client.account;

import com.d1ff.common.grpc.account.AccountGrpcServiceGrpc;
import com.d1ff.common.grpc.account.GetAccountByEmailRequest;
import com.d1ff.common.grpc.account.GetAccountByEmailResponse;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;

import java.util.UUID;

@Slf4j
public class AccountGrpcClient {

    @GrpcClient("account-service")
    private AccountGrpcServiceGrpc.AccountGrpcServiceBlockingStub accountStub;

    public AccountGrpcResponse getNameAndUserIdAndUserIconByEmail(String email) {
        try {
            log.info("gRPC getAccountByEmail called with email: {}", email);

            GetAccountByEmailRequest request = GetAccountByEmailRequest.newBuilder()
                    .setEmail(email)
                    .build();

            GetAccountByEmailResponse response = accountStub.getAccountByEmail(request);

            log.info("gRPC response received for email {}: userId={}, username={}",
                    email, response.getUserId(), response.getUsername());

            return new AccountGrpcResponse(
                    response.getUsername(),
                    UUID.fromString(response.getUserId()),
                    response.getAvatarObjectName().isEmpty() ? null : response.getAvatarObjectName(),
                    response.getAvatarBucketName().isEmpty() ? null : response.getAvatarBucketName()
            );
        } catch (StatusRuntimeException e) {
            log.error("gRPC call failed for email {}: {}", email, e.getStatus());
            throw new RuntimeException("User with email " + email + " not found", e);
        }
    }

    public UUID getUserIdByEmail(String email) {
        try {
            log.info("gRPC getUserIdByEmail called with email: {}", email);

            GetAccountByEmailRequest request = GetAccountByEmailRequest.newBuilder()
                    .setEmail(email)
                    .build();

            GetAccountByEmailResponse response = accountStub.getAccountByEmail(request);

            log.info("gRPC userId received for email {}: {}", email, response.getUserId());

            return UUID.fromString(response.getUserId());
        } catch (StatusRuntimeException e) {
            log.error("gRPC call failed for email {}: {}", email, e.getStatus());
            throw new RuntimeException("User with email " + email + " not found", e);
        }
    }
}
