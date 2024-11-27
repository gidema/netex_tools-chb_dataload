package nl.gertjanidema.netex.chb_dataload.jobs;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.AbstractStep;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import nl.gertjanidema.netex.chb_dataload.ndov.NdovService;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class LoadChbFilesJob {

    @Inject
    private NdovService ndovService;
//    public List<String> chbFileInfo;
    /**
     * Defines the main batch job for importing.
     *
     * @param jobRepository the repository for storing job metadata.
     * @param truncateStep the step associated with this job.
     * @return a configured Job for exporting quays.
     */
    @SuppressWarnings("static-method")
    @Bean
    Job importJob(JobRepository jobRepository,
            Step downloadStep)  {
        return new JobBuilder("loadChbFilesJob", jobRepository)
            .start(downloadStep)
            .build();
    }

    @Bean
    Step downloadStep() {
        return new DownloadStep();
    }

    class DownloadStep extends AbstractStep {
        public DownloadStep() {
            this.setName("downloadStep");
            this.setAllowStartIfComplete(true);
        }

        @Override
        @Inject
        public void setJobRepository(JobRepository jobRepository) {
            super.setJobRepository(jobRepository);
        }
        
        @Override
        protected void doExecute(StepExecution stepExecution) throws Exception {
            ndovService.downloadChbFile();
            ndovService.downloadPsaFile();
            stepExecution.setExitStatus(ExitStatus.COMPLETED);
            stepExecution.setEndTime(LocalDateTime.now());
        }
    }
}