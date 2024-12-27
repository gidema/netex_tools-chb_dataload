package nl.gertjanidema.netex.chb_dataload.jobs;

import java.nio.file.Path;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import nl.chb.Stopplace;
import nl.gertjanidema.netex.chb_dataload.dto.DlChbStopPlace;
import nl.gertjanidema.netex.chb_dataload.mapping.ChbStopPlaceProcessor;
import nl.gertjanidema.netex.chb_dataload.mapping.StopPlaceMapper;
import nl.gertjanidema.netex.core.batch.GzipFileSystemResource;

@Configuration
@EnableBatchProcessing
public class ChbStopPlaceDataloadJob extends AbstractChbDataloadJob {

    public ChbStopPlaceDataloadJob() {
        super("chb.dl_chb_stop_place");
    }

    @Value("${osm_netex.path.temp}")
    private Path tempPath;

    Path getChbFile() {
        return tempPath.resolve("ExportCHB.xml.gz");
    }

    public static String JOB_NAME = "chbStopPlaceImportJob";

    @SuppressWarnings("static-method")
    @Bean
    StopPlaceMapper quayMapper() {
        return new StopPlaceMapper();
    }
    
    @Bean
    @StepScope
    StaxEventItemReader<Stopplace> stopplaceReader() {
        return new StaxEventItemReaderBuilder<Stopplace>()
            .name("chbStopplaceReader")
            .resource(new GzipFileSystemResource(getChbFile()))
            .addFragmentRootElements("stopplace")
            .unmarshaller(stopplaceMarshaller())
            .build();
    }

    @SuppressWarnings("static-method")
    @Bean
    Jaxb2Marshaller stopplaceMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Stopplace.class);
        return marshaller;
    }
    
    /**
     * Defines the main batch job for importing.
     *
     * @param jobRepository the repository for storing job metadata.
     * @param step1 the step associated with this job.
     * @return a configured Job for importing contacts.
     */
    @SuppressWarnings("static-method")
    @Bean
    Job importJob(JobRepository jobRepository, 
            @Qualifier("truncateStep") Step truncateStep,
            @Qualifier("importStep") Step importStep)  {
        var job = new JobBuilder(JOB_NAME, jobRepository)
            .start(truncateStep)
            .next(importStep)
            .build();
        return job;
    }

    /**
     * Creates and returns a {@link JpaItemWriter} bean for persisting entities.
     *
     * @return a configured JpaItemWriter for writing entities.
     */
    @Bean
    JpaItemWriter<DlChbStopPlace> writer() {
        var writer = new JpaItemWriter<DlChbStopPlace>();
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
    Step importStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("readStep", jobRepository)
            .<Stopplace, DlChbStopPlace>chunk(1000, transactionManager)
            .reader(stopplaceReader())  // null path just for type resolution
            .processor(processor())
            .writer(writer())
            .build();
    }

    @SuppressWarnings("static-method")
    @Bean
    ChbStopPlaceProcessor processor() {
        return new ChbStopPlaceProcessor();
    }
}