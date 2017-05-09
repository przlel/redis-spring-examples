package com.example.pubsub;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.TestRedisConfiguration;
import com.google.common.base.Charsets;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestRedisConfiguration.class, PubSubTests.RedisConfiguration.class })
public class PubSubTests {

  private static final ChannelTopic topic = new ChannelTopic("testTopic");
  private static String receivedMessage = null;

  @Configuration
  public static class RedisConfiguration {

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(final JedisConnectionFactory jedisConnectionFactory) {
      RedisMessageListenerContainer container = new RedisMessageListenerContainer();
      container.setConnectionFactory(jedisConnectionFactory);
      container.addMessageListener(messageListenerAdapter(), topic);
      return container;
    }

    @Bean
    //subscriber
    public MessageListener messageListenerAdapter() {
      return new MessageListener() {
        @Override
        public void onMessage(final Message message, final byte[] pattern) {
          receivedMessage = new String(message.getBody(), Charsets.UTF_8);
        }
      };
    }

  }

  @Inject
  private RedisTemplate redisTemplate;

  @Test
  public void pub_sub() throws Exception {
    final String message = "TestMessage";

    //publisher
    redisTemplate.convertAndSend(topic.getTopic(), message);

    Thread.sleep(1000);
    assertThat(receivedMessage).isEqualTo(message);
  }
}
