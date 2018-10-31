package by.epam.training.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class HtmlParsingServiceImpl implements ParsingService {

    private static final Logger logger = Logger.getLogger(HtmlParsingServiceImpl.class);

    private static final String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    private static final int timeout = 10000;

    private static final int depth = 2;

    private Executor executor = Executors.newFixedThreadPool(80);

    public Set<String> parse(String url) {
        Set<String> set = new HashSet<>();
        int depthCount = 0;
        collectLinks(url, set, depthCount);
        return set;
    }

    public Set<String> parseParallel(String url) {
        CompletableFuture<Set<String>> future = CompletableFuture.supplyAsync(() -> getSetOfLinksParallel(url), executor);
        ((ExecutorService) executor).shutdown();
        Set<String> links = new HashSet<>();
        try {
            links = future.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage(), e);
        }
        return links;
    }

    private Set<String> getSetOfLinksParallel(String url) {
        Set<String> set = new HashSet<>();
        int depthCount = 0;
        collectLinksParallel(url, set, depthCount);
        return set;
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
                            collectLinks(link1, set, depthCount);
                        }
                    }
                }
            }
        }
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

    private Set<String> addLinksToSetParallel(Elements links, String url) {
        return links.parallelStream().map(link -> link.attr("abs:href"))
                .filter(attr -> attr.startsWith(url)).collect(Collectors.toSet());
    }

    private Set<String> addLinksToSet(Elements links, String url) {
        return links.stream().map(link -> link.attr("abs:href"))
                .filter(attr -> attr.startsWith(url)).collect(Collectors.toSet());
    }

    private Document getDocument(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(url).timeout(timeout).ignoreContentType(false).validateTLSCertificates(false).get();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return document;
    }

    private boolean isUrlValid(String url) {
        return url.matches(regex);
    }
}
