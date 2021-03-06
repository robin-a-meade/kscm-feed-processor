package edu.hawaii.kscmfeedprocessor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hawaii.kscmfeedprocessor.kscm.MessageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class KscmRecordProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Config config;

    @Autowired
    private KscmToBannerService kscmToBannerService;

    @Autowired
    private KscmService kscmService;


    public void processRecord(String sequenceNumber, String partitionKey, String messageJsonString) throws Exception {
        logger.debug("processRecord {}: {}", partitionKey, messageJsonString);
        if (messageJsonString == null || messageJsonString.length() == 0) {
            logger.debug("messageJsonString was null or zero length!");
            return;
        }

        String instCode = this.config.getInstCodeFromPartitionKey(partitionKey);

        // Return if this isn't one of the institutions that is configured to be processed
        if (!this.config.getInstCodes().contains(instCode)) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        MessageData messageData = objectMapper.readValue(messageJsonString, MessageData.class);
        if (messageData.resource.equals("courses") && messageData.delta.operation.equals("update") && messageData.delta.changes[0].vals.get
                ("updated") != null) {
            long millisecondsSinceEpoch = messageData.delta.changes[0].vals.get("updated").asLong();
            Instant instant = Instant.ofEpochMilli(millisecondsSinceEpoch);
            ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
            logger.info("course update: {} {} {}", instCode, zdt, messageJsonString);
        }

        if (userCheckedBannerIntegrationFlag(messageData)) {

            logger.info("Kinesis Event: KscmUser checked bannerIntegrationFlag: Pushing to Banner: {} {}", instCode, messageData.delta.documentId);

            RunData runData = new RunData();
            runData.setInstCode(instCode);
            runData.setHostPrefix(this.config.getHostPrefixFromInstCode(instCode));

            runData.setKscmCourseVersionId(messageData.delta.documentId);
            runData.setKinesisSequenceNumber(sequenceNumber);
            runData.setKinesisPartitionKey(sequenceNumber);
            runData.setKinesisMessageJsonString(messageJsonString);
            runData.setKinesisMessageData(messageData);

            kscmToBannerService.handleRequest(runData);

            kscmService.postBack(runData);

        }
    }

    private boolean userCheckedBannerIntegrationFlag(MessageData messageData) {
        return messageData.resource.equals("courses") && messageData.delta.operation.equals("update") && getBannerIntegrationFlag(messageData);
    }

    private boolean getBannerIntegrationFlag(MessageData messageData) {
        JsonNode bannerIntegrationFlagJsonNode = messageData.delta.changes[0].vals.get("bannerIntegrationFlag");
        if (bannerIntegrationFlagJsonNode != null && bannerIntegrationFlagJsonNode.asBoolean(false)) {
            return true;
        }
        /*
        // Account for misspelled GKey
        JsonNode bannerintegrationFlagJsonNode = messageData.delta.changes[0].vals.get("bannerintegrationFlag");
        if (bannerintegrationFlagJsonNode != null && bannerintegrationFlagJsonNode.asBoolean(false)) {
            return true;
        }
        */
        return false;
    }

}
