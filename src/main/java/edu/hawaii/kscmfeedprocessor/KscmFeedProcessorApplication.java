package edu.hawaii.kscmfeedprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@SpringBootApplication
public class KscmFeedProcessorApplication implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment env;

    @Autowired
    private Config config;

    @Autowired
    private KscmService kscmService;

    @Autowired
    private KscmToBannerService kscmToBannerService;

    @Autowired
    private BannerUpdaterService bannerUpdaterService;

    @Autowired
    private SimpleAppForTroubleshooting simpleAppForTroubleshooting;

    @Override
    public void run(String... args) throws Exception {
        logger.info("ActiveProfiles: {}", Arrays.toString(this.env.getActiveProfiles()));
        config.init();
        kscmService.init();
        bannerUpdaterService.init();

        String[] arrActiveProfiles = this.env.getActiveProfiles();
        if (Arrays.stream(this.env.getActiveProfiles()).anyMatch(env -> (env.equalsIgnoreCase("test")))) {
            // When running tests, we don't want to start the kinesis client.
            // We accomplish this goal using the "test" profile technique seen here:
            // - http://stackoverflow.com/a/35098172
            // - https://github.com/spring-projects/spring-boot/issues/830#issuecomment-42682793
            // This is also useful for troubleshooting service initialization, which is what SimpleAppForTroubleshooting does:
            simpleAppForTroubleshooting.run();
        } else {
            // Run the real application that starts the kinesis client.
            AmazonKinesisApplicationSample app = applicationContext.getBean("amazonKinesisApplicationSample", AmazonKinesisApplicationSample.class);
            app.run(args);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(KscmFeedProcessorApplication.class, args);
	}
}
