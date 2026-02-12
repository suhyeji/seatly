package com.seatly.seatly.global;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.seatly.seatly.websocket.RedisExpiredKeyListener;

@EnableScheduling
@Configuration
public class RedisConfig {

  @Bean
  @Primary
  public RedisTemplate<String, String> redisTemplate(
      RedisConnectionFactory factory) {

    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);

    StringRedisSerializer stringSerializer = new StringRedisSerializer();
    template.setKeySerializer(stringSerializer);
    template.setValueSerializer(stringSerializer);
    template.setHashKeySerializer(stringSerializer);
    template.setHashValueSerializer(stringSerializer);

    return template;
  }

  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer(
      RedisConnectionFactory connectionFactory,
      RedisExpiredKeyListener redisExpiredKeyListener) {

    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);

    // __keyevent@0__:expired 채널 구독
    container.addMessageListener(
        redisExpiredKeyListener,
        new PatternTopic("__keyevent@0__:expired"));

    return container;
  }

}
