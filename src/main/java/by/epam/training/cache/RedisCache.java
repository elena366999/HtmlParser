package by.epam.training.cache;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.Set;

import static by.epam.training.constant.Constants.EXPIRATION_TIME;

@Component("redisCache")
public class RedisCache implements Cache {

    private static final Logger logger = Logger.getLogger(RedisCache.class);

    private Jedis jedis;

    public RedisCache() {
        this.jedis = new Jedis("localhost");
    }

    @Override
    public void cache(Set<String> links, String url) {
        String trimmedUrl = trimUrl(url);
        if (jedis.exists(trimmedUrl)) {
            jedis.del(trimmedUrl);
        }
            links.forEach(link -> jedis.sadd(trimmedUrl, link));
        logger.debug("Links for url " + url + " were cached using Redis");
        logger.info("Redis cache wilL expire in " + EXPIRATION_TIME + " seconds");

        jedis.expire(trimmedUrl, EXPIRATION_TIME);
    }

    @Override
    public Set<String> checkCache(String url) {
        String trimmedUrl = trimUrl(url);
        if (jedis.exists(trimmedUrl)) {
            logger.info("Some cached data were found in Redis");
            return jedis.smembers(trimmedUrl);
        } else
            logger.info("No Redis cache found for url " + url);
        return Collections.emptySet();
    }
}
