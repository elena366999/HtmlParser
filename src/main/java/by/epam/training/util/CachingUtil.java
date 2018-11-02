package by.epam.training.util;

import java.util.Set;

public interface CachingUtil {

    void cache(Set<String> links, String url);

    Set<String> checkCache(String url);

    default String trimUrl(String url) {
        return url.split("//")[1].replace("/", "");
    }

}
