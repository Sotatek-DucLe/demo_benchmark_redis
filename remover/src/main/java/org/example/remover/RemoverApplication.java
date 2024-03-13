package org.example.remover;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.connection.RedisKeyCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Set;

@SpringBootApplication
@RestController
@RequiredArgsConstructor
@Slf4j
public class RemoverApplication {

    private final StringRedisTemplate redisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(RemoverApplication.class, args);
    }

    @DeleteMapping("/api/v1")
    public long deleteKeysV1(@RequestParam String pattern) {

        long start = System.currentTimeMillis();
        final ScanOptions options = ScanOptions.scanOptions().match(pattern).count(100).build();
        final RedisKeyCommands keyCommands =
                Objects.requireNonNull(this.redisTemplate.getConnectionFactory())
                        .getConnection()
                        .keyCommands();
        try (Cursor<byte[]> cursor = keyCommands.scan(options)) {
            long count = cursor.stream().count();
            if (count > 0) {
                log.info("Preparing delete :: {}", count);
            } else {
                log.info("Nothing to delete!");
            }

            while (cursor.hasNext()) {
                this.redisTemplate.delete(new String(cursor.next()));
            }
        }
        return (System.currentTimeMillis() - start) / 1000;
    }

    @DeleteMapping("/api/v2")
    public void deleteKeysV2(@RequestParam String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            log.info("Preparing delete :: {}", keys.size());
            redisTemplate.delete(keys);
            return;
        }
        log.info("Nothing to delete!");
    }
}
