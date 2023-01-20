package ro.sopa.statistifier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import ro.sopa.statistifier.job.JobRegistry;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    @Scope(value="singleton")
    public JobRegistry jobRegistry() {
        return new JobRegistry();
    }

}