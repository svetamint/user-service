package com.godeltech.users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import validation.ProtoValidationService;

@Configuration
public class UserConfiguration {

  @Bean
  public ProtoValidationService validationService() {
    return new ProtoValidationService();
  }
}
