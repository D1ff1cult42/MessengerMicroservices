package com.d1ff.chatservice.grpc;

import com.d1ff.chatservice.dto.response.AccountGrpcResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

//MOCK class for future gRPC client implementation to communicate with Account Service
@Service
public class AccountGrpcClient {
    public AccountGrpcResponse getNameAndUserIdAndUserIconByEmail(String email){
        return new AccountGrpcResponse("John Doe", UUID.randomUUID(), "iconObjectName", "bucketName");
    }
}
