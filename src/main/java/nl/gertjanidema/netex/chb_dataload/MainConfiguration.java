package nl.gertjanidema.netex.chb_dataload;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import nl.gertjanidema.netex.chb_dataload.ndov.NdovService;


@Configuration
//@EnableJpaRepositories(basePackages = "nl.gertjanidema.netex.dataload.dto")
@EntityScan("nl.gertjanidema.netex.chb_dataload.dto")
public class MainConfiguration {
   @SuppressWarnings("static-method")
    @Bean
    NdovService ndovService() {
        return new NdovService();
    }
}
