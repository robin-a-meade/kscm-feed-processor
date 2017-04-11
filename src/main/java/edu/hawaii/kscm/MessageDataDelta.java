package edu.hawaii.kscm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class MessageDataDelta {
    public String operation;
    public String documentId;
    public MessageDataDeltaChange[] changes;
}
