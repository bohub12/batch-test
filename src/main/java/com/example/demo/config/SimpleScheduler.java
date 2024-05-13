package com.example.demo.config;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimpleScheduler {
    private final JobLauncher jobLauncher;
    private final SimpleJobConfig simpleJobConfig;

    @Scheduled(cron = "0 */1 * * * *")
    void execute()
            throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("dateTime", LocalDateTime.now())
                .toJobParameters();

        jobLauncher.run(simpleJobConfig.simpleJob(), jobParameters);
    }
}
