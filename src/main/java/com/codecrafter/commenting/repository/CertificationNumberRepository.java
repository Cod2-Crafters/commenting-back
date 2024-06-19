package com.codecrafter.commenting.repository;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CertificationNumberRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveCertificationNumber(String email, String certificationNumber) {
        // 인증클릭까지 만료시간 180초(3분)
        redisTemplate.opsForValue().set(email, certificationNumber, 180, TimeUnit.SECONDS);
    }

    public String getCertificationNumber(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    public void removeCertificationNumber(String email) {
        redisTemplate.delete(email);
    }

    public boolean hasKey(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(email));
    }
}