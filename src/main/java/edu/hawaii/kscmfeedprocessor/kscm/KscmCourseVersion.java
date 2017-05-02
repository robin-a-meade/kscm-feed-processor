package edu.hawaii.kscmfeedprocessor.kscm;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Throwables;
import edu.hawaii.kscmfeedprocessor.Util;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class KscmCourseVersion {

    // This field is needed to cope with misspelled GKEYs. In such cases we switch on instCode.
    // When the mispellings are corrected, this field can be deleted.
    private String instCode;

    private String pid;

    private String id;

    private BigDecimal updated;

    private String updatedBy;

    private String subjectCode;

    private String number;

    private String title;

    private String bdeTranscriptTitle;

    private String description;

    private String bdeCstaCode;

    private String bdeCollCode;

    private String bdeDivsCode;

    private String bdeDeptCode;

    private String bdeAprvCode;

    private BigDecimal bdeCreditLow;

    private String bdeCreditOpt;

    private BigDecimal bdeCreditHigh;

    private BigDecimal bdeBillingLow;

    private String bdeBillingOpt;

    private BigDecimal bdeBillingHigh;

    private BigDecimal bdeLectureLow;

    private String bdeLectureOpt;

    private BigDecimal bdeLectureHigh;

    private BigDecimal bdeLabLow;

    private String bdeLabOpt;

    private BigDecimal bdeLabHigh;

    private BigDecimal bdeOtherLow;

    private String bdeOtherOpt;

    private BigDecimal bdeOtherHigh;

    private BigDecimal bdeContactLow;

    private String bdeContactOpt;

    private BigDecimal bdeContactHigh;

    private BigDecimal bdeRepeatLimit;

    private BigDecimal bdeMaxCredits;

    private Boolean bannerIntegrationFlag;

    // To allow for misspelling
    private Boolean bannerintegrationFlag;

    private String bannerIntegrationResults;

    // To allow for misspelling
    private String bannerintegrationResults;

    private Term startTerm;

    private String bdeGradingOptionDef;

    private GradingOptions bdeGradingOptions;

    private CourseLevels bdeCourseLevels;

    // To allow for misspelling
    private CourseLevels bdeCourseLevel;

    private ScheduleTypes bdeScheduleTypes;

    private DegreeAttributes bdeDegreeAttributes;

    public String getInstCode() {
        return instCode;
    }

    public void setInstCode(String instCode) {
        this.instCode = instCode;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getUpdated() {
        return updated;
    }

    public void setUpdated(BigDecimal updated) {
        this.updated = updated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBdeTranscriptTitle() {
        return bdeTranscriptTitle;
    }

    public void setBdeTranscriptTitle(String bdeTranscriptTitle) {
        this.bdeTranscriptTitle = bdeTranscriptTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBdeCstaCode() {
        return bdeCstaCode;
    }

    public void setBdeCstaCode(String bdeCstaCode) {
        this.bdeCstaCode = bdeCstaCode;
    }

    public String getBdeCollCode() {
        return bdeCollCode;
    }

    public void setBdeCollCode(String bdeCollCode) {
        this.bdeCollCode = bdeCollCode;
    }

    public String getBdeDivsCode() {
        return bdeDivsCode;
    }

    public void setBdeDivsCode(String bdeDivsCode) {
        this.bdeDivsCode = bdeDivsCode;
    }

    public String getBdeDeptCode() {
        return bdeDeptCode;
    }

    public void setBdeDeptCode(String bdeDeptCode) {
        this.bdeDeptCode = bdeDeptCode;
    }

    public String getBdeAprvCode() {
        return bdeAprvCode;
    }

    public void setBdeAprvCode(String bdeAprvCode) {
        this.bdeAprvCode = bdeAprvCode;
    }

    public BigDecimal getBdeCreditLow() {
        return bdeCreditLow;
    }

    public void setBdeCreditLow(BigDecimal bdeCreditLow) {
        this.bdeCreditLow = bdeCreditLow;
    }

    public String getBdeCreditOpt() {
        return bdeCreditOpt;
    }

    public void setBdeCreditOpt(String bdeCreditOpt) {
        this.bdeCreditOpt = bdeCreditOpt;
    }

    public BigDecimal getBdeCreditHigh() {
        return bdeCreditHigh;
    }

    public void setBdeCreditHigh(BigDecimal bdeCreditHigh) {
        this.bdeCreditHigh = bdeCreditHigh;
    }

    public BigDecimal getBdeBillingLow() {
        return bdeBillingLow;
    }

    public void setBdeBillingLow(BigDecimal bdeBillingLow) {
        this.bdeBillingLow = bdeBillingLow;
    }

    public String getBdeBillingOpt() {
        return bdeBillingOpt;
    }

    public void setBdeBillingOpt(String bdeBillingOpt) {
        this.bdeBillingOpt = bdeBillingOpt;
    }

    public BigDecimal getBdeBillingHigh() {
        return bdeBillingHigh;
    }

    public void setBdeBillingHigh(BigDecimal bdeBillingHigh) {
        this.bdeBillingHigh = bdeBillingHigh;
    }

    public BigDecimal getBdeLectureLow() {
        return bdeLectureLow;
    }

    public void setBdeLectureLow(BigDecimal bdeLectureLow) {
        this.bdeLectureLow = bdeLectureLow;
    }

    public String getBdeLectureOpt() {
        return bdeLectureOpt;
    }

    public void setBdeLectureOpt(String bdeLectureOpt) {
        this.bdeLectureOpt = bdeLectureOpt;
    }

    public BigDecimal getBdeLectureHigh() {
        return bdeLectureHigh;
    }

    public void setBdeLectureHigh(BigDecimal bdeLectureHigh) {
        this.bdeLectureHigh = bdeLectureHigh;
    }

    public BigDecimal getBdeLabLow() {
        return bdeLabLow;
    }

    public void setBdeLabLow(BigDecimal bdeLabLow) {
        this.bdeLabLow = bdeLabLow;
    }

    public String getBdeLabOpt() {
        return bdeLabOpt;
    }

    public void setBdeLabOpt(String bdeLabOpt) {
        this.bdeLabOpt = bdeLabOpt;
    }

    public BigDecimal getBdeLabHigh() {
        return bdeLabHigh;
    }

    public void setBdeLabHigh(BigDecimal bdeLabHigh) {
        this.bdeLabHigh = bdeLabHigh;
    }

    public BigDecimal getBdeOtherLow() {
        return bdeOtherLow;
    }

    public void setBdeOtherLow(BigDecimal bdeOtherLow) {
        this.bdeOtherLow = bdeOtherLow;
    }

    public String getBdeOtherOpt() {
        return bdeOtherOpt;
    }

    public void setBdeOtherOpt(String bdeOtherOpt) {
        this.bdeOtherOpt = bdeOtherOpt;
    }

    public BigDecimal getBdeOtherHigh() {
        return bdeOtherHigh;
    }

    public void setBdeOtherHigh(BigDecimal bdeOtherHigh) {
        this.bdeOtherHigh = bdeOtherHigh;
    }

    public BigDecimal getBdeContactLow() {
        return bdeContactLow;
    }

    public void setBdeContactLow(BigDecimal bdeContactLow) {
        this.bdeContactLow = bdeContactLow;
    }

    public String getBdeContactOpt() {
        return bdeContactOpt;
    }

    public void setBdeContactOpt(String bdeContactOpt) {
        this.bdeContactOpt = bdeContactOpt;
    }

    public BigDecimal getBdeContactHigh() {
        return bdeContactHigh;
    }

    public void setBdeContactHigh(BigDecimal bdeContactHigh) {
        this.bdeContactHigh = bdeContactHigh;
    }

    public BigDecimal getBdeRepeatLimit() {
        return bdeRepeatLimit;
    }

    public void setBdeRepeatLimit(BigDecimal bdeRepeatLimit) {
        this.bdeRepeatLimit = bdeRepeatLimit;
    }

    public BigDecimal getBdeMaxCredits() {
        return bdeMaxCredits;
    }

    public void setBdeMaxCredits(BigDecimal bdeMaxCredits) {
        this.bdeMaxCredits = bdeMaxCredits;
    }

    public Boolean getBannerIntegrationFlag() {
        // Allow for misspelling
        switch (this.instCode) {
            case "MAN":
                return bannerintegrationFlag;
            default:
                return bannerIntegrationFlag;
        }
    }

    // This is the proper spelling
    public void setBannerIntegrationFlag(Boolean bannerIntegrationFlag) {
        this.bannerIntegrationFlag = bannerIntegrationFlag;
    }

    // To allow for misspelling
    public void setBannerintegrationFlag(Boolean bannerintegrationFlag) {
        this.bannerIntegrationFlag = bannerintegrationFlag;
    }

    public String getBannerIntegrationResults() {
        // To allow for misspelling
        switch (this.instCode) {
            case "MAN":
                return bannerintegrationResults;
            default:
                return bannerIntegrationResults;
        }
    }

    // This is the proper spelling
    public void setBannerIntegrationResults(String bannerIntegrationResults) {
        this.bannerIntegrationResults = bannerIntegrationResults;
    }

    // To allow for misspelling
    public void setBannerintegrationResults(String bannerintegrationResults) {
        this.bannerIntegrationResults = bannerintegrationResults;
    }

    public Term getStartTerm() {
        return startTerm;
    }

    public void setStartTerm(Term startTerm) {
        this.startTerm = startTerm;
    }

    public String getBdeGradingOptionDef() {
        return bdeGradingOptionDef;
    }

    public void setBdeGradingOptionDef(String bdeGradingOptionDef) {
        this.bdeGradingOptionDef = bdeGradingOptionDef;
    }

    public GradingOptions getBdeGradingOptions() {
        return bdeGradingOptions;
    }

    public void setBdeGradingOptions(GradingOptions bdeGradingOptions) {
        this.bdeGradingOptions = bdeGradingOptions;
    }

    public CourseLevels getBdeCourseLevels() {
        switch (this.instCode) {
            case "HAW":
            case "LEE":
            case "MAU":
            case "WIN":
            case "WOA":
                return bdeCourseLevel;
            default:
                return bdeCourseLevels;
        }
    }

    public void setBdeCourseLevels(CourseLevels bdeCourseLevels) {
        this.bdeCourseLevels = bdeCourseLevels;
    }

    public void setBdeCourseLevel(CourseLevels bdeCourseLevel) {
        this.bdeCourseLevel = bdeCourseLevel;
    }
    public ScheduleTypes getBdeScheduleTypes() {
        return bdeScheduleTypes;
    }

    public void setBdeScheduleTypes(ScheduleTypes bdeScheduleTypes) {
        this.bdeScheduleTypes = bdeScheduleTypes;
    }

    public DegreeAttributes getBdeDegreeAttributes() {
        switch (this.instCode) {
            case "WOA":
                // Hard coded until schema is fixed, to get through testing
                DegreeAttributes da = new DegreeAttributes();
                da.setDegreeAttribute("attrDA", true);
                return da;
            default:
                return bdeDegreeAttributes;
        }
    }

    public void setBdeDegreeAttributes(DegreeAttributes bdeDegreeAttributes) {
        this.bdeDegreeAttributes = bdeDegreeAttributes;
    }

    /**********************************************************************
     *                        AdditionalProperties
     **********************************************************************/
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public String toJson() {
        return Util.toJson(this);
    }

}
