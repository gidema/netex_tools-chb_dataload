package nl.gertjanidema.netex.chb_dataload.jobs;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class ChbStagingJob {

    private static String update_chb_quay = """
TRUNCATE TABLE chb.chb_quay;
INSERT INTO chb.chb_quay (
SELECT bearing,
    onlygetout,
    mutationdate,
    validfrom,
    id,
    level,
    location,
    quay_status,
    quay_type,
    quay_code,
    stop_place_id,
    stop_place_long_name,
    stop_place_name, street,
    town,
    ST_TRANSFORM(ST_SETSRID(ST_POINT(x_coordinate, y_coordinate), 28992), 4326) AS wsg_location,
    ST_SETSRID(ST_POINT(x_coordinate, y_coordinate), 28992) AS rd_location,
    quay_name,
    stop_side_code,
    transport_modes
    FROM chb.dl_chb_quay
);
""";

    private static String update_chb_stop_place = """
TRUNCATE TABLE chb.chb_stop_place;
INSERT INTO chb.chb_stop_place (
SELECT mutation_date,
    uic_code,
    valid_from,
    description,
    id,
    internal_name,
    place_code,
    public_name,
    public_name_long,
    public_name_medium,
    stop_place_code,
    stop_place_indication,
    stop_place_owner,
    stop_place_status,
    stop_place_type,
    street,
    town
    FROM chb.dl_chb_stop_place
    );
""";

    private static String update_chb_psa = """
TRUNCATE TABLE chb.chb_psa;
INSERT INTO chb.chb_psa (
SELECT user_stop_owner_code,
    user_stop_code,
    quay_code,
    quay_ref,
    stopplace_code,
    stopplace_ref,
    user_stop_valid_from,
    user_stop_valid_thru
    FROM chb.dl_chb_psa
    );
""";

    private static String update_chb_stop_area = """
TRUNCATE TABLE chb.chb_stop_area;
INSERT INTO chb.chb_stop_area (
    SELECT sp.id AS stop_place_id, sp.stop_place_code AS area_code, sp.public_name AS name,
        ST_Centroid(ST_Collect(q.wgs_location)) AS wgs_centriod, ST_Centroid(ST_Collect(rd_location)) AS rd_centroid,
        count(*) AS stop_count
    FROM chb.chb_quay q
    JOIN chb.chb_stop_place sp ON q.stop_place_id = sp.id
    GROUP BY sp.id, sp.stop_place_code, sp.public_name
);
""";

    private final EntityManagerFactory entityManagerFactory;

    @Inject
    private ApplicationContext applicationContext;

    @Bean
    EntityManager entityManager() {
        return entityManagerFactory.createEntityManager();
    }
    
    /**
     * Defines the main batch job for importing.
     *
     * @param jobRepository the repository for storing job metadata.
     * @param step1 the step associated with this job.
     * @return a configured Job for importing contacts.
     */
    @Bean
    Job netexEtlUpdate(JobRepository jobRepository)  {

        return new JobBuilder("chbStagingJob", jobRepository)
            .start(sqlUpdateStep("Update quays", update_chb_quay))
            .next(sqlUpdateStep("Update stop places", update_chb_stop_place))
            .next(sqlUpdateStep("Update passenger stop assignments", update_chb_psa))
            .next(sqlUpdateStep("Update stop areas", update_chb_stop_area))
            .build();
    }

    /**
     * Defines an SQL update step to update a.
     *
     * @param stepName The name of the step
     * @param sql The sql code to execute.
     * @return a configured Step.
     */
    Step sqlUpdateStep(String stepName, String sql) {
        var jobRepository = applicationContext.getBean(JobRepository.class);
        var transactionManager = applicationContext.getBean(PlatformTransactionManager.class);
        var transactionTemplate = applicationContext.getBean(TransactionTemplate.class);
        Tasklet tasklet = sqlTasklet(transactionTemplate, sql);
        return new StepBuilder(stepName, jobRepository)
        .tasklet(tasklet, transactionManager)
        .allowStartIfComplete(true)
        .build();
    }

    @SuppressWarnings("static-method")
    @Bean 
    TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);       
    }
    
    private Tasklet sqlTasklet(TransactionTemplate transactionTemplate, String query) {
        return new Tasklet() {

            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                try (
                        var entityManager = entityManagerFactory.createEntityManager();
                )
                {
                    transactionTemplate.execute(transactionStatus -> {
                        entityManager.joinTransaction();
                        entityManager
                          .createNativeQuery(query)
                          .executeUpdate();
                        transactionStatus.flush();
                        return null;
                    });
                }
                finally {
                    //
                }
                return null;
            }
            
        };
    }
}