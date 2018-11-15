package by.epam.training.service;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static by.epam.training.constant.Constants.DEPTH;

@Service("nonConcurrentHtmlParsingService")
public class NonConcurrentHtmlParsingServiceImpl extends ParsingService {

    @Override
    public Set<String> parse(String url, boolean skipCacheCheck) {
        if (skipCacheCheck) {
            return retrieveLinks(url);
        }
        Set<String> linksFromCache = getLinksFromCache(url);
        if (linksFromCache.isEmpty()) {
            logger.info("Retrieving new links from html page (non-concurrently)...");
            return retrieveLinks(url);
        }
        logger.info("Returning cache data (non-concurrently)...");
        return linksFromCache;
    }

    private Set<String> retrieveLinks(String url) {
        Set<String> linksSet = new HashSet<>();
        int depthCount = 0;
        collectLinksNonConcurrently(url, linksSet, depthCount);
        cache(url, linksSet);
        return linksSet;
    }

    private void collectLinksNonConcurrently(String url, Set<String> linksSet, int depthCount) {
        Elements linkElements = getElements(url);
        if (linkElements != null && !linkElements.isEmpty()) {
            logger.debug("Number of link elements for current url retrieved in non-concurrent service: " + linkElements.size());
            Set<String> links = createSetOfLinks(linkElements, url);
            linksSet.addAll(links);

            logger.debug("Links " + links + " were added to the set of links (non-concurrently)");
            increaseDepthAndCollectLinks(linksSet, depthCount, links);
        }
    }

    private Elements getElements(String url) {
        Elements linkElements = null;
        if (isUrlValid(url)) {
            Document document = getDocument(url);
            if (document != null) {
                logger.debug("Url currently processed in non-concurrent service: " + url);
                linkElements = document.select("a[href]");
            }
        }
        return linkElements;
    }

    private void increaseDepthAndCollectLinks(Set<String> linksSet, int depthCount, Set<String> links) {
        if (depthCount <= DEPTH) {
            logger.debug("Current depth count value in non-concurrent service: " + depthCount);
            depthCount++;
            for (String link : links) {
                collectLinksNonConcurrently(link, linksSet, depthCount);
            }
        }
    }

    private Set<String> createSetOfLinks(Elements links, String url) {
        return links.stream().map(l -> l.attr("abs:href"))
                .filter(attr -> attr.startsWith(url)).limit(20).collect(Collectors.toSet());
    }

}
