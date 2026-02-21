package com.d1ff.accountservice.mapper.response;

import com.d1ff.accountservice.dto.request.UpdateAccountRequest;
import com.d1ff.accountservice.dto.response.AccountResponse;
import com.d1ff.accountservice.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountResponseMapper {
    AccountResponse toResponse(Account account);
}
