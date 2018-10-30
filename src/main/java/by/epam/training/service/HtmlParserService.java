package by.epam.training.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@Service
public class HtmlParserService {

    private static final Logger logger = Logger.getLogger(HtmlParserService.class);

    private static int depthCount = 0;

    private static final String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    private static final int timeout = 10000;

    private static final int depth = 2; //depth value was decreased to reduce runtime

    private static Set<String> linksSet = new HashSet<>();

    public Set<String> parseHtml(String url) {
        collectLinks(url);
        Set<String> links = linksSet;
        linksSet = new HashSet<>();
        depthCount = 0;
        return links;
    }

    public Set<String> parseHtmlParallel(String url) {
        Executor executor = Executors.newFixedThreadPool(80);
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
        collectLinksParallel(url);
        Set<String> links = linksSet;
        linksSet = new HashSet<>();
        depthCount = 0;
        return links;
    }

    private void collectLinksParallel(String url) {
        if (isUrlValid(url)) {
            Document document = getDocument(url);
            if (document != null) {
                Elements links = document.select("a[href]");
                List<String> linksList = new ArrayList<>();
                if (!links.isEmpty()) {
                    addLinksToListParallel(links, linksList, url);
                    linksSet.addAll(linksList);
                    if (depthCount < depth) {
                        depthCount++;
                        linksList.parallelStream().forEach(this::collectLinksParallel);
                    }
                }
            }
        }
    }

    private void collectLinks(String url) {
        if (isUrlValid(url)) {
            Document document = getDocument(url);
            if (document != null) {
                Elements links = document.select("a[href]");
                List<String> linksList = new ArrayList<>();
                if (!links.isEmpty()) {
                    addLinksToList(links, linksList, url);
                    linksSet.addAll(linksList);
                    if (depthCount < depth) {
                        depthCount++;
                        linksList.forEach(this::collectLinks);
                    }
                }
            }
        }
    }

    private void addLinksToListParallel(Elements links, List<String> linksList, String url) {
        links.forEach(link -> {
            String attr = link.attr("abs:href");
            if (attr.startsWith(url)) {
                linksList.add(attr);
                linksList.parallelStream().forEach(System.out::println);
            }
        });
    }

    private void addLinksToList(Elements links, List<String> linksList, String url) {
        links.forEach(link -> {
            String attr = link.attr("abs:href");
            if (attr.startsWith(url)) {
                linksList.add(attr);
                linksList.forEach(System.out::println);
            }
        });
    }

    private Document getDocument(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(url).timeout(timeout).ignoreContentType(false).validateTLSCertificates(false).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }

    private boolean isUrlValid(String url) {
        return url.matches(regex);
    }
}
