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
import nl.gertjanidema.netex.chb_dataload.ChbQuayReader;
import nl.gertjanidema.netex.chb_dataload.dto.DlChbQuay;
import nl.gertjanidema.netex.chb_dataload.mapping.QuayMapper;
import nl.gertjanidema.netex.core.batch.GzipFileSystemResource;

@Configuration
@EnableBatchProcessing
public class ChbQuayDataloadJob extends AbstractChbDataloadJob {
    
    ChbQuayDataloadJob() {
        super("chb.dl_chb_quay");
    }
    
    @Value("${osm_netex.path.temp}")
    private Path tempPath;

    Path getChbFile() {
        return tempPath.resolve("ExportCHB.xml.gz");
    }

    public static String JOB_NAME = "chbQuayImportJob";

    @Bean
    @StepScope
    ChbQuayReader quayReader() {
        return new ChbQuayReader(stopplaceReader(), quayMapper());
    }
    
    @SuppressWarnings("static-method")
    @Bean
    QuayMapper quayMapper() {
        return new QuayMapper();
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
    @Bean
    static
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
    JpaItemWriter<DlChbQuay> writer() {
        var writer = new JpaItemWriter<DlChbQuay>();
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
        return new StepBuilder("step2", jobRepository)
            .<DlChbQuay, DlChbQuay>chunk(1000, transactionManager)
            .reader(quayReader())  // null path just for type resolution
            .writer(writer())
            .build();
    }
}