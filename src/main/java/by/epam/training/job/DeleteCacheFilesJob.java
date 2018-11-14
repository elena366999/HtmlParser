package by.epam.training.job;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;

import static by.epam.training.constant.Constants.FOLDER_NAME;

public class DeleteCacheFilesJob implements Job {

    private static final Logger logger = Logger.getLogger(DeleteCacheFilesJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext){
        System.out.println("Running DeleteCacheFilesJob...");
        File cacheDir = new File(FOLDER_NAME);
        if (cacheDir.exists()) {
            long purgeTime = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
            File[] listFiles = cacheDir.listFiles();
            if (listFiles != null && listFiles.length != 0) {
                for (File file : listFiles) {
                    if (file.lastModified() < purgeTime) {
                        if (!file.delete()) {
                            logger.error("Unable to delete file: " + file);
                        }
                    }
                }
            }
        }
    }
}
