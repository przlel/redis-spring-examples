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
public class ZSetsTests {


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
  public void add() {
    connection.zAdd(KEY_STRING.getBytes(), 9, VALUE_STRING.getBytes());
    connection.zAdd(KEY_STRING.getBytes(), 8, VALUE_STRING_2.getBytes());

    redisTemplate.opsForZSet().add(KEY_STRING, VALUE_STRING, 9);
    redisTemplate.opsForZSet().add(KEY_STRING, VALUE_STRING_2, 8);

  }

  @Test
  public void range_by_score() {
    connection.zAdd(KEY_STRING.getBytes(), 9, VALUE_STRING.getBytes());
    connection.zAdd(KEY_STRING.getBytes(), 8, VALUE_STRING_2.getBytes());
    connection.zAdd(KEY_STRING.getBytes(), 7, "abc".getBytes());


    redisTemplate.opsForZSet().add(KEY_STRING, VALUE_STRING, 9);
    redisTemplate.opsForZSet().add(KEY_STRING, VALUE_STRING_2, 8);
    redisTemplate.opsForZSet().add(KEY_STRING, "abc", 7);

    final Set<byte[]> zRange = connection.zRangeByScore(KEY_STRING.getBytes(), 8, 9);
    final Set rangeByScore = redisTemplate.opsForZSet().rangeByScore(KEY_STRING, 8, 9);

    assertThat(zRange).hasSize(2);
    assertThat(rangeByScore).hasSize(2);

  }
}