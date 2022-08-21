package com.assignments.finalworks.config;

import com.assignments.finalworks.config.properties.BatchProperties;
import com.assignments.finalworks.domain.CollectJobComponent;
import com.assignments.finalworks.domain.DeleteJobComponent;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzJobConfig {

    @Bean
    @ConfigurationProperties(BatchProperties.BATCH_PROPERTIES_PREFIX)
    public BatchProperties batchProperties() {
        return new BatchProperties();
    }

    @Bean
    @Qualifier("collectJobDetail")
    public JobDetail collectJobDetail() {
        return this.buildJobDetail(CollectJobComponent.class, new JobDataMap());
    }

    @Bean
    @Qualifier("deleteJobDetail")
    public JobDetail deleteJobDetail() {
        return this.buildJobDetail(DeleteJobComponent.class, new JobDataMap());
    }

    @Bean
    @Qualifier("collectJobTrigger")
    public Trigger collectJobTrigger() {
        return this.buildJobTrigger(this.batchProperties().getCollectCycle());
    }

    @Bean
    @Qualifier("deleteJobTrigger")
    public Trigger deleteJobTrigger() {
        return this.buildJobTrigger(this.batchProperties().getDeleteCycle());
    }


    private Trigger buildJobTrigger(String scheduleExp) {
        return TriggerBuilder
                .newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(scheduleExp)).build();
    }

    private JobDetail buildJobDetail(Class clazz, JobDataMap params) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAll(params);

        return JobBuilder.newJob(clazz)
                .usingJobData(jobDataMap)
                .build();
    }
}
