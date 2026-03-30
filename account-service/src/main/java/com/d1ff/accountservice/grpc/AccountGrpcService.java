package com.d1ff.accountservice.grpc;

import com.d1ff.accountservice.entity.Account;
import com.d1ff.accountservice.repository.AccountRepository;
import com.d1ff.accountservice.service.interfaces.AccountCacheService;
import com.d1ff.common.grpc.account.AccountGrpcServiceGrpc;
import com.d1ff.common.grpc.account.GetAccountByEmailRequest;
import com.d1ff.common.grpc.account.GetAccountByEmailResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Optional;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class AccountGrpcService extends AccountGrpcServiceGrpc.AccountGrpcServiceImplBase {

    private final AccountCacheService accountCacheService;

    @Override
    public void getAccountByEmail(GetAccountByEmailRequest request,
                                  StreamObserver<GetAccountByEmailResponse> responseObserver) {
        try {
            String email = request.getEmail();
            log.info("gRPC getAccountByEmail called with email: {}", email);

            if (email.isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Email cannot be empty")
                        .asRuntimeException());
                return;
            }

            Optional<Account> accountOpt = accountCacheService.findByEmail(email);

            if (accountOpt.isEmpty()) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Account not found for email: " + email)
                        .asRuntimeException());
                return;
            }

            Account account = accountOpt.get();
            GetAccountByEmailResponse.Builder builder = GetAccountByEmailResponse.newBuilder()
                    .setUserId(account.getUserId().toString())
                    .setUsername(account.getUsername() != null ? account.getUsername() : "");

            if (account.getAvatarObjectName() != null) {
                builder.setAvatarObjectName(account.getAvatarObjectName());
            }
            if (account.getAvatarBucketName() != null) {
                builder.setAvatarBucketName(account.getAvatarBucketName());
            }

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in getAccountByEmail gRPC: {}", e.getMessage());
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal error: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}