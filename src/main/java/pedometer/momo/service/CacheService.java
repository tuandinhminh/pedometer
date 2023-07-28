package pedometer.momo.service;

import java.util.List;

public interface CacheService {
    void addCache(String key, Object data);
    Object getCache(String key);
    void deleteCache(List<String> key);
}
