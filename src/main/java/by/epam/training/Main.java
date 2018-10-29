package by.epam.training;

import by.epam.training.controller.HtmlParserController;
import by.epam.training.service.HtmlParserService;

public class Main {

    private static final String url = "https://jobs.tut.by/";

    public static void main(String[] args) {
        HtmlParserService parser = new HtmlParserService();
        HtmlParserController controller = new HtmlParserController(parser);
        System.out.println("Size of links set is " + controller.parseHtml(url));
    }
}







