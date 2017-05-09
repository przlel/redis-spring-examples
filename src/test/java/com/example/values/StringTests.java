package com.example.values;

import static java.lang.String.valueOf;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
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
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.TestRedisConfiguration;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestRedisConfiguration.class })
public class StringTests {


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
    connection.flushDb();

    redisTemplate.setValueSerializer(new StringRedisSerializer(UTF_8));
    connection = jedisConnectionFactory.getConnection();
    redisTemplate.opsForValue().set(KEY_STRING, VALUE_STRING);
    redisTemplate.opsForValue().set(KEY_STRING_2, valueOf(VALUE_STRING_2));
    redisTemplate.opsForValue().set(KEY_INT, VALUE_INT + "");

    connection.set(KEY_STRING.getBytes(), VALUE_STRING.getBytes());
    connection.set(KEY_STRING_2.getBytes(), valueOf(VALUE_STRING_2).getBytes());
    connection.set(KEY_INT.getBytes(), valueOf(VALUE_INT).getBytes());
  }

  @Test
  public void get_mget_connection() {
    final byte[] singleValue = connection.get(KEY_STRING.getBytes());
    final List<byte[]> result = connection.mGet(KEY_STRING.getBytes(), KEY_STRING_2.getBytes());
    final List<String> values = result.stream().map(String::new).collect(toList());

    assertThat(new String(singleValue)).isEqualTo(VALUE_STRING);

    assertThat(values)
        .hasSize(2)
        .contains(VALUE_STRING, valueOf(VALUE_STRING_2));

    redisTemplate.opsForValue().get(KEY_STRING);
  }

  @Test
  public void get_mget_template() {
    final String singleValue = (String) redisTemplate.opsForValue().get(KEY_STRING);
    final List<String> values = redisTemplate.opsForValue().multiGet(asList(KEY_STRING, KEY_STRING_2));

    assertThat(singleValue).isEqualTo(VALUE_STRING);

    assertThat(values)
        .hasSize(2)
        .contains(VALUE_STRING, valueOf(VALUE_STRING_2));

  }

  @Test
  public void mset() {
    final ImmutableMap<String, String> map = getMap();
    final Map<byte[], byte[]> collect = convertMap(map);

    connection.mSet(collect);
    redisTemplate.opsForValue().multiSet(map);
  }

  @Test
  public void incr_incr_by() {
    connection.incr(KEY_INT.getBytes());
    redisTemplate.opsForValue().increment(KEY_INT, 1);

    assertThat(new String(connection.get(KEY_INT.getBytes()))).isEqualTo("1");
    assertThat(redisTemplate.opsForValue().get(KEY_INT)).isEqualTo("1");
  }

  private Map<byte[], byte[]> convertMap(final ImmutableMap<String, String> map) {
    return map.entrySet().stream().collect(toMap(item -> item.getKey().getBytes(), item -> item.getValue().getBytes()));
  }

  private ImmutableMap<String, String> getMap() {
    return Maps.toMap(Arrays.asList("k1", "k2", "k3"), input -> input + "Value");
  }
}