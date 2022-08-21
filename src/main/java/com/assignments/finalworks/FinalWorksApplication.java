package com.assignments.finalworks;

import com.assignments.finalworks.application.ArchiveDataForm;
import com.assignments.finalworks.config.WebClientConfig;
import com.assignments.finalworks.domain.CollectJob;
import com.assignments.finalworks.infra.ApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

@RequiredArgsConstructor
@SpringBootApplication
@ConfigurationPropertiesScan("com.assignments.finalworks.config.properties")
@EnableAsync
public class FinalWorksApplication implements ApplicationRunner {

    private final ArchiveDataForm archiveDataForm;
    private final CollectJob collectJob;
    private final ApiClient apiControl;
    private final WebClientConfig webClientConfig;

    public static void main(String[] args) {
        SpringApplication.run(FinalWorksApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
		String startDate = "2022-02-06 09:05:00";
		String endDate = "2022-02-10 09:05:00";
		archiveDataForm.makeArchiveDataForm(startDate, endDate);
//		collectJob.setBatchProperties();
//		long runTime = System.currentTimeMillis() / 1000;
//		collectJob.processData(runTime, "101.79.244.11");
    }
}
