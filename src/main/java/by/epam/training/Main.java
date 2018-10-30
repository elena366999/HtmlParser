package by.epam.training;

import by.epam.training.controller.HtmlParserController;
import by.epam.training.service.HtmlParserService;

public class Main {

    private static final String url = "https://jobs.tut.by/";

    public static void main(String[] args) {
        HtmlParserService parser = new HtmlParserService();
        HtmlParserController controller = new HtmlParserController(parser);

        long start = System.currentTimeMillis();
        controller.parseHtml(url);
        long result = System.currentTimeMillis() - start;

        long start2 = System.currentTimeMillis();
        controller.parseHtmlParallel(url);
        long result2 = System.currentTimeMillis() - start2;

        System.out.println("Runtime without multithreading - " + result + " ms");
        System.out.println("Runtime with multithreading - " + result2  + " ms");

    }
}







