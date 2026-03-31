package com.d1ff.accountservice.mapper.request;

import com.d1ff.accountservice.dto.request.UpdateAccountRequest;
import com.d1ff.accountservice.entity.Account;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UpdateAccountRequestMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "avatarBucketName", ignore = true)
    @Mapping(target = "avatarObjectName", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    void updateFromRequest(@MappingTarget Account account,
                           UpdateAccountRequest request);
}
