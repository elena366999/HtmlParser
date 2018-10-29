package by.epam.training.controller;

import by.epam.training.service.HtmlParserService;

import java.util.Set;

public class HtmlParserController {

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

    public Set<String> parseHtml(String url) {
        htmlParserService.getLinks(url);
        return htmlParserService.getLinksSet();
    }
}
