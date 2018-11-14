package by.epam.training.job;

import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class JobRunner {

    private static final Logger logger = Logger.getLogger(JobRunner.class);

    private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();

    public static void runJob() {
        try {
            Scheduler scheduler = schedulerFactory.getScheduler();

            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("deleteCacheFilesTrigger", "group1").startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(60*60*24).repeatForever()).build();

            JobDetail deleteCacheFileJob = JobBuilder.newJob(DeleteCacheFilesJob.class).withIdentity("deleteCacheFileJob", "group1").build();

            scheduler.scheduleJob(deleteCacheFileJob, trigger);
            scheduler.start();

        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
