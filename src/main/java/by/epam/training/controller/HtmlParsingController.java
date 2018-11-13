package by.epam.training.controller;

import by.epam.training.service.ParsingService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.concurrent.ExecutionException;

@RestController
public class HtmlParsingController {

    private static final Logger logger = Logger.getLogger(HtmlParsingController.class);

    @Autowired
    @Qualifier("nonConcurrentHtmlParsingService")
    private ParsingService nonConcurrentHtmlParsingServiceImpl;

    @Autowired
    @Qualifier("concurrentHtmlParsingService")
    private ParsingService concurrentHtmlParsingServiceImpl;

    @GetMapping("parseConcurrently")
    public Set<String> parseHtmlConcurrently(@RequestParam("url") String url,
                                             @RequestParam(name = "skipCacheCheck", required = false) String skipCacheCheck)  {
        Set<String> parsedLinks;
        if (skipCacheCheck != null && skipCacheCheck.equals("true")) {
            long start = System.currentTimeMillis();
            logger.info("Parsing concurrently without checking cache...");
            parsedLinks = concurrentHtmlParsingServiceImpl.parse(url, true);
            System.out.println(System.currentTimeMillis()-start);
        } else {
            logger.info("Checking cache and parsing concurrently...");
            parsedLinks = concurrentHtmlParsingServiceImpl.parse(url, false);
        }
        return parsedLinks;
    }

    @GetMapping("parse")
    public Set<String> parseHtmlNonConcurrently(@RequestParam("url") String url,
                                                @RequestParam(name = "skipCacheCheck", required = false) String skipCacheCheck) {
        Set<String> parsedLinks;
        if (skipCacheCheck != null && skipCacheCheck.equals("true")) {
            logger.info("Parsing non-concurrently without checking cache...");
            parsedLinks = nonConcurrentHtmlParsingServiceImpl.parse(url, true);
        } else {
            logger.info("Checking cache and parsing non-concurrently...");
            parsedLinks = nonConcurrentHtmlParsingServiceImpl.parse(url, false);
        }
        return parsedLinks;
    }
}

