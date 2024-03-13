package org.example.redis_job;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootApplication
@SuppressWarnings("all")
public class RedisJobApplication {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public static void main(String[] args) {
        SpringApplication.run(RedisJobApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> createRecordsRedis();
    }

    private void createRecordsRedis() {
        createRecordsRedis(1000000);
    }

    private void createRecordsRedis(int quantity) {
        for (int i = 0; i < quantity; i++) {
            stringRedisTemplate.opsForValue().set(
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true));
        }
    }

}
