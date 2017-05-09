package com.example.transaction;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.TestRedisConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestRedisConfiguration.class })
public class TransactionTest {

  @Inject
  private JedisConnectionFactory jedisConnectionFactory;

  @Inject
  private RedisTemplate redisTemplate;

  @Before
  public void before() throws Exception {
    jedisConnectionFactory.getConnection().flushDb();
  }

  @Test
  public void should_be_in_transaction() {
    redisTemplate.execute(new SessionCallback() {
      @Override
      public Object execute(final RedisOperations operations) throws DataAccessException {
        operations.multi();
        operations.opsForValue().set("Key", "Value");
        operations.opsForValue().set("Key2", "Value");
        return operations.exec();
      }
    });

  }
  @Test
  public void should_discard_changes() {
    redisTemplate.execute(new SessionCallback() {
      @Override
      public Object execute(final RedisOperations operations) throws DataAccessException {
        operations.multi();
        operations.opsForValue().set("Key", "Value");
        operations.opsForValue().set("Key2", "Value");
        operations.discard();
        return null;
      }
    });

  }
}
