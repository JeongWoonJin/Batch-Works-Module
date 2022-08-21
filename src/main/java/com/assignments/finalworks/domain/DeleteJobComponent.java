package com.assignments.finalworks.domain;


import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DeleteJobComponent extends QuartzJobBean {

    private final DeleteJob deleteJob;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        long runTime = System.currentTimeMillis() / 1000;
        System.out.println("삭제 스케줄러 작동!");
        deleteJob.deleteFile(runTime);
    }
}
