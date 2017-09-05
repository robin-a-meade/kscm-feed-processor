package edu.hawaii.kscmfeedprocessor.banner;

import edu.hawaii.kscmfeedprocessor.Util;

import java.math.BigDecimal;
import java.util.Date;


public class Scrfees {

    // Common columns
    private String vpdiCode;
    private String subjCode;
    private String crseNumb;
    private String userId;
    private Date activityDate;
    private String dataOrigin;
    private BigDecimal surrogateId; // NOTE: always null in Banner DB
    private BigDecimal version;     // NOTE: always null in Banner DB
    // Unique to this table
    private String effTerm;
    private String detlCode;
    private BigDecimal feeAmount;
    private int seqno;

    /**
     * FLAT:
     *   SCRFEES_FEE_IND_IND = 1
     *   SCRFEES_FEE_IND = 'F'
     * CRED:
     *   SCRFEES_FEE_IND_IND = 0
     *   SCRFEES_FEE_IND = 'C'
     */
    private String ftypCode;  // NOTE: we'll assume always FLAT
    private int feeIndInd;    // determined by ftypCode
    private String feeInd;    // determined by ftypCode

    public String toJson() {
        return Util.toJson(this);
    }

    // *********************************************************************
    // *                  Generated getters and setters                    *
    // *********************************************************************

    public String getVpdiCode() {
        return vpdiCode;
    }

    public void setVpdiCode(String vpdiCode) {
        this.vpdiCode = vpdiCode;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    public String getDataOrigin() {
        return dataOrigin;
    }

    public void setDataOrigin(String dataOrigin) {
        this.dataOrigin = dataOrigin;
    }

    public BigDecimal getSurrogateId() {
        return surrogateId;
    }

    public void setSurrogateId(BigDecimal surrogateId) {
        this.surrogateId = surrogateId;
    }

    public BigDecimal getVersion() {
        return version;
    }

    public void setVersion(BigDecimal version) {
        this.version = version;
    }

    public String getEffTerm() {
        return effTerm;
    }

    public void setEffTerm(String effTerm) {
        this.effTerm = effTerm;
    }

    public String getDetlCode() {
        return detlCode;
    }

    public void setDetlCode(String detlCode) {
        this.detlCode = detlCode;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public int getSeqno() {
        return seqno;
    }

    public void setSeqno(int seqno) {
        this.seqno = seqno;
    }

    public String getFtypCode() {
        return ftypCode;
    }

    public void setFtypCode(String ftypCode) {
        this.ftypCode = ftypCode;
        switch (ftypCode) {
            case "FLAT":
                feeIndInd = 1;
                feeInd = "F";
                break;
            case "CRED":
                feeIndInd = 0;
                feeInd = "C";
                break;
        }
    }

    public int getFeeIndInd() {
        return feeIndInd;
    }

    public String getFeeInd() {
        return feeInd;
    }
}
