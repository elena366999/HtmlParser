package by.epam.training.controller;

import by.epam.training.service.ParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class HtmlParsingController {

    @Autowired
    @Qualifier("regularService")
    private ParsingService htmlParsingServiceImpl;

    @Autowired
    @Qualifier("parallelService")
    private ParsingService htmlParsingParallelServiceImpl;

    @GetMapping("parseParallel")
    public Set<String> parseHtmlParallel(@RequestParam("url") String url, @RequestParam(name = "skipCacheCheck", required = false) String skipCacheCheck) {
        Set<String> parsedLinks;
        if (skipCacheCheck != null && skipCacheCheck.equals("true")) {
            parsedLinks = htmlParsingParallelServiceImpl.parse(url, true);
            return parsedLinks;
        } else {
            parsedLinks = htmlParsingParallelServiceImpl.parse(url, false);
            return parsedLinks;
        }
    }

    @GetMapping("parse")
    public Set<String> parseHtml(@RequestParam("url") String url, @RequestParam(name = "skipCacheCheck", required = false) String skipCacheCheck) {
        Set<String> parsedLinks;
        if (skipCacheCheck != null && skipCacheCheck.equals("true")) {
            parsedLinks = htmlParsingServiceImpl.parse(url, true);
            return parsedLinks;
        } else {
            parsedLinks = htmlParsingServiceImpl.parse(url, false);
            return parsedLinks;
        }
    }
}

