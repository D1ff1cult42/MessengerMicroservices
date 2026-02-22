package com.d1ff.accountservice.mapper.request;

import com.d1ff.accountservice.dto.request.CreateAccountRequest;
import com.d1ff.accountservice.entity.Account;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CreateAccountRequestMapper {
    @Mapping(source = "requestUserId", target = "userId")
    @Mapping(source = "requestEmail", target = "email")
    @Mapping(target = "avatarBucketName", ignore = true)
    @Mapping(target = "avatarObjectName", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    Account fromRequest(CreateAccountRequest request, UUID requestUserId, String requestEmail);
}
