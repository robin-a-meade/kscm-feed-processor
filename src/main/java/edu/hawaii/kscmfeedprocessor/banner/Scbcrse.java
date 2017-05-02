package edu.hawaii.kscmfeedprocessor.banner;


import edu.hawaii.kscmfeedprocessor.Util;
import org.apache.commons.lang3.StringEscapeUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Scbcrse {
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
    private String title;
    private String collCode;
    private String divsCode;
    private String deptCode;
    private String cstaCode;
    private String aprvCode;
    private BigDecimal repeatLimit;
    private BigDecimal maxRptUnits;
    private String repsCode;
    private String prereqChkMethodCde;
    private String cappPrereqTestInd;
    private String tuiwInd;
    private String pwavCode;
    private String addFeesInd;

    // creditHr
    private String creditHrInd;
    private BigDecimal creditHrLow;
    private BigDecimal creditHrHigh;
    // billHr
    private String billHrInd;
    private BigDecimal billHrLow;
    private BigDecimal billHrHigh;
    // lecHr
    private String lecHrInd;
    private BigDecimal lecHrLow;
    private BigDecimal lecHrHigh;
    // labHr
    private String labHrInd;
    private BigDecimal labHrLow;
    private BigDecimal labHrHigh;
    // othHr
    private String othHrInd;
    private BigDecimal othHrLow;
    private BigDecimal othHrHigh;
    // contHr
    private String contHrInd;
    private BigDecimal contHrLow;
    private BigDecimal contHrHigh;
    // sdbdesc
    private Scbdesc scbdesc;
    // scrlevl
    private List<Scrlevl> listOfScrlevl;
    // scrattr
    private List<Scrattr> listOfScrattr;
    // scrgmod
    private List<Scrgmod> listOfScrgmod;
    // scrschd
    private List<Scrschd> listOfScrschd;
    // scrsyln
    private Scrsyln scrsyln;

    public String toJson() {
        return Util.toJson(this);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append('{')
                .append(subjCode)
                .append(crseNumb)
                .append(' ')
                .append(effTerm)
                .append(' ')
                .append('"')
                .append(StringEscapeUtils.escapeJava(title))
                .append('"')
                .append('}');
        return sb.toString();
    }

    // Generated getters and setters

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCollCode() {
        return collCode;
    }

    public void setCollCode(String collCode) {
        this.collCode = collCode;
    }

    public String getDivsCode() {
        return divsCode;
    }

    public void setDivsCode(String divsCode) {
        this.divsCode = divsCode;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getCstaCode() {
        return cstaCode;
    }

    public void setCstaCode(String cstaCode) {
        this.cstaCode = cstaCode;
    }

    public String getAprvCode() {
        return aprvCode;
    }

    public void setAprvCode(String aprvCode) {
        this.aprvCode = aprvCode;
    }

    public BigDecimal getRepeatLimit() {
        return repeatLimit;
    }

    public void setRepeatLimit(BigDecimal repeatLimit) {
        this.repeatLimit = repeatLimit;
    }

    public BigDecimal getMaxRptUnits() {
        return maxRptUnits;
    }

    public void setMaxRptUnits(BigDecimal maxRptUnits) {
        this.maxRptUnits = maxRptUnits;
    }

    public String getPrereqChkMethodCde() {
        return prereqChkMethodCde;
    }

    public void setPrereqChkMethodCde(String prereqChkMethodCde) {
        this.prereqChkMethodCde = prereqChkMethodCde;
    }

    public String getTuiwInd() {
        return tuiwInd;
    }

    public void setTuiwInd(String tuiwInd) {
        this.tuiwInd = tuiwInd;
    }

    public String getPwavCode() {
        return pwavCode;
    }

    public void setPwavCode(String pwavCode) {
        this.pwavCode = pwavCode;
    }

    public String getAddFeesInd() {
        return addFeesInd;
    }

    public void setAddFeesInd(String addFeesInd) {
        this.addFeesInd = addFeesInd;
    }

    public String getCreditHrInd() {
        return creditHrInd;
    }

    public void setCreditHrInd(String creditHrInd) {
        this.creditHrInd = creditHrInd;
    }

    public BigDecimal getCreditHrLow() {
        return creditHrLow;
    }

    public void setCreditHrLow(BigDecimal creditHrLow) {
        this.creditHrLow = creditHrLow;
    }

    public BigDecimal getCreditHrHigh() {
        return creditHrHigh;
    }

    public void setCreditHrHigh(BigDecimal creditHrHigh) {
        this.creditHrHigh = creditHrHigh;
    }

    public String getLecHrInd() {
        return lecHrInd;
    }

    public void setLecHrInd(String lecHrInd) {
        this.lecHrInd = lecHrInd;
    }

    public BigDecimal getLecHrLow() {
        return lecHrLow;
    }

    public void setLecHrLow(BigDecimal lecHrLow) {
        this.lecHrLow = lecHrLow;
    }

    public BigDecimal getLecHrHigh() {
        return lecHrHigh;
    }

    public void setLecHrHigh(BigDecimal lecHrHigh) {
        this.lecHrHigh = lecHrHigh;
    }

    public String getLabHrInd() {
        return labHrInd;
    }

    public void setLabHrInd(String labHrInd) {
        this.labHrInd = labHrInd;
    }

    public BigDecimal getLabHrLow() {
        return labHrLow;
    }

    public void setLabHrLow(BigDecimal labHrLow) {
        this.labHrLow = labHrLow;
    }

    public BigDecimal getLabHrHigh() {
        return labHrHigh;
    }

    public void setLabHrHigh(BigDecimal labHrHigh) {
        this.labHrHigh = labHrHigh;
    }

    public String getOthHrInd() {
        return othHrInd;
    }

    public void setOthHrInd(String othHrInd) {
        this.othHrInd = othHrInd;
    }

    public BigDecimal getOthHrLow() {
        return othHrLow;
    }

    public void setOthHrLow(BigDecimal othHrLow) {
        this.othHrLow = othHrLow;
    }

    public BigDecimal getOthHrHigh() {
        return othHrHigh;
    }

    public void setOthHrHigh(BigDecimal othHrHigh) {
        this.othHrHigh = othHrHigh;
    }

    public String getBillHrInd() {
        return billHrInd;
    }

    public void setBillHrInd(String billHrInd) {
        this.billHrInd = billHrInd;
    }

    public BigDecimal getBillHrLow() {
        return billHrLow;
    }

    public void setBillHrLow(BigDecimal billHrLow) {
        this.billHrLow = billHrLow;
    }

    public BigDecimal getBillHrHigh() {
        return billHrHigh;
    }

    public void setBillHrHigh(BigDecimal billHrHigh) {
        this.billHrHigh = billHrHigh;
    }

    public String getContHrInd() {
        return contHrInd;
    }

    public void setContHrInd(String contHrInd) {
        this.contHrInd = contHrInd;
    }

    public BigDecimal getContHrLow() {
        return contHrLow;
    }

    public void setContHrLow(BigDecimal contHrLow) {
        this.contHrLow = contHrLow;
    }

    public BigDecimal getContHrHigh() {
        return contHrHigh;
    }

    public void setContHrHigh(BigDecimal contHrHigh) {
        this.contHrHigh = contHrHigh;
    }

    public Scbdesc getScbdesc() {
        return scbdesc;
    }

    public void setScbdesc(Scbdesc scbdesc) {
        this.scbdesc = scbdesc;
    }

    public List<Scrlevl> getListOfScrlevl() {
        return listOfScrlevl;
    }

    public void setListOfScrlevl(List<Scrlevl> listOfScrlevl) {
        this.listOfScrlevl = listOfScrlevl;
    }

    public List<Scrattr> getListOfScrattr() {
        return listOfScrattr;
    }

    public void setListOfScrattr(List<Scrattr> listOfScrattr) {
        this.listOfScrattr = listOfScrattr;
    }

    public List<Scrgmod> getListOfScrgmod() {
        return listOfScrgmod;
    }

    public void setListOfScrgmod(List<Scrgmod> listOfScrgmod) {
        this.listOfScrgmod = listOfScrgmod;
    }

    public List<Scrschd> getListOfScrschd() {
        return listOfScrschd;
    }

    public void setListOfScrschd(List<Scrschd> listOfScrschd) {
        this.listOfScrschd = listOfScrschd;
    }

    public Scrsyln getScrsyln() {
        return scrsyln;
    }

    public void setScrsyln(Scrsyln scrsyln) {
        this.scrsyln = scrsyln;
    }

    public String getRepsCode() {
        return repsCode;
    }

    public void setRepsCode(String repsCode) {
        this.repsCode = repsCode;
    }

    public String getCappPrereqTestInd() {
        return cappPrereqTestInd;
    }

    public void setCappPrereqTestInd(String cappPrereqTestInd) {
        this.cappPrereqTestInd = cappPrereqTestInd;
    }
}
