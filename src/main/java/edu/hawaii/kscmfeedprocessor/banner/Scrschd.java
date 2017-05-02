package edu.hawaii.kscmfeedprocessor.banner;

import java.math.BigDecimal;
import java.util.Date;

public class Scrschd {
    // Common columns
    private String subjCode;
    private String crseNumb;
    private String vpdiCode;
    private String userId;
    private Date activityDate;
    private BigDecimal surrogateId; // NOTE: always null in Banner DB
    private BigDecimal version;     // NOTE: always null in Banner DB
    private String dataOrigin;
    // Unique to this table
    private String effTerm;
    private String schdCode;
    private BigDecimal workload; // Default is null
    private BigDecimal maxEnrl; // Default is null
    private BigDecimal adjWorkload; // Default is null
    private String insmCode; // Default is null

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

    public String getEffTerm() {
        return effTerm;
    }

    public void setEffTerm(String effTerm) {
        this.effTerm = effTerm;
    }

    public String getSchdCode() {
        return schdCode;
    }

    public void setSchdCode(String schdCode) {
        this.schdCode = schdCode;
    }

    public BigDecimal getWorkload() {
        return workload;
    }

    public void setWorkload(BigDecimal workload) {
        this.workload = workload;
    }

    public BigDecimal getMaxEnrl() {
        return maxEnrl;
    }

    public void setMaxEnrl(BigDecimal maxEnrl) {
        this.maxEnrl = maxEnrl;
    }

    public BigDecimal getAdjWorkload() {
        return adjWorkload;
    }

    public void setAdjWorkload(BigDecimal adjWorkload) {
        this.adjWorkload = adjWorkload;
    }

    public String getInsmCode() {
        return insmCode;
    }

    public void setInsmCode(String insmCode) {
        this.insmCode = insmCode;
    }
}
