package edu.hawaii.kscm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hawaii.kscm.domain.MessageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

@Component
public class KscmRecordProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Config config;

    @Autowired
    private BannerService bannerService;

    public void processRecord(String sequenceNumber, String partitionKey, String dataJson) throws Exception {
        logger.debug("processRecord {}: {}", partitionKey, dataJson);

        // TODO: Springify KscmRecordProcessor and AmazonKinesisApplicationRecordProcessorFactory
        String instCode = this.config.getInstCodeFromPartitionKey(partitionKey);

        ObjectMapper objectMapper = new ObjectMapper();
        MessageData messageData = objectMapper.readValue(dataJson, MessageData.class);

        long updated = 0;
        boolean pushToBanner = false;
        //logger.info("{}: {}", instCode, dataJson);
        if (messageData.resource.equals("courses")) {
            JsonNode bannerIntegrationFlag = messageData.delta.changes[0].vals.get("bannerIntegrationFlag");
            // Need to account for misspelled GKeys!
            JsonNode mispelling = messageData.delta.changes[0].vals.get("bannerintegrationFlag");
            if (bannerIntegrationFlag != null && bannerIntegrationFlag.asBoolean(false)) {
                pushToBanner = true;
            } else if (mispelling != null && mispelling.asBoolean(false)) {
                pushToBanner = true;
            }
            if (messageData.delta.changes[0].vals.get("updated") != null) {
                updated = messageData.delta.changes[0].vals.get("updated").asLong();
            }
        }
        if (pushToBanner) {
            String updatedIsoFmt = "-";
            if (updated != 0) {
                Instant instant = Instant.ofEpochMilli(updated);
                ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
                updatedIsoFmt = ISO_OFFSET_DATE_TIME.format(zdt);
            }
            logger.info("{} PUSHTOBANNER {} DOCID: {}", instCode, updatedIsoFmt, messageData.delta.documentId);
            bannerService.push(instCode, messageData.delta.documentId);
        }
    }
}
