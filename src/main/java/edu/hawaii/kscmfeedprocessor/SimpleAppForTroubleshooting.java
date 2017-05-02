package edu.hawaii.kscmfeedprocessor;

import edu.hawaii.kscmfeedprocessor.banner.*;
import edu.hawaii.kscmfeedprocessor.banner.vpdi.MultiUseContext;
import edu.hawaii.kscmfeedprocessor.banner.vpdi.VpdiContextManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.Closeable;
import java.util.List;

/**
 * Useful for troubleshooting service initialization (without starting up the kinesis client).
 * This class is run from KscmFeedProcessorApplication if "test" is included among the Spring Boot
 * active profiles. E.g., -Dspring.profiles.active=test,stg activates the "test" and "stg" profiles.
 */
@Repository
public class SimpleAppForTroubleshooting {
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
    ScbcrkyDao scbcrkyDao;

    @Autowired
    ScbcrseDao scbcrseDao;

    @Autowired
    ScbdescDao scbdescDao;

    @Resource(name = "bannerDataSource")
    private DataSource dataSource;

    void run() {
        logger.info("SimpleAppForTroubleshooting");
        // Put code for troubleshooting initialization here.
    }

}
