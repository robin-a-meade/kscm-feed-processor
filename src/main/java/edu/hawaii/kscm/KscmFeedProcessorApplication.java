package edu.hawaii.kscm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Arrays;
import java.util.List;

@EnableTransactionManagement
@SpringBootApplication
public class KscmFeedProcessorApplication implements CommandLineRunner{
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
    private BannerService bannerService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("run");
        config.init();
        kscmService.init();
        bannerService.init();
        AmazonKinesisApplicationSample amazonKinesisApplicationSample = applicationContext.getBean
                ("amazonKinesisApplicationSample",
                        AmazonKinesisApplicationSample.class);
        amazonKinesisApplicationSample.run(args);
    }

    public static void main(String[] args) {
        SpringApplication.run(KscmFeedProcessorApplication.class, args);
	}
}
