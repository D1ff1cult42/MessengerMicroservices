package com.d1ff.accountservice.mapper.response;

import com.d1ff.accountservice.dto.response.AccountResponse;
import com.d1ff.accountservice.entity.Account;
import com.d1ff.dto.response.PresignedUrlResponse;
import com.d1ff.grpc.client.FileGrpcClient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountResponseMapper {

    default AccountResponse toResponse(Account account, FileGrpcClient fileService) {
        if (account == null) return null;

        PresignedUrlResponse icon = null;
        if (account.getAvatarBucketName() != null && account.getAvatarObjectName() != null) {
            icon = fileService.getPresignedUrl(account.getAvatarBucketName(), account.getAvatarObjectName());
        }

        return new AccountResponse(
                account.getUserId(),
                account.getEmail(),
                account.getUsername(),
                account.getDescription(),
                icon != null ? icon.url() : null,
                icon != null ? icon.ttl() : null,
                account.getCreatedAt(),
                account.isVerified()
        );
    }
}
