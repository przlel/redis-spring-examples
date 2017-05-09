package com.example.database;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.TestRedisConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestRedisConfiguration.class })
public class MultipleDatabaseTests {
  private final String KEY_STRING = "StringKey";
  private final String VALUE_STRING = "mayValue";
  private final String VALUE_STRING_2 = "myValue2";


  @Inject
  private JedisConnectionFactory jedisConnectionFactory;

  @Inject
  private Provider<RedisTemplate> redisTemplateProvider;

  private RedisConnection connection;


  @Before
  public void before() throws Exception {
    connection = jedisConnectionFactory.getConnection();
    connection.flushDb();

  }

  @Test
  public void multiple_database() {
    final RedisTemplate redisTemplateDB1 = redisTemplateProvider.get();
    ((JedisConnectionFactory) redisTemplateDB1.getConnectionFactory()).setDatabase(1);

    final RedisTemplate redisTemplateDB2 = redisTemplateProvider.get();
    ((JedisConnectionFactory) redisTemplateDB1.getConnectionFactory()).setDatabase(2);

    redisTemplateDB1.opsForValue().set(KEY_STRING, VALUE_STRING);
    redisTemplateDB2.opsForValue().set(KEY_STRING, VALUE_STRING_2);

    assertThat(redisTemplateDB1.opsForValue().get(KEY_STRING)).isEqualTo(VALUE_STRING);
    assertThat(redisTemplateDB2.opsForValue().get(KEY_STRING)).isEqualTo(VALUE_STRING_2);

    ((JedisConnectionFactory) redisTemplateDB1.getConnectionFactory()).getConnection().flushDb();
    assertThat(redisTemplateDB1.opsForValue().get(KEY_STRING)).isNull();
    assertThat(redisTemplateDB2.opsForValue().get(KEY_STRING)).isEqualTo(VALUE_STRING_2);
  }
}