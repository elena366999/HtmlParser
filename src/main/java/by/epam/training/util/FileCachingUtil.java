package by.epam.training.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Component("fileCaching")
public class FileCachingUtil implements CachingUtil {

    private static final Logger logger = Logger.getLogger(FileCachingUtil.class);

    private static final String ENCODING = "UTF-8";

    private static final String FOLDER_NAME = "src\\main\\java\\by\\epam\\training\\cache";

    private static File cacheFolder;

    private static String trimmedUrl;

    private static final String MESSAGE = "NoCache";

    public Set<String> checkCache(String url) {
        prepare(url);
        File retrievedFile = getFile();
        if (retrievedFile == null || retrievedFile.getName().equals(MESSAGE)) {
            return Collections.emptySet();
        } else {
            return readCache(url);
        }
    }

    public void cache(Set<String> links, String url) {
        String trimmed = trimUrl(url);
        File file2 = new File(FOLDER_NAME + "\\" + trimmed + ".txt");
        try {
            FileUtils.writeLines(file2, links);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void prepare(String url) {
        cacheFolder = new File(FOLDER_NAME);
        trimmedUrl = trimUrl(url);
    }

    private File getFile() {
        File file = null;
        if (isValidDirectory(cacheFolder)) {
            List<File> filesInFolder = Arrays.asList(cacheFolder.listFiles());
            if (!filesInFolder.isEmpty()) {
                file = filesInFolder.stream().filter(file1 -> FilenameUtils.removeExtension(file1.getName()).equals(trimmedUrl)).findFirst().orElse(new File(MESSAGE));
            }
        }
        return file;
    }

    private Set<String> readCache(String url) {
        File file = getFile();
        List<String> lines = new ArrayList<>();
        try {
            lines = FileUtils.readLines(file, ENCODING);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return new HashSet<>(lines);
    }

    private static boolean isValidDirectory(File file) {
        return file.exists() && file.isDirectory();
    }

}
