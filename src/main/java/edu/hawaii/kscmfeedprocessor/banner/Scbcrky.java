package edu.hawaii.kscmfeedprocessor.banner;

import java.math.BigDecimal;
import java.util.Date;

public class Scbcrky {
    // Common columns
    private String subjCode;
    private String crseNumb;
    private String vpdiCode;
    private String userId;
    private Date activityDate;
    private BigDecimal surrogateId;
    private BigDecimal version;
    private String dataOrigin;
    // Unique to this table
    private String termCodeStart;
    private String termCodeEnd;

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

    public String getVpdiCode() {
        return vpdiCode;
    }

    public void setVpdiCode(String vpdiCode) {
        this.vpdiCode = vpdiCode;
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

    public String getTermCodeStart() {
        return termCodeStart;
    }

    public void setTermCodeStart(String termCodeStart) {
        this.termCodeStart = termCodeStart;
    }

    public String getTermCodeEnd() {
        return termCodeEnd;
    }

    public void setTermCodeEnd(String termCodeEnd) {
        this.termCodeEnd = termCodeEnd;
    }
}


