package com.d1ff.accountservice.mapper.request;

import com.d1ff.accountservice.dto.request.CreateAccountRequest;
import com.d1ff.accountservice.entity.Account;
import jakarta.validation.constraints.Email;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CreateAccountRequestMapper {
    @Mappings({
            @Mapping(target = "userId", expression = "java(account.setUserId(requestUserId))"),
            @Mapping(target = "email", expression = "java(account.setEmail(requestEmail))")
    })
    Account fromRequest(CreateAccountRequest request,
                     @Context UUID requestUserId,
                     @Context String requestEmail);
}
