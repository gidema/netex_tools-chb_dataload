package nl.gertjanidema.netex.chb_dataload.cli;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = { "nl.gertjanidema.netex.chb_dataload" },
excludeFilters = { @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "nl.gertjanidema.netex.chb_dataload.jobs.*")})
public class ChbDataload implements CommandLineRunner, ApplicationContextAware {

    private static Logger LOG = LoggerFactory
      .getLogger(ChbDataload.class);

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        LOG.info("STARTING THE APPLICATION");
        new SpringApplicationBuilder(ChbDataload.class)
            .web(WebApplicationType.NONE)
            .run(args);
        LOG.info("APPLICATION FINISHED");
    }

    private ApplicationContext applicationContext;
    

    @Override
    public void run(String... args) {
        LOG.info("EXECUTING : command line runner");
        var jobRegistry = applicationContext.getBean(JobRegistry.class);
        var jobLauncher = applicationContext.getBean(JobLauncher.class);
        Job job;
        try {
            var parameters = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
            job = jobRegistry.getJob("loadChbFilesJob");
            jobLauncher.run(job, parameters);
            job = jobRegistry.getJob("chbQuayImportJob");
            jobLauncher.run(job, parameters);
            job = jobRegistry.getJob("chbStopPlaceImportJob");
            jobLauncher.run(job, parameters);
            job = jobRegistry.getJob("chbPsaImportJob");
            jobLauncher.run(job, parameters);
            job = jobRegistry.getJob("chbSupQuayImportJob");
            jobLauncher.run(job, parameters);
            job = jobRegistry.getJob("chbSupStopPlaceImportJob");
            jobLauncher.run(job, parameters);
            job = jobRegistry.getJob("chbSupPsaImportJob");
            jobLauncher.run(job, parameters);
            job = jobRegistry.getJob("chbStagingJob");
            jobLauncher.run(job, parameters);
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}