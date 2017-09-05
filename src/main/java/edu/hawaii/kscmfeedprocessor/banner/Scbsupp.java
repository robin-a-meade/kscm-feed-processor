package edu.hawaii.kscmfeedprocessor.banner;

import edu.hawaii.kscmfeedprocessor.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 */
public class Scbsupp {
    // Common columns
    private String vpdiCode;
    private String subjCode;
    private String crseNumb;
    private String userId;
    private Date activityDate;
    private BigDecimal surrogateId; // NOTE: always null in Banner DB
    private BigDecimal version;     // NOTE: always null in Banner DB
    private String dataOrigin;
    // Unique to this table
    private String effTerm;
    private String cudaCode;

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

    public String getDataOrigin() {
        return dataOrigin;
    }

    public void setDataOrigin(String dataOrigin) {
        this.dataOrigin = dataOrigin;
    }

    public String getEffTerm() {
        return effTerm;
    }

    public void setEffTerm(String effTerm) {
        this.effTerm = effTerm;
    }

    public String getCudaCode() {
        return cudaCode;
    }

    public void setCudaCode(String cudaCode) {
        this.cudaCode = cudaCode;
    }
}
