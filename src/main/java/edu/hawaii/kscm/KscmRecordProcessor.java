package edu.hawaii.kscm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 */
public class KscmRecordProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void processRecord(String sequenceNumber, String partitionKey, String data) {
        logger.info("SeqNo: {}, PartKey: {}, Data: {}", sequenceNumber,  partitionKey, data);
    }
}
