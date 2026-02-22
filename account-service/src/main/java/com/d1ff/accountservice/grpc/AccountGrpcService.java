package com.d1ff.accountservice.grpc;

import com.d1ff.accountservice.entity.Account;
import com.d1ff.accountservice.repository.AccountRepository;
import com.d1ff.common.grpc.account.AccountGrpcServiceGrpc;
import com.d1ff.common.grpc.account.GetAccountByEmailRequest;
import com.d1ff.common.grpc.account.GetAccountByEmailResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class AccountGrpcService extends AccountGrpcServiceGrpc.AccountGrpcServiceImplBase {

    private final AccountRepository accountRepository;

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

            Account account = accountRepository.findByEmail(email).orElse(null);

            if (account == null) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Account not found for email: " + email)
                        .asRuntimeException());
                return;
            }

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
