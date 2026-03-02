package com.d1ff.grpc.client.account;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "grpc.account-client", name = "enabled", havingValue = "true")
public class AccountGrpcClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AccountGrpcClient accountGrpcClient() {
        return new AccountGrpcClient();
    }
}
