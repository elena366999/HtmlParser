package by.epam.training.service;

import by.epam.training.cache.Cache;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.Set;

import static by.epam.training.constant.Constants.REGEX;
import static by.epam.training.constant.Constants.TIMEOUT;

public abstract class ParsingService {

    Logger logger = Logger.getLogger(ParsingService.class);

    @Autowired
    @Qualifier("fileCache")
    Cache fileCache;

    @Autowired
    @Qualifier("redisCache")
    Cache redisCache;

    public abstract Set<String> parse(String value, boolean skipCacheCheck);

    Document getDocument(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(url).timeout(TIMEOUT).ignoreContentType(false).validateTLSCertificates(false).get();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return document;
    }

    boolean isUrlValid(String url) {
        return url.matches(REGEX);
    }

    void cache(String url, Set<String> set) {
        logger.info("Writing data to Redis (non-concurrently)...");
        redisCache.cache(set, url);

        logger.info("Writing data to file (non-concurrently)...");
        fileCache.cache(set, url);
    }

    Set<String> getLinksFromCache(String url) {
        Set<String> redisCachedLinks = redisCache.checkCache(url);
        if (!redisCachedLinks.isEmpty()) {
            return redisCachedLinks;
        } else {
            logger.info("Redis cache is empty. Trying to retrieve cache form file (concurrently)...");
            return fileCache.checkCache(url);
        }
    }
}
