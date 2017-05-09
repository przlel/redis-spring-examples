package com.example.connector;

import static com.example.SpringRedisApplication.REDIS_SENTINEL_MASTER_ID;
import static com.example.SpringRedisApplication.REDIS_SENTINEL_NODES;
import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { RedisSentinelConnectorTests.RedisConfiguration.class })
public class RedisSentinelConnectorTests {

  @Configuration
  public static class RedisConfiguration {

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
      RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
      redisSentinelConfiguration.setMaster(REDIS_SENTINEL_MASTER_ID);
      redisSentinelConfiguration.setSentinels(REDIS_SENTINEL_NODES);

      return new JedisConnectionFactory(redisSentinelConfiguration);
    }

    @Bean
    public RedisTemplate redisTemplate(final JedisConnectionFactory jedisConnectionFactory) {
      final RedisTemplate redisTemplate = new RedisTemplate<>();
      redisTemplate.setConnectionFactory(jedisConnectionFactory);
      return redisTemplate;
    }
  }

  private final String KEY = "myKeySentinel";
  private final String VALUE = "mayValueSentinel";

  @Inject
  private JedisConnectionFactory jedisConnectionFactory;

  @Inject
  private RedisTemplate redisTemplate;

  @Before
  public void before() throws Exception {
    jedisConnectionFactory.getConnection().flushDb();
  }

  @Test
  public void with_redis_connection() {
    final RedisConnection connection = jedisConnectionFactory.getConnection();

    connection.set(KEY.getBytes(), VALUE.getBytes());
    final byte[] bytes = connection.get(KEY.getBytes());

    assertThat(new String(bytes)).isEqualTo(VALUE);
  }

  @Test
  public void with_redis_template() {
    redisTemplate.opsForValue().set(KEY, VALUE);

    final Object fromRedis = redisTemplate.opsForValue().get(KEY);
    assertThat(fromRedis).isEqualTo(VALUE);
  }
}
