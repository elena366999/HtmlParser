package by.epam.training.controller;

import by.epam.training.service.HtmlParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Set;

@Controller
public class HtmlParserController {

    @Autowired
    private HtmlParserService htmlParserService;

    public HtmlParserService getHtmlParserService() {
        return htmlParserService;
    }

    public void setHtmlParserService(HtmlParserService htmlParserService) {
        this.htmlParserService = htmlParserService;
    }

    public HtmlParserController(HtmlParserService htmlParserService) {
        this.htmlParserService = htmlParserService;
    }

    @GetMapping("parseParallel")
    @ResponseBody
    public Set<String> parseHtmlParallel(@RequestParam("url") String url) {
        return htmlParserService.parseHtmlParallel(url);
    }

    @GetMapping("parse")
    @ResponseBody
    public Set<String> parseHtml(@RequestParam("url") String url) {
        return htmlParserService.parseHtml(url);
    }
}

