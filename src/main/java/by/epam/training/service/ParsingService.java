package by.epam.training.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public interface ParsingService {

    Logger logger = Logger.getLogger(ParsingService.class);

    String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    int timeout = 10000;

    int depth = 2;

    Executor executor = Executors.newFixedThreadPool(80);

    Set<String> parse(String value);

    default Document getDocument(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(url).timeout(timeout).ignoreContentType(false).validateTLSCertificates(false).get();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return document;
    }

    default boolean isUrlValid(String url) {
        return url.matches(regex);
    }
}
