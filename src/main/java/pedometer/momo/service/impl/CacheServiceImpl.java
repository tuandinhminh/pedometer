package pedometer.momo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pedometer.momo.service.CacheService;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {
    private final RedisTemplate<String, Object> template;

    @Override
    public void addCache(String key, Object data) {
        template.opsForValue().set(key, data, 7, TimeUnit.DAYS);
    }

    @Override
    public Object getCache(String key) {
        return template.opsForValue().get(key);
    }

    @Override
    public void deleteCache(List<String> key) {
        template.delete(key);
    }
}
