package com.d1ff.mailservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Mail service API")
                        .version("1.0")
                        .description("Сервис подтверждения аккаунта через почту. " +
                                    "Необходимо подтверждать аккаунт через почту " +
                                    "и если аккаунт не будет подтвержден в течении n дней - он будет удален, также рассылка на почту " +
                                    "c предупреждением о необходимости подтверждения.")
                        .contact(new Contact()
                                .name("D1ff1cult42")
                                .email("orbitovkek@gmail.com")));
    }
}