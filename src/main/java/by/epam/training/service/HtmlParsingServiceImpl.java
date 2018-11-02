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
import java.util.stream.Collectors;

@Service("regularService")
public class HtmlParsingServiceImpl implements ParsingService {

    @Autowired
    @Qualifier("fileCaching")
    private CachingUtil fileCachingUtil;

    @Autowired
    @Qualifier("redisCaching")
    private CachingUtil redisCachingUtil;

    public Set<String> parse(String url, boolean skipCacheCheck) {
        if (skipCacheCheck) {
            return parseWithoutCheckingCache(url);
        } else {
            return checkCacheAndParse(url);
        }
    }

    @Override
    public Set<String> checkCacheAndParse(String url) {
        Set<String> checkedCache = fileCachingUtil.checkCache(url);
        if (!checkedCache.equals(Collections.emptySet())) {
            return checkedCache;
        } else {
            Set<String> set = new HashSet<>();
            int depthCount = 0;
            collectLinks(url, set, depthCount);
            redisCachingUtil.cache(set, url);
            fileCachingUtil.cache(set, url);
            return set;
        }
    }

    public Set<String> parseWithoutCheckingCache(String url) {
        Set<String> set = new HashSet<>();
        int depthCount = 0;
        collectLinks(url, set, depthCount);
        Set<String> checkedCache = fileCachingUtil.checkCache(url);
        if (checkedCache.equals(Collections.emptySet())) {
            redisCachingUtil.cache(set, url);
            fileCachingUtil.cache(set, url);
        }
        return set;
    }

    private void collectLinks(String url, Set<String> set, int depthCount) {
        if (isUrlValid(url)) {
            Document document = getDocument(url);
            if (document != null) {
                Elements links = document.select("a[href]");
                if (!links.isEmpty()) {
                    Set<String> links1 = addLinksToSet(links, url);
                    set.addAll(links1);
                    if (depthCount < depth) {
                        depthCount++;
                        for (String link1 : links1) {
                            collectLinks(link1, set, depthCount);
                        }
                    }
                }
            }
        }
    }

    private Set<String> addLinksToSet(Elements links, String url) {
        return links.stream().limit(10).map(link -> link.attr("abs:href"))
                .filter(attr -> attr.startsWith(url)).collect(Collectors.toSet());
    }

}
