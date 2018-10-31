package by.epam.training.controller;

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
    @Qualifier("regularService")
    private ParsingService htmlParsingServiceImpl;

    @Autowired
    @Qualifier("parallelService")
    private ParsingService htmlParsingParallelServiceImpl;

    @GetMapping("parseParallel")
    public Set<String> parseHtmlParallel(@RequestParam("url") String url) {
        long start = System.currentTimeMillis();
        Set<String> parsedLinks = htmlParsingParallelServiceImpl.parse(url);
        logger.info("Runtime for parallel: " + (System.currentTimeMillis() - start));
        return parsedLinks;
    }

    @GetMapping("parse")
    public Set<String> parseHtml(@RequestParam("url") String url) {
        long start = System.currentTimeMillis();
        Set<String> parsedLinks = htmlParsingServiceImpl.parse(url);
        logger.info("Runtime for regular: " + (System.currentTimeMillis() - start));
        return parsedLinks;
    }
}

