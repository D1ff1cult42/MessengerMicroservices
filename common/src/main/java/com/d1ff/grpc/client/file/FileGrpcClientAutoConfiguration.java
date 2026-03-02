package com.d1ff.grpc.client.file;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "grpc.file-client", name = "enabled", havingValue = "true")
public class FileGrpcClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FileGrpcClient fileGrpcClient() {
        return new FileGrpcClient();
    }
}
