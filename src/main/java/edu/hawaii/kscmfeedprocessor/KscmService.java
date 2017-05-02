package edu.hawaii.kscmfeedprocessor;

import edu.hawaii.kscmfeedprocessor.kscm.BannerIntegrationResults;
import edu.hawaii.kscmfeedprocessor.kscm.KscmCourseVersion;
import edu.hawaii.kscmfeedprocessor.kscm.KscmUser;
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
import static java.lang.String.format;


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

    public WebTarget getWebTarget(String instCode) {
        return this.webTargets.get(instCode);
    }


    private void validateApiKeys() {
        logger.info("validateApiKeys");
        for (String instCode : this.config.getInstCodes()) {
            logger.info("validating apiKey for {}", instCode);

            // Build invocation
            WebTarget webTarget = webTargets.get(instCode);
            webTarget = webTarget.path("/cm/courses/schema/number");
            Invocation.Builder ib = webTarget.request(MediaType.APPLICATION_JSON);
            addApiKey(ib, instCode);
            Invocation get = ib.buildGet();

            // Invoke it
            Response response = get.invoke();

            if (response.getStatus() == 200) {
                logger.info("apiKey for {} is ... {} ({})", instCode, "GOOD", response.getStatus());
            } else {
                logger.error("apiKey for {} is ... {} ({})", instCode, "BAD", response.getStatus());
                throw new RuntimeException("Bad apikey for " + instCode);
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

    public void addApiKey(Invocation.Builder ib, String instCode) {
        String apiKey = apiKeys.get(instCode);
        ib.header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
    }

    public void postBack(RunData run) {
        WebTarget webTarget = null;
        try {
            logger.info("postBack: \n\n{}\n\n", run.toString());

            // The object to put
            BannerIntegrationResults bannerIntegrationResults = new BannerIntegrationResults(run.toString());

            // Build invocation
            webTarget = webTargets.get(run.getInstCode());
            webTarget = webTarget.path("/cm/courses/" + run.getKscmCourseVersionId());
            Invocation.Builder ib = webTarget.request(MediaType.APPLICATION_JSON);
            addApiKey(ib, run.getInstCode());
            Invocation invocation = ib.buildPut(Entity.json(bannerIntegrationResults));

            // Invoke it
            KscmCourseVersion kcv = invocation.invoke(KscmCourseVersion.class);

        } catch (Throwable t) {
            // Just log it as an error.
            // We don't wont the KscmRecordProcessor to re-attempt pushing the course into Banner just because the post-back failed.
            String msg = String.format("Exception during post-back to {}. The post-back message was: {}", getWebTargetUri(webTarget), run.toString());
            logger.error(msg, t);
        }
    }

    public KscmUser retrieveKscmUser(RunData runData) {
        String instCode = runData.getInstCode();
        String updatedBy = runData.getKcv().getUpdatedBy();

        logger.info("retrieveKscmUser: {} {}", instCode, updatedBy);

        KscmUser kscmUser = null;
        WebTarget webTarget = null;

        // Build invocation

        webTarget = webTargets.get(instCode);
        webTarget = webTarget.path("/v1/users/" + updatedBy);

        try {

            Invocation.Builder ib = webTarget.request(MediaType.APPLICATION_JSON);
            addApiKey(ib, instCode);
            Invocation invocation = ib.buildGet();

            // Invoke it
            kscmUser = invocation.invoke(KscmUser.class);

            logger.trace("kscmUser: {}", kscmUser.toJson());

        } catch (Exception e) {
            // Add message to RunData and rethrow the exception
            String msg = format("RetrieveKscmUser: error: Exception occurred while retrieving KSCM course version at URL: {}",
                    getWebTargetUri(webTarget));
            runData.addMessage(msg, e);
            throw e;
        }
        return kscmUser;
    }


    public KscmCourseVersion retrieveKscmCourseVersion(RunData runData) {
        String instCode = runData.getInstCode();
        String id = runData.getKscmCourseVersionId();

        logger.info("retrieveKscmCourseVersion: {} {}", instCode, id);

        KscmCourseVersion kcv = null;
        WebTarget webTarget = null;

        // Build invocation

            webTarget = webTargets.get(instCode);
            webTarget = webTarget.path("/cm/courses/" + id);

        try {

            Invocation.Builder ib = webTarget.request(MediaType.APPLICATION_JSON);
            addApiKey(ib, instCode);
            Invocation invocation = ib.buildGet();

            // Invoke it
            kcv = invocation.invoke(KscmCourseVersion.class);
            kcv.setInstCode(instCode); // hack to deal with misspelled GKeys.

            logger.trace("kcv: {}", kcv.toJson());

        } catch (Exception e) {
            // Add message to RunData and rethrow the exception
            String msg = String.format("retrieveKscmCourseVersion: Exception occurred while retrieving KSCM course version at URL: {}",
                    getWebTargetUri(webTarget));
            runData.addMessage(msg);
            logger.error(msg);
            throw e;
        }
        return kcv;
    }

    /**
     *  A static util method to get the URI of a WebTarget. Returns "null" if webTarget is null.
     */
    public static String getWebTargetUri(WebTarget webTarget) {
        return webTarget != null ? webTarget.getUri().toString() : "null";
    }


    public void init() {
        initApiKeys();
        initWebTargets();
        validateApiKeys();
        initKscmOptions();
        //retrieveKscmCourseVersion("MAN", "ACC999_201610");
        //postBack("WOA", "ACC999_201710", "MESSAGE2");
        //retrieveKscmCourseVersion("MAN", "ACC999_201610");
    }

}
