package com.example.values;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;

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
public class ListTests {


  private final String KEY_STRING = "StringKey";
  private final String VALUE_STRING = "mayValue";
  private final String VALUE_STRING_2 = "myValue2";

  @Inject
  private JedisConnectionFactory jedisConnectionFactory;

  @Inject
  private RedisTemplate redisTemplate;
  private RedisConnection connection;

  @Before
  public void before() throws Exception {

    connection = jedisConnectionFactory.getConnection();
    connection.flushDb();
  }

  @Test
  public void push() {
    connection.lPush(KEY_STRING.getBytes(), VALUE_STRING.getBytes(), VALUE_STRING_2.getBytes());
    connection.rPush(KEY_STRING.getBytes(), VALUE_STRING_2.getBytes());

    redisTemplate.opsForList().leftPushAll(KEY_STRING, VALUE_STRING, VALUE_STRING_2);
    redisTemplate.opsForList().rightPush(KEY_STRING, VALUE_STRING_2);

    assertThat(connection.lLen(KEY_STRING.getBytes())).isEqualTo(3);
    assertThat(redisTemplate.opsForList().size(KEY_STRING)).isEqualTo(3);

  }

  @Test
  public void bRPop() {
    final List<byte[]> bytes = connection.bRPop((int) SECONDS.toSeconds(1), KEY_STRING.getBytes());
    assertThat(bytes).isNull();

    final String result = (String) redisTemplate.opsForList().rightPop(KEY_STRING, 1, SECONDS);
    assertThat(result).isNull();
  }
}