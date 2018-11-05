package by.epam.training.service;

import by.epam.training.cache.Cache;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static by.epam.training.constant.Constants.DEPTH;

@Service("concurrentHtmlParsingService")
public class ConcurrentHtmlParsingServiceImpl implements ParsingService {

    @Autowired
    @Qualifier("fileCache")
    private Cache fileCache;

    @Autowired
    @Qualifier("redisCache")
    private Cache redisCache;

    @Override
    public Set<String> parse(String url, boolean skipCacheCheck) {
        Set<String> cache = getCache(url);
        if (skipCacheCheck || cache.equals(Collections.emptySet())) {
            logger.info("Retrieving new links from html page (concurrently)...");
            return retrieveLinks(url);
        } else {
            logger.info("Returning cache data (concurrently)...");
            return cache;
        }
    }

    private void cache(String url, Set<String> set) {
        logger.info("Writing data to Redis (concurrently)...");
        redisCache.cache(set, url);

        logger.info("Writing data to file (concurrently)...");
        fileCache.cache(set, url);
    }

    private Set<String> getCache(String url) {
        Set<String> checkedRedisCache = redisCache.checkCache(url);
        if (!checkedRedisCache.equals(Collections.emptySet())) {
            return checkedRedisCache;
        } else {
            logger.info("Redis cache is empty. Trying to retrieve cache form file (concurrently)...");
            return fileCache.checkCache(url);
        }
    }

    private Set<String> retrieveLinks(String url) {
        Set<String> linksSet = new HashSet<>();
        int depthCount = 0;
        collectLinksConcurrently(url, linksSet, depthCount);
        cache(url, linksSet);
        return linksSet;
    }

    private void collectLinksConcurrently(String url, Set<String> linksSet, int depthCount) {
        if (isUrlValid(url)) {
            Document document = getDocument(url);
            if (document != null) {
                logger.debug("Url currently processed in concurrent service: " + url);
                Elements linkElements = document.select("a[href]");
                if (!linkElements.isEmpty()) {
                    logger.debug("Number of link elements for current url retrieved in concurrent service: " + linkElements.size());

                    Set<String> links = addLinksToSetConcurrently(linkElements, url);
                    linksSet.addAll(links);
                    logger.debug("Links " + links + " were added to the set of links (concurrently)");

                    increaseDepthAndCollectLinks(linksSet, depthCount, links);
                }
            }
        }
    }

    private void increaseDepthAndCollectLinks(Set<String> linksSet, int depthCount, Set<String> links) {
        logger.debug("Current depth count value in concurrent service: " + depthCount);
        if (depthCount < DEPTH) {
            logger.debug("Current depth count value in concurrent service: " + depthCount);
            depthCount++;
            for (String link : links) {
                collectLinksConcurrently(link, linksSet, depthCount);
            }
        }
    }

    private Set<String> addLinksToSetConcurrently(Elements linkElements, String url) {
        return linkElements.parallelStream().limit(20).map(link -> link.attr("abs:href"))
                .filter(attr -> attr.startsWith(url)).collect(Collectors.toSet());
    }
}
