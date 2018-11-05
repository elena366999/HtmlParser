package by.epam.training.cache;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static by.epam.training.constant.Constants.ENCODING;
import static by.epam.training.constant.Constants.FOLDER_NAME;

@Component("fileCache")
public class FileCache implements Cache {

    private static final Logger logger = Logger.getLogger(FileCache.class);

    private static File cacheFolder;

    public Set<String> checkCache(String url) {
        cacheFolder = new File(FOLDER_NAME);
        String trimmedUrl = trimUrl(url);
        File retrievedFile = getFile(trimmedUrl);
        if (retrievedFile == null) {
            logger.info("No cache file found for url " + url + ", empty set returned");
            return Collections.emptySet();
        } else {
            logger.info("Some cache file found for url " + url + ", reading data from file...");
            return readCache(retrievedFile);
        }
    }

    public void cache(Set<String> links, String url) {
        String trimmed = trimUrl(url);
        File cacheFile = new File(FOLDER_NAME + "\\" + trimmed + ".txt");
        logger.info("New cache file created");
        try {
            FileUtils.writeLines(cacheFile, links);
            logger.info("Links were successfully written to file");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private File getFile(String trimmedUrl) {
        File file = null;
        if (isValidDirectory(cacheFolder)) {
            List<File> filesInFolder = Arrays.asList(cacheFolder.listFiles());
            if (!filesInFolder.isEmpty()) {
                logger.info("Trying to locate cache file " + trimmedUrl + ".txt");
                file = filesInFolder.stream().filter(f -> FilenameUtils
                        .removeExtension(f.getName()).equals(trimmedUrl)).findFirst().orElse(null);
            }
        }
        return file;
    }

    private Set<String> readCache(File file) {
        List<String> lines = new ArrayList<>();
        try {
            lines = FileUtils.readLines(file, ENCODING);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("Data was successfully read form cache file");
        return new HashSet<>(lines);
    }

    private static boolean isValidDirectory(File file) {
        return file.exists() && file.isDirectory();
    }

}
