package by.epam.training.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static by.epam.training.constant.Constants.REGEX;
import static by.epam.training.constant.Constants.TIMEOUT;

public class ParserTask implements Callable<Set<String>> {

    private static final Logger logger = Logger.getLogger(ParserTask.class);

    private String url;

    public ParserTask(String url) {
        this.url = url;
    }

    @Override
    public Set<String> call() {
        Set<String> result = new HashSet<>();
        if (isUrlValid(url)) {
            Document document = getDocument(url);
            if (document != null) {
                Elements linkElements = document.select("a[href]");
                if (linkElements != null) {
                    result = linkElements.stream().map(l -> l.attr("abs:href"))
                            .filter(attr -> attr.startsWith(url)).collect(Collectors.toSet());
                }
            }
        }
        return result;
    }

    private boolean isUrlValid(String url) {
        return url.matches(REGEX);
    }

    private Document getDocument(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(url).timeout(TIMEOUT).ignoreContentType(false).validateTLSCertificates(false).get();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return document;
    }
}

