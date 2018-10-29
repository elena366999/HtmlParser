package by.epam.training.htmlparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HtmlParser {

    private int depthCount = 0;

    private static final int timeout = 7000;

    private static final int depth = 2; //depth value was decreased to reduce runtime

    private Set<String> linksSet = new HashSet<>();

    public Set<String> getLinksSet() {
        return linksSet;
    }

    public int getDepthCount() {
        return depthCount;
    }

    public void getLinks(String url) {
        if (isUrlValid(url)) {
            Document document = getDocument(url);
            if (!(document == null)) {
                Elements links = document.select("a[href]");
                List<String> linksList = new ArrayList<>();
                if (!links.isEmpty()) {
                    addLinksToList(links, linksList);
                    linksSet.addAll(linksList);
                    if (depthCount < depth) {
                        depthCount++;
                        linksList.forEach(this::getLinks);
                    }
                }
            }
        }
    }

    private void addLinksToList(Elements links, List<String> linksList) {
        for (Element link : links) {
            linksList.add(link.attr("abs:href"));
            linksList.forEach(System.out::println);
        }
    }

    private Document getDocument(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(url).timeout(timeout).ignoreContentType(false).validateTLSCertificates(false).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }

    private boolean isUrlValid(String url) {
        return !(url == null) && !url.isEmpty() &&( url.startsWith("https") || url.startsWith("http"));
    }
}
