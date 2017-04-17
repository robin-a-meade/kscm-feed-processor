package edu.hawaii.kscm;

import edu.hawaii.kscm.domain.BannerIntegrationResults;
import edu.hawaii.kscm.domain.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;


@Component
public class KscmService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Config config;

    @Autowired
    private Environment env;

    @Value("${app.profile}")
    private String profile;

    private Map<String, WebTarget> webTargets = new HashMap<>();
    private Map<String, String> apiKeys = new HashMap<>();

    private Map<String, KscmOptions> kscmOptionsByInstCode = new HashMap<>();

    public KscmOptions getKscmOptionsFor(String instCode) {
        KscmOptions kscmOptions = kscmOptionsByInstCode.get(instCode);
        assert kscmOptions != null : instCode;
        return kscmOptions;
    }

    private void initApiKeys() {
        logger.info("initApiKeys");
        for (String instCode : this.config.getInstCodes()) {
            logger.info("Initializing apiKey for " + instCode);
            String apiKey = env.getProperty("app." + instCode + ".apikey");
            apiKeys.put(instCode, apiKey);
        }
    }

    private void initWebTargets() {
        logger.info("initWebTargets");
        if (this.profile == null) {
            throw new IllegalArgumentException("app.profile property was not set");
        }
        logger.info("app.profile: {}", profile);
        Client client = ClientBuilder.newClient();
        for (String instCode : this.config.getInstCodes()) {
            logger.info("Initializing WebTarget for " + instCode);
            String hostPrefix = env.getProperty("app." + instCode + ".hostPrefix");
            String hostSuffix = "";
            switch (profile) {
                case "prd":
                    hostSuffix = "";
                    break;
                case "default":
                    throw new IllegalArgumentException("app.profile should be one of: stg, sbx, prd, etc.");
                default:
                    hostSuffix = "-" + profile;
                    break;
            }
            String host = hostPrefix + hostSuffix;
            WebTarget webTarget = client.target("https://" + host + ".kuali.co/api");
            this.webTargets.put(instCode, webTarget);
        }
    }


    private void validateApiKeys() {
        logger.info("validateApiKeys");
        for (String instCode : this.config.getInstCodes()) {
            logger.info("validating apiKey for {}", instCode);

            Invocation.Builder ib = getInvocationBuilder(instCode, "/cm/courses/schema/number");
            Invocation get = ib.buildGet();
            Response response = get.invoke();

            if (response.getStatus() == 200) {
                logger.info("apiKey for {} ... {}: {}", instCode, "GOOD", response.getStatus());
            } else {
                logger.error("apiKey for {} ... {}: {}", instCode, "BAD", response.getStatus());
                throw new RuntimeException("Bad apiKey for " + instCode);
            }
        }
    }

    private void initKscmOptions() {
        logger.info("initKscmOptions");
        for (String instCode : this.config.getInstCodes()) {
            KscmOptions kscmOptions = new KscmOptions(this, instCode);
            kscmOptionsByInstCode.put(instCode, kscmOptions);
        }
    }

    public Invocation.Builder getInvocationBuilder(String instCode, String path) {
        WebTarget webTarget = webTargets.get(instCode);
        webTarget = webTarget.path(path);
        String apiKey = apiKeys.get(instCode);
        Invocation.Builder ib = webTarget.request(MediaType.APPLICATION_JSON);
        ib.header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        return ib;
    }

    public void clearFlag(String instCode, String id, String message) {
        logger.info("clearFlag: {} {} '{}'", instCode, id, message);
        Invocation.Builder ib = getInvocationBuilder("MAN", "/cm/courses/" + id);
        BannerIntegrationResults bannerIntegrationResults = new BannerIntegrationResults(message);
        Invocation invocation = ib.buildPut(Entity.json(bannerIntegrationResults));
        Course c = invocation.invoke(Course.class);
    }


    public Course retrieveCourse(String instCode, String id) {
        logger.info("retrieveCourse: {} {}", instCode, id);

        Invocation.Builder ib = getInvocationBuilder(instCode, "/cm/courses/" + id);
        Invocation invocation = ib.buildGet();
        Course c = invocation.invoke(Course.class);
        c.setInstCode(instCode);
        logger.info("subjectCode: {}", c.getSubjectCode());
        logger.info("number: {}", c.getNumber());
        logger.info("id: {}", c.getId());
        logger.info("bannerIntegrationFlag: {}", c.getBannerIntegrationFlag());
        logger.info("bannerIntegrationResults: {}", c.getBannerIntegrationResults());
        logger.info("startTerm.year: " + "{}", c.getStartTerm().getYear());
        logger.info("startTerm.termOptionId: {}", c.getStartTerm().getTermOptionId());
        Map<String, Object> bdeScheduleTypes = c.getBdeScheduleTypes().getScheduleTypes();
        for (Map.Entry<String, Object> entry : bdeScheduleTypes.entrySet()) {
            Boolean value = (Boolean) entry.getValue();
            if (value) {
                logger.info("bdeScheduleType: {}", entry.getKey());
            }
        }
        Map<String, Object> bdeCourseLevels = c.getBdeCourseLevels().getCourseLevels();
        for (Map.Entry<String, Object> entry : bdeCourseLevels.entrySet()) {
            Boolean value = (Boolean) entry.getValue();
            if (value) {
                logger.info("bdeCourseLevel: {}", entry.getKey());
            }
        }
        Map<String, Object> bdeGradingOptions = c.getBdeGradingOptions().getGradingOptions();
        for (Map.Entry<String, Object> entry : bdeGradingOptions.entrySet()) {
            Boolean value = (Boolean) entry.getValue();
            if (value) {
                logger.info("bdeGradingOption: {}", entry.getKey());
            }
        }
        Map<String, Object> bdeDegreeAttributes = c.getBdeDegreeAttributes().getDegreeAttributes();
        for (Map.Entry<String, Object> entry : bdeDegreeAttributes.entrySet()) {
            Boolean value = (Boolean) entry.getValue();
            if (value) {
                logger.info("bdeDegreeAttributes: {}", entry.getKey());
            }
        }
        logger.info("bdeGradingOptionDef: {}", c.getBdeGradingOptionDef());
        return c;
    }


    public void init() {
        initApiKeys();
        initWebTargets();
        validateApiKeys();
        initKscmOptions();
        //retrieveCourse("MAN", "ACC999_201610");
        //clearFlag("MAN", "ACC999_201610", "MESSAGE1");
        //retrieveCourse("MAN", "ACC999_201610");
    }

}
