package com.example.values;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

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
public class SetsTests {


  private final String KEY_STRING = "StringKey";
  private final String KEY_STRING_2 = "StringKey2";
  private final String VALUE_STRING = "mayValue";
  private final String VALUE_STRING_2 = "myValue2";
  private final String VALUE_STRING_3 = "myValue3";

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
  public void add_move() {
    connection.sAdd(KEY_STRING.getBytes(), VALUE_STRING.getBytes(), VALUE_STRING_2.getBytes(), VALUE_STRING_3.getBytes());
    redisTemplate.opsForSet().add(KEY_STRING, VALUE_STRING, VALUE_STRING_2, VALUE_STRING_3);

    connection.sMove(KEY_STRING.getBytes(), KEY_STRING_2.getBytes(), VALUE_STRING_3.getBytes());
    redisTemplate.opsForSet().move(KEY_STRING, VALUE_STRING_3, KEY_STRING_2.getBytes());
  }

  @Test
  public void diff() {
    connection.sAdd(KEY_STRING.getBytes(), VALUE_STRING.getBytes(), VALUE_STRING_2.getBytes());
    connection.sAdd(KEY_STRING_2.getBytes(), VALUE_STRING.getBytes());
    redisTemplate.opsForSet().add(KEY_STRING, VALUE_STRING, VALUE_STRING_2);
    redisTemplate.opsForSet().add(KEY_STRING_2, VALUE_STRING);

    final Set<byte[]> bytes = connection.sDiff(KEY_STRING.getBytes(), KEY_STRING_2.getBytes());
    final Set difference = redisTemplate.opsForSet().difference(KEY_STRING, KEY_STRING_2);

    connection.sDiffStore("NewKey".getBytes(), KEY_STRING.getBytes(), KEY_STRING_2.getBytes());
    redisTemplate.opsForSet().differenceAndStore( KEY_STRING, KEY_STRING_2, "NewKey");

    assertThat(bytes).hasSize(1);
    assertThat(difference).hasSize(1);

  }
}