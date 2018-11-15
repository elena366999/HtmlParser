package by.epam.training.controller;

import by.epam.training.resolver.SkipCacheCheck;
import by.epam.training.service.ParsingService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

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
                                             @RequestParam(name = "skipCacheCheck", required = false,
                                                     defaultValue = "false") @SkipCacheCheck Boolean skipCacheCheck) {
        Set<String> parsedLinks;
        logger.info("Parsing concurrently without checking cache...");
        parsedLinks = concurrentHtmlParsingServiceImpl.parse(url, skipCacheCheck);

        return parsedLinks;
    }

    @GetMapping("parse")
    public Set<String> parseHtmlNonConcurrently(@RequestParam("url") String url,
                                                @RequestParam(name = "skipCacheCheck", required = false)
                                                @SkipCacheCheck Boolean skipCacheCheck) {
        Set<String> parsedLinks;
        logger.info("Parsing non-concurrently without checking cache...");
        parsedLinks = nonConcurrentHtmlParsingServiceImpl.parse(url, skipCacheCheck);

        return parsedLinks;
    }
}

