package edu.hawaii.kscm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 */
public class KscmRecordProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void processRecord(String sequenceNumber, String partitionKey, String dataJson) throws Exception {
        //logger.info("SeqNo: {}, PartKey: {}, MessageData: {}", sequenceNumber, partitionKey, dataJson);
        ObjectMapper objectMapper = new ObjectMapper();
        MessageData messageData = objectMapper.readValue(dataJson, MessageData.class);
        boolean commitToBanner = false;
        try {
            commitToBanner = messageData.resource.equals("courses") && messageData.delta.changes[0].vals.get
                    ("bannerIntegrationFlag").asBoolean();
        } catch (NullPointerException e) {
            // suppress
        }
        if (commitToBanner) {
            logger.info("PUSH Course TO BANNER: partitionKey: {}, documentId: {}", partitionKey, messageData.delta
                    .documentId);
        }
    }
}
