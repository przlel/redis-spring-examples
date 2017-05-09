package com.example;

import static com.example.SpringRedisApplication.REDIS_SENTINEL_MASTER_ID;
import static com.example.SpringRedisApplication.REDIS_SENTINEL_NODES;
import static java.nio.charset.StandardCharsets.UTF_8;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class TestRedisConfiguration {

  @Bean
  @Scope("prototype")
  public JedisConnectionFactory jedisConnectionFactory() {
    RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
    redisSentinelConfiguration.setMaster(REDIS_SENTINEL_MASTER_ID);
    redisSentinelConfiguration.setSentinels(REDIS_SENTINEL_NODES);

    return new JedisConnectionFactory(redisSentinelConfiguration);
  }

  @Bean
  @Scope("prototype")
  public RedisTemplate redisTemplate(final JedisConnectionFactory jedisConnectionFactory) {
    final RedisTemplate redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(jedisConnectionFactory);
    redisTemplate.setValueSerializer(new StringRedisSerializer(UTF_8));
    redisTemplate.setHashKeySerializer(new StringRedisSerializer(UTF_8));
    redisTemplate.setHashValueSerializer(new StringRedisSerializer(UTF_8));
    return redisTemplate;
  }
}
