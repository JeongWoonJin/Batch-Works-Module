package com.assignments.finalworks.application.cronjob;

import javax.annotation.PostConstruct;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CronJob {

	private final Scheduler scheduler;
	private final JobDetail collectJobDetail;
	private final JobDetail deleteJobDetail;
	private final Trigger collectJobTrigger;
	private final Trigger deleteJobTrigger;

	public CronJob(Scheduler scheduler, @Qualifier("collectJobDetail") JobDetail collectJobDetail, @Qualifier("deleteJobDetail") JobDetail deleteJobDetail,
		@Qualifier("collectJobTrigger") Trigger collectJobTrigger,
		@Qualifier("deleteJobTrigger") Trigger deleteJobTrigger) {
		this.scheduler = scheduler;
		this.collectJobDetail = collectJobDetail;
		this.deleteJobDetail = deleteJobDetail;
		this.collectJobTrigger = collectJobTrigger;
		this.deleteJobTrigger = deleteJobTrigger;
	}

	@PostConstruct
	public void start() throws SchedulerException {
		try {
			this.scheduler.scheduleJob(this.collectJobDetail, this.collectJobTrigger);
			this.scheduler.scheduleJob(this.deleteJobDetail, this.deleteJobTrigger);
		} catch (SchedulerException e) {
			throw new SchedulerException("failed to run quartz job", e);
		}
	}
}
