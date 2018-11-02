package by.epam.training.service;

import by.epam.training.util.CachingUtil;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service("parallelService")
public class HtmlParsingParallelServiceImpl implements ParsingService {

    @Autowired
    @Qualifier("fileCaching")
    private CachingUtil fileCachingUtil;

    @Autowired
    @Qualifier("redisCaching")
    private CachingUtil redisCachingUtil;

    @Override
    public Set<String> parse(String url, boolean skipCacheCheck) {
        CompletableFuture<Set<String>> future;
        if (skipCacheCheck) {
            future = CompletableFuture.supplyAsync(() -> parseWithoutCheckingCache(url));
        } else {
            future = CompletableFuture.supplyAsync(() -> checkCacheAndParse(url));
        }
        return getLinksFromFuture(future);
    }

    @Override
    public Set<String> parseWithoutCheckingCache(String url) {
        Set<String> set = new HashSet<>();
        int depthCount = 0;
        collectLinksParallel(url, set, depthCount);
        Set<String> checkedCache = fileCachingUtil.checkCache(url);
        if (checkedCache.equals(Collections.emptySet())) {
            cache(url, set);
        }
        return set;
    }

    private Set<String> getLinksFromFuture(CompletableFuture<Set<String>> future) {
        Set<String> links = new HashSet<>();
        try {
            links = future.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage(), e);
        }
        return links;
    }

    private void cache(String url, Set<String> set) {
        redisCachingUtil.cache(set, url);
        fileCachingUtil.cache(set, url);
    }

    public Set<String> checkCacheAndParse(String url) {
        Set<String> checkedCache = fileCachingUtil.checkCache(url);
        if (!checkedCache.equals(Collections.emptySet())) {
            return checkedCache;
        } else {
            Set<String> set = new HashSet<>();
            int depthCount = 0;
            collectLinksParallel(url, set, depthCount);
            cache(url, set);
            return set;
        }
    }

    private void collectLinksParallel(String url, Set<String> set, int depthCount) {
        if (isUrlValid(url)) {
            Document document = getDocument(url);
            if (document != null) {
                Elements links = document.select("a[href]");
                if (!links.isEmpty()) {
                    Set<String> links1 = addLinksToSetParallel(links, url);
                    set.addAll(links1);
                    if (depthCount < depth) {
                        depthCount++;
                        for (String link1 : links1) {
                            collectLinksParallel(link1, set, depthCount);
                        }
                    }
                }
            }
        }
    }

    private Set<String> addLinksToSetParallel(Elements links, String url) {
        return links.parallelStream().limit(10).map(link -> link.attr("abs:href"))
                .filter(attr -> attr.startsWith(url)).collect(Collectors.toSet());
    }
}
