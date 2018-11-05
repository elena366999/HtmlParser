package by.epam.training.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Set;

import static by.epam.training.constant.Constants.REGEX;
import static by.epam.training.constant.Constants.TIMEOUT;

public interface ParsingService {

    Logger logger = Logger.getLogger(ParsingService.class);

    Set<String> parse(String value, boolean skipCacheCheck);

    default Document getDocument(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(url).timeout(TIMEOUT).ignoreContentType(false).validateTLSCertificates(false).get();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return document;
    }

    default boolean isUrlValid(String url) {
        return url.matches(REGEX);
    }
}
