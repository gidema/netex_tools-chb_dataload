package nl.gertjanidema.netex.chb_dataload;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.ApplicationContextFactory;
import org.springframework.batch.core.configuration.support.GenericApplicationContextFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import nl.gertjanidema.netex.chb_dataload.jobs.ChbPsaDataloadJob;
import nl.gertjanidema.netex.chb_dataload.jobs.ChbQuayDataloadJob;
import nl.gertjanidema.netex.chb_dataload.jobs.ChbStopplaceDataloadJob;
import nl.gertjanidema.netex.chb_dataload.jobs.LoadChbFilesJob;
import nl.gertjanidema.netex.chb_dataload.jobs.ChbStagingJob;

@Configuration
@EnableBatchProcessing(modular = true)
public class ChbBatchConfig {
    @Bean
    static ApplicationContextFactory loadChbFilesJob() {
        return new GenericApplicationContextFactory(LoadChbFilesJob.class);
    }

    @Bean
    static ApplicationContextFactory importChbQuaysConfig() {
        return new GenericApplicationContextFactory(ChbQuayDataloadJob.class);
    }

    @Bean
    static ApplicationContextFactory importChbStopPlaceConfig() {
        return new GenericApplicationContextFactory(ChbStopplaceDataloadJob.class);
    }

    @Bean
    static ApplicationContextFactory importChbPsaConfig() {
        return new GenericApplicationContextFactory(ChbPsaDataloadJob.class);
    }
    
    @Bean
    static ApplicationContextFactory batchChbEtlUpdateConfig() {
        return new GenericApplicationContextFactory(ChbStagingJob.class);
    }

}
