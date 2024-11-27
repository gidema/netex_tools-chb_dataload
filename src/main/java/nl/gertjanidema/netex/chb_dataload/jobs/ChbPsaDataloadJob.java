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

import nl.chb.psa.Quay;
import nl.gertjanidema.netex.chb_dataload.ChbPsaReader;
import nl.gertjanidema.netex.chb_dataload.dto.DlChbPsa;
import nl.gertjanidema.netex.chb_dataload.mapping.ChbPsaMapper;
import nl.gertjanidema.netex.core.batch.GzipFileSystemResource;

@Configuration
@EnableBatchProcessing
public class ChbPsaDataloadJob extends AbstractChbDataloadJob {

    public ChbPsaDataloadJob() {
        super("chb.dl_chb_stop_place");
    }

    @Value("${osm_netex.path.temp}")
    private Path tempPath;

    Path getChbFile() {
        return tempPath.resolve("PassengerStopAssignmentExportCHB.xml.gz");
    }

    public static String JOB_NAME = "chbPsaImportJob";

    @Bean
    ChbPsaReader psaReader() {
        return new ChbPsaReader(quayReader(), psaMapper());
    }
    
    @SuppressWarnings("static-method")
    @Bean
    ChbPsaMapper psaMapper() {
        return new ChbPsaMapper();
    }
    
    @Bean
    @StepScope
    StaxEventItemReader<Quay> quayReader() {
        return new StaxEventItemReaderBuilder<Quay>()
            .name("chbPsaQuayReader")
            .resource(new GzipFileSystemResource(getChbFile()))
            .addFragmentRootElements("quay")
            .unmarshaller(stopplaceMarshaller())
            .build();
    }

    @SuppressWarnings("static-method")
    @Bean
    Jaxb2Marshaller stopplaceMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Quay.class);
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
    Step importStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("readStep", jobRepository)
            .<DlChbPsa, DlChbPsa>chunk(1000, transactionManager)
            .reader(psaReader())  // null path just for type resolution
            .writer(writer())
            .build();
    }
}