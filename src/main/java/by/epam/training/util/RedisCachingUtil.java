package by.epam.training.util;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.Set;

@Component("redisCaching")
public class RedisCachingUtil implements CachingUtil {

    private Jedis jedis;

    public RedisCachingUtil() {
        this.jedis = new Jedis("localhost");
    }

    @Override
    public void cache(Set<String> links, String url) {
        String trimmedUrl = trimUrl(url);
        links.forEach(link -> jedis.sadd(trimmedUrl, link));
    }

    @Override
    public Set<String> checkCache(String url) {
        String trimmedUrl = trimUrl(url);
        if (jedis.exists(trimmedUrl)) {
            return jedis.smembers(trimmedUrl);
        } else
            return Collections.emptySet();
    }
}
