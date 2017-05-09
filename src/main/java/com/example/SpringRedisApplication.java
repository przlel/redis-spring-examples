package com.example;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.connection.RedisNode;

@SpringBootApplication
public class SpringRedisApplication {

  public static String REDIS_HOST = "localhost";
  public static int REDIS_PORT = 6379;
  public static String REDIS_SENTINEL_MASTER_ID = "mymaster";
  //host:port
  public static List<RedisNode> REDIS_SENTINEL_NODES = rangeClosed(5000, 5002)
      .mapToObj(port -> new RedisNode(REDIS_HOST, port))
      .collect(toList());

  public static void main(String[] args) {
    SpringApplication.run(SpringRedisApplication.class, args);
  }
}
