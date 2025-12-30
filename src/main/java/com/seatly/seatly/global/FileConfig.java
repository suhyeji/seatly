package com.seatly.seatly.global;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Configuration
@EnableConfigurationProperties(FileProperties.class)
public class FileConfig {
}
