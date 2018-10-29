package by.epam.training;

import by.epam.training.htmlparser.HtmlParser;

public class Main {

    private static final String url = "https://jobs.tut.by/";

    public static void main(String[] args) {
        HtmlParser parser = new HtmlParser();
        parser.getLinks(url);
        System.out.println("Finished after depth value reached "+ parser.getDepthCount());
        System.out.println(parser.getLinksSet().size());
    }
}







