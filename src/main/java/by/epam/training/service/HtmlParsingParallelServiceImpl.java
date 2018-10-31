package by.epam.training.service;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service("parallelService")
public class HtmlParsingParallelServiceImpl implements ParsingService {

    private static final Logger logger = Logger.getLogger(HtmlParsingParallelServiceImpl.class);

    public Set<String> parse(String url) {
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
                            collectLinksParallel(link1, set, depthCount);
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
}
