package by.epam.training.service;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static by.epam.training.constant.Constants.DEPTH;

@Service("concurrentHtmlParsingService")
public class ConcurrentHtmlParsingServiceImpl extends ParsingService {

    private final ExecutorService executorService = Executors
            .newFixedThreadPool(80);

    private ReentrantLock lock = new ReentrantLock();

    private Set<String> currentlyProcessedUrls = Sets.newConcurrentHashSet();

    @Override
    public Set<String> parse(String url, boolean skipCacheCheck) {
        if (skipCacheCheck) {
            return retrieveLinks(url);
        }
        Set<String> linksFromCache = getLinksFromCache(url);
        if (linksFromCache.isEmpty()) {
            logger.info("Retrieving new links from html page (concurrently)...");
            return retrieveLinks(url);
        }
        logger.info("Returning cache data (concurrently)...");
        return linksFromCache;
    }

    private Set<String> retrieveLinks(String url) {
        int depthCount = 0;
        Set<String> links = new HashSet<>();
        links = submitTaskToExecService(url, links);
        Set<String> result = new HashSet<>(links);
        try {
            result.addAll(collectLinks(links, depthCount + 1));
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage(), e);
        }

        cacheResultsInLock(url, result);
        return result;
    }

    private Set<String> submitTaskToExecService(String url, Set<String> links) {
        try {
            links = executorService.submit(new ParserTask(url)).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage(), e);
        }
        return links;
    }

    private void cacheResultsInLock(String url, Set<String> result) {
        if (!currentlyProcessedUrls.contains((url))) {
            lock.lock();
            try {
                currentlyProcessedUrls.add(url);
                cache(url, result);
                currentlyProcessedUrls.remove(url);
            } finally {
                lock.unlock();
            }
        }
    }

    private Set<String> collectLinks(Set<String> links, int depthCount) throws InterruptedException, ExecutionException {
        Set<String> result = new HashSet<>();
        if (depthCount < DEPTH) {
            List<ParserTask> tasks = links.stream().map(ParserTask::new).collect(Collectors.toList());
            List<Future<Set<String>>> futureList = executorService.invokeAll(tasks);

            for (Future<Set<String>> future : futureList) {
                Set<String> l = future.get();
                result.addAll(l);
                result.addAll(collectLinks(l, depthCount + 1));
            }
        }
        return result;
    }
}
