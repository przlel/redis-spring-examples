package com.example.values;

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestRedisConfiguration.class })
public class HashTests {


  private final String KEY_STRING = "StringKey";
  private final String KEY_STRING_2 = "StringKey2";
  private final String KEY_INT = "IntKey";
  private final String VALUE_STRING = "mayValue";
  private final String VALUE_STRING_2 = "myValue2";
  private final Integer VALUE_INT = 0;

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
  public void set_mset() {
    connection.hSet(KEY_STRING.getBytes(),KEY_STRING_2.getBytes(),VALUE_STRING_2.getBytes());
    connection.hMSet(KEY_STRING_2.getBytes(), convertMap(getMap()));

    redisTemplate.opsForHash().put(KEY_STRING,KEY_STRING_2,VALUE_STRING_2);
    redisTemplate.opsForHash().putAll(KEY_STRING_2,getMap());

  }

  @Test
  public void bRPop() {
    final List<byte[]> bytes = connection.bRPop((int) SECONDS.toSeconds(1), KEY_STRING.getBytes());
    assertThat(bytes).isNull();

    final String result = (String) redisTemplate.opsForList().rightPop(KEY_STRING, 1, SECONDS);
    assertThat(result).isNull();
  }

  private Map<byte[], byte[]> convertMap(final ImmutableMap<String, String> map) {
    return map.entrySet().stream().collect(toMap(item -> item.getKey().getBytes(), item -> item.getValue().getBytes()));
  }

  private ImmutableMap<String, String> getMap() {
    return Maps.toMap(Arrays.asList("k1", "k2", "k3"), input -> input + "Value");
  }
}