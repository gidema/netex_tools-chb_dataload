package nl.gertjanidema.netex.chb_dataload;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.ApplicationContextFactory;
import org.springframework.batch.core.configuration.support.GenericApplicationContextFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import nl.gertjanidema.netex.chb_dataload.jobs.ChbPsaDataloadJob;
import nl.gertjanidema.netex.chb_dataload.jobs.ChbQuayDataloadJob;
import nl.gertjanidema.netex.chb_dataload.jobs.ChbStopPlaceDataloadJob;
import nl.gertjanidema.netex.chb_dataload.jobs.ChbSupPsaDataloadJob;
import nl.gertjanidema.netex.chb_dataload.jobs.ChbSupQuayDataloadJob;
import nl.gertjanidema.netex.chb_dataload.jobs.ChbSupStopPlaceDataloadJob;
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
    static ApplicationContextFactory importChbSupQuayConfig() {
        return new GenericApplicationContextFactory(ChbSupQuayDataloadJob.class);
    }
    
    @Bean
    static ApplicationContextFactory importChbStopPlaceConfig() {
        return new GenericApplicationContextFactory(ChbStopPlaceDataloadJob.class);
    }
    
    @Bean
    static ApplicationContextFactory importChbSupStopPlaceConfig() {
        return new GenericApplicationContextFactory(ChbSupStopPlaceDataloadJob.class);
    }

    @Bean
    static ApplicationContextFactory importChbPsaConfig() {
        return new GenericApplicationContextFactory(ChbPsaDataloadJob.class);
    }
    
    @Bean
    static ApplicationContextFactory importChbSupPsaConfig() {
        return new GenericApplicationContextFactory(ChbSupPsaDataloadJob.class);
    }
    
    @Bean
    static ApplicationContextFactory batchChbEtlUpdateConfig() {
        return new GenericApplicationContextFactory(ChbStagingJob.class);
    }

}
