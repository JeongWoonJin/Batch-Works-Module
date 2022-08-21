package com.assignments.finalworks.domain;


import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CollectJobComponent extends QuartzJobBean {

    private final CollectJob collectJob;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        long runTime = System.currentTimeMillis() / 1000;
        this.collectJob.batchCollect(runTime);
    }
}
