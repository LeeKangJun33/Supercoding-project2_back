package com.example.project02.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@RequiredArgsConstructor
@Repository
public class SmsCertification {

    private final String PREFIX = "sms:";
    private final int LIMIT_TIME = 3 * 60;

    private final StringRedisTemplate stringRedisTemplate;

    public void createSmsCertification(String phone, String certificationNumber) { //(3)
        stringRedisTemplate.opsForValue()
                .set(PREFIX + phone, certificationNumber, Duration.ofSeconds(LIMIT_TIME));
    }

    public String getSmsCertification(String phone) { // (4)
        return stringRedisTemplate.opsForValue().get(PREFIX + phone);
    }

    public void removeSmsCertification(String phone) { // (5)
        stringRedisTemplate.delete(PREFIX + phone);
    }

    public boolean hasKey(String phone) {  //(6)
        return stringRedisTemplate.hasKey(PREFIX + phone);
    }
}
