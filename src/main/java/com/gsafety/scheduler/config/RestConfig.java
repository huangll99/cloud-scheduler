package com.gsafety.scheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Author: huangll
 * Written on 2018/9/4.
 */
@Configuration
public class RestConfig {

  @Bean
  public RestTemplate restTemplate(){
    return new RestTemplate();
  }
}
