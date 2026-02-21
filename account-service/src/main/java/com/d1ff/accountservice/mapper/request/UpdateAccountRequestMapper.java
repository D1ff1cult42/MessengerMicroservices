package com.d1ff.accountservice.mapper.request;

import com.d1ff.accountservice.dto.request.UpdateAccountRequest;
import com.d1ff.accountservice.entity.Account;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UpdateAccountRequestMapper {
    @Mappings({})
    void updateFromRequest(@MappingTarget Account account,
                           UpdateAccountRequest request);
}
