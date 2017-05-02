package edu.hawaii.kscmfeedprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Config {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<String, String> mappingFromPartitionKeyToInstCode = new HashMap<>();

    private Map<String, String> mappingFromInstCodeToHostPrefix = new HashMap<>();

    public String getInstCodeFromPartitionKey(String partitionKey) {
        String instCode = mappingFromPartitionKeyToInstCode.get(partitionKey);
        //assert instCode != null : partitionKey;
        return instCode;
    }

    public String getHostPrefixFromInstCode(String instCode) {
        String hostPrefix = mappingFromInstCodeToHostPrefix.get(instCode);
        //assert hostPrefix != null : instCode;
        return hostPrefix;
    }

    @Autowired
    private Environment env;

    @Autowired
    private ApplicationContext applicationContext;

    ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    private List<String> instCodes;

    @Value("${app.profile}")
    private String profile;

    public List<String> getInstCodes() {
        return instCodes;
    }

    private void initInstCodes() {
        logger.info("initInstCodes");
        String instCodesPropertyValue = env.getProperty("app.instCodes");
        logger.info("instCodes: {}", instCodesPropertyValue);
        instCodes = Arrays.asList(instCodesPropertyValue.split("\\s*,\\s*"));
        for (String instCode : instCodes) {
            logger.info("instCode: {}", instCode);
        }
    }

    private void initMappingFromPartitionKeyToInstCode() {
        for (String instCode : this.instCodes) {
            String hostPrefix = env.getProperty("app." + instCode + ".hostPrefix");
            String partitionKey = "stu-cm-" + hostPrefix;
            logger.info("initMappingFromPartitionKeyToInstCode: {}: {}", partitionKey, instCode);
            this.mappingFromPartitionKeyToInstCode.put(partitionKey, instCode);
        }
    }

    private void initMappingFromInstCodeToHostPrefix() {
        for (String instCode : this.instCodes) {
            String hostPrefix = env.getProperty("app." + instCode + ".hostPrefix");
            logger.info("initMappingFromInstCodeToHostPrefix: {}: {}", instCode, hostPrefix);
            this.mappingFromInstCodeToHostPrefix.put(instCode, hostPrefix);
        }
    }


    public void init() {
        initInstCodes();
        initMappingFromPartitionKeyToInstCode();
        initMappingFromInstCodeToHostPrefix();
    }

}
