package by.epam.training.service;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service("regularService")
public class HtmlParsingServiceImpl implements ParsingService {

    public Set<String> parse(String url) {
        Set<String> set = new HashSet<>();
        int depthCount = 0;
        collectLinks(url, set, depthCount);
        return set;
    }

    private void collectLinks(String url, Set<String> set, int depthCount) {
        if (isUrlValid(url)) {
            Document document = getDocument(url);
            if (document != null) {
                Elements links = document.select("a[href]");
                if (!links.isEmpty()) {
                    Set<String> links1 = addLinksToSet(links, url);
                    set.addAll(links1);
                    if (depthCount < depth) {
                        depthCount++;
                        for (String link1 : links1) {
                            collectLinks(link1, set, depthCount);
                        }
                    }
                }
            }
        }
    }

    private Set<String> addLinksToSet(Elements links, String url) {
        return links.stream().map(link -> link.attr("abs:href"))
                .filter(attr -> attr.startsWith(url)).collect(Collectors.toSet());
    }

}
