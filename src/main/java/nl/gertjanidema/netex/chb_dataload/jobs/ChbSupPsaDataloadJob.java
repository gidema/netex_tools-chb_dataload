package nl.gertjanidema.netex.chb_dataload.jobs;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;
import java.beans.PropertyEditor;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import nl.gertjanidema.netex.chb_dataload.dto.DlChbPsa;
import nl.gertjanidema.netex.core.util.LocalDateTimeEditor;

@Configuration
@EnableBatchProcessing
public class ChbSupPsaDataloadJob {
    private static Map<Class<? extends Object>, PropertyEditor> customEditors = 
        Map.of(LocalDateTime.class, new LocalDateTimeEditor());

    public ChbSupPsaDataloadJob() {
    }

    @Value("${osm_netex.path.config}")
    private Path configPath;

    @Inject
    EntityManagerFactory entityManagerFactory;
    
    @Inject
    private JobRepository jobRepository;

    Resource getResource() {
        return new FileSystemResource(configPath.resolve("sup_chb_psa.csv"));
    }

    public static String JOB_NAME = "chbSupPsaImportJob";

    
//    @Inject
//    private ItemReader<DlChbPsa> chbPsaItemReader;

    /**
     * Defines the main batch job for importing.
     *
     * @param jobRepository the repository for storing job metadata.
     * @param step1 the step associated with this job.
     * @return a configured Job for importing contacts.
     */
    @Bean
    Job importJob(Step importStep)  {
        Job job = new JobBuilder(JOB_NAME, jobRepository)
            .start(importStep)
            .build();
        return job;
    }

    /**
     * Creates and returns a {@link JpaItemWriter} bean for persisting entities.
     *
     * @return a configured JpaItemWriter for writing entities.
     */
    @Bean
    JpaItemWriter<DlChbPsa> writer() {
        var writer = new JpaItemWriter<DlChbPsa>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    /**
     * Defines the main batch step which includes reading, processing (if any), and writing.
     *
     * @param jobRepository the repository for storing job metadata.
     * @param transactionManager the transaction manager to handle transactional behavior.
     * @return a configured Step for reading and writing Contact entities.
     */
    @Bean
    Step importStep(JobRepository _jobRepository, PlatformTransactionManager _transactionManager,
            FlatFileItemReader<DlChbPsa> csvItemReader) {
        return new StepBuilder("readStep", _jobRepository)
            .<DlChbPsa, DlChbPsa>chunk(100, _transactionManager)
            .reader(csvItemReader)  // null path just for type resolution
            .writer(writer())
            .build();
    }
    
    @Bean
    public FlatFileItemReader<DlChbPsa> csvItemReader(){
        return new FlatFileItemReaderBuilder<DlChbPsa>()
            .name("DlChbPsa")
            .linesToSkip(1)
            .resource(getResource())
            .lineMapper(lineMapper())
            .build();
    }
    
    @SuppressWarnings("static-method")
    @Bean
    public LineMapper<DlChbPsa> lineMapper() {
        DefaultLineMapper<DlChbPsa> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
//        lineTokenizer.setNames("id","firstName","lastName","emailAddress","phoneNumber","address","strVisitDate");
        lineTokenizer.setStrict(true); // Set strict property to false
        lineTokenizer.setNames("user_stop_owner_code", "user_stop_code", "quay_code", "quay_ref",
            "stopplace_code", "stopplace_ref", "user_stop_valid_from", "user_stop_valid_thru");
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        BeanWrapperFieldSetMapper<DlChbPsa> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setCustomEditors(customEditors);
        fieldSetMapper.setTargetType(DlChbPsa.class);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return defaultLineMapper;
    }
}