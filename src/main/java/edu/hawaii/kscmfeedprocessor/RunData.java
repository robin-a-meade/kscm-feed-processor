package edu.hawaii.kscmfeedprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Throwables;
import edu.hawaii.kscmfeedprocessor.banner.Scbcrse;
import edu.hawaii.kscmfeedprocessor.kscm.KscmCourseVersion;
import edu.hawaii.kscmfeedprocessor.kscm.KscmUser;
import edu.hawaii.kscmfeedprocessor.kscm.MessageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 */
public class RunData {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Default is SUCCESS. Any processing error will set this to FAILURE.
    private Status status = Status.SUCCESS;

    private ZonedDateTime zonedDateTime = ZonedDateTime.now();

    private Date activityDate = Date.from(zonedDateTime.toInstant());

    private LinkedList<String> messageList = new LinkedList<>();

    private String userId;

    private KscmUser kscmUser;

    private String dataOrigin = "KSCM";

    private String instCode;

    private String subjCode;

    private String crseNumb;

    private String effTerm; // The banner term corresponding to the startTerm of the KSCM course

    private String hostPrefix;

    private String kscmCourseVersionId;

    private String kinesisSequenceNumber;

    private String kinesisPartitionKey;

    private String kinesisMessageJsonString;

    private MessageData kinesisMessageData;

    private KscmCourseVersion kcv;

    private Scbcrse convertedScbcrse;


    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Status and time

        sb.append(status);
        sb.append(' ');
        sb.append(zonedDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)));
        sb.append("\n");

        // Messages in reverse chronological order

        Iterator it = messageList.descendingIterator();
        while (it.hasNext()) {
            sb.append(it.next());
            sb.append("\n");
        }

        // Print additional details if it was a FAILURE

        if (status == Status.FAILURE) {
            sb.append("\n\n");
            sb.append(String.format("kinesisPartitionKey: '%s'\n", kinesisPartitionKey));
            sb.append(String.format("instCode: '%s'\n", instCode));
            sb.append(String.format("kscmCourseVersionId: '%s'\n", kscmCourseVersionId));
            sb.append(String.format("kinesisSequenceNumber: '%s'\n", kinesisSequenceNumber));
            sb.append(String.format("kinesisMessageJsonString: %s\n", formatJson(kinesisMessageJsonString)));
            sb.append(String.format("userId: '%s'\n", userId));
        }

        return sb.toString();
    }

    private static String formatJson(String jsonString) {
        String result = null;
        try {
            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            Object jsonObject = mapper.readValue(jsonString, Object.class);
            result = mapper.writeValueAsString(jsonObject);
        } catch (Exception e) {
            result = "Exception parsing: " + jsonString;
        }
        return result;
    }

    public void addMessage(String msg) {
        messageList.add(msg);
        logger.info(msg);
    }

    public void addMessage(String msg, Throwable t) {
        addMessage(Throwables.getStackTraceAsString(t));
        messageList.add(msg);
        logger.error(msg, t);
    }

    // Generated Getters and Setters

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }

    public Date getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    public String getUserId() {
        return userId;
    }


    public KscmUser getKscmUser() {
        return kscmUser;
    }

    public void setKscmUser(KscmUser kscmUser) {
        this.kscmUser = kscmUser;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDataOrigin() {
        return dataOrigin;
    }

    public void setDataOrigin(String dataOrigin) {
        this.dataOrigin = dataOrigin;
    }

    public String getInstCode() {
        return instCode;
    }

    public void setInstCode(String instCode) {
        this.instCode = instCode;
    }

    public String getSubjCode() {
        return subjCode;
    }

    public void setSubjCode(String subjCode) {
        this.subjCode = subjCode;
    }

    public String getCrseNumb() {
        return crseNumb;
    }

    public void setCrseNumb(String crseNumb) {
        this.crseNumb = crseNumb;
    }

    public String getEffTerm() {
        return effTerm;
    }

    public void setEffTerm(String effTerm) {
        this.effTerm = effTerm;
    }

    public String getHostPrefix() {
        return hostPrefix;
    }

    public void setHostPrefix(String hostPrefix) {
        this.hostPrefix = hostPrefix;
    }

    public String getKscmCourseVersionId() {
        return kscmCourseVersionId;
    }

    public void setKscmCourseVersionId(String kscmCourseVersionId) {
        this.kscmCourseVersionId = kscmCourseVersionId;
    }

    public String getKinesisSequenceNumber() {
        return kinesisSequenceNumber;
    }

    public void setKinesisSequenceNumber(String kinesisSequenceNumber) {
        this.kinesisSequenceNumber = kinesisSequenceNumber;
    }

    public String getKinesisPartitionKey() {
        return kinesisPartitionKey;
    }

    public void setKinesisPartitionKey(String kinesisPartitionKey) {
        this.kinesisPartitionKey = kinesisPartitionKey;
    }

    public String getKinesisMessageJsonString() {
        return kinesisMessageJsonString;
    }

    public void setKinesisMessageJsonString(String kinesisMessageJsonString) {
        this.kinesisMessageJsonString = kinesisMessageJsonString;
    }

    public MessageData getKinesisMessageData() {
        return kinesisMessageData;
    }

    public void setKinesisMessageData(MessageData kinesisMessageData) {
        this.kinesisMessageData = kinesisMessageData;
    }

    public KscmCourseVersion getKcv() {
        return kcv;
    }

    public void setKcv(KscmCourseVersion kcv) {
        this.kcv = kcv;
    }

    public Scbcrse getConvertedScbcrse() { return convertedScbcrse; }

    public void setConvertedScbcrse(Scbcrse convertedScbcrse) { this.convertedScbcrse = convertedScbcrse; }


    // Main method (for quick test)

    public static void main(String[] args) {

        RunData r = new RunData();
        r.setInstCode("WOA");
        //r.setKinesisMessageJsonString("");
        r.setKinesisPartitionKey("partition-stg-woa");
        r.setKinesisSequenceNumber("0123234234298347298347");
        r.setKscmCourseVersionId("92749849384");

        r.addMessage("Step 1 succeeded");

        r.addMessage("Step 2 failed", new RuntimeException("An exception occurred"));

        r.setStatus(Status.FAILURE);

        System.out.println(r);
    }

}

