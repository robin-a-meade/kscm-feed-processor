package edu.hawaii.kscm.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class Course {

    // This is set afterwards
    private String instCode;

    private String pid;

    private String id;

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

    private Double bdeCreditLow;

    private String bdeCreditOpt;

    private Double bdeCreditHigh;

    private Double bdeBillingLow;

    private String bdeBillingOpt;

    private Double bdeBillingHigh;

    private Double bdeLectureLow;

    private String bdeLectureOpt;

    private Double bdeLectureHigh;

    private Double bdeLabLow;

    private String bdeLabOpt;

    private Double bdeLabHigh;

    private Double bdeOtherLow;

    private String bdeOtherOpt;

    private Double bdeOtherHigh;

    private Double bdeContactLow;

    private String bdeContactOpt;

    private Double bdeContactHigh;

    private Double bdeRepeatLimit;

    private Double bdeMaxCredits;

    private Boolean bannerIntegrationFlag;

    // To allow for misspelling
    private Boolean bannerintegrationFlag;

    private String bannerIntegrationResults;

    // To allow for misspelling
    private String bannerintegrationResults;

    private CourseTerm startTerm;

    private String bdeGradingOptionDef;

    private CourseGradingOptions bdeGradingOptions;

    private CourseLevels bdeCourseLevels;

    // To allow for misspelling
    private CourseLevels bdeCourseLevel;

    private CourseScheduleTypes bdeScheduleTypes;

    private CourseDegreeAttributes bdeDegreeAttributes;

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

    public Double getBdeCreditLow() {
        return bdeCreditLow;
    }

    public void setBdeCreditLow(Double bdeCreditLow) {
        this.bdeCreditLow = bdeCreditLow;
    }

    public String getBdeCreditOpt() {
        return bdeCreditOpt;
    }

    public void setBdeCreditOpt(String bdeCreditOpt) {
        this.bdeCreditOpt = bdeCreditOpt;
    }

    public Double getBdeCreditHigh() {
        return bdeCreditHigh;
    }

    public void setBdeCreditHigh(Double bdeCreditHigh) {
        this.bdeCreditHigh = bdeCreditHigh;
    }

    public Double getBdeBillingLow() {
        return bdeBillingLow;
    }

    public void setBdeBillingLow(Double bdeBillingLow) {
        this.bdeBillingLow = bdeBillingLow;
    }

    public String getBdeBillingOpt() {
        return bdeBillingOpt;
    }

    public void setBdeBillingOpt(String bdeBillingOpt) {
        this.bdeBillingOpt = bdeBillingOpt;
    }

    public Double getBdeBillingHigh() {
        return bdeBillingHigh;
    }

    public void setBdeBillingHigh(Double bdeBillingHigh) {
        this.bdeBillingHigh = bdeBillingHigh;
    }

    public Double getBdeLectureLow() {
        return bdeLectureLow;
    }

    public void setBdeLectureLow(Double bdeLectureLow) {
        this.bdeLectureLow = bdeLectureLow;
    }

    public String getBdeLectureOpt() {
        return bdeLectureOpt;
    }

    public void setBdeLectureOpt(String bdeLectureOpt) {
        this.bdeLectureOpt = bdeLectureOpt;
    }

    public Double getBdeLectureHigh() {
        return bdeLectureHigh;
    }

    public void setBdeLectureHigh(Double bdeLectureHigh) {
        this.bdeLectureHigh = bdeLectureHigh;
    }

    public Double getBdeLabLow() {
        return bdeLabLow;
    }

    public void setBdeLabLow(Double bdeLabLow) {
        this.bdeLabLow = bdeLabLow;
    }

    public String getBdeLabOpt() {
        return bdeLabOpt;
    }

    public void setBdeLabOpt(String bdeLabOpt) {
        this.bdeLabOpt = bdeLabOpt;
    }

    public Double getBdeLabHigh() {
        return bdeLabHigh;
    }

    public void setBdeLabHigh(Double bdeLabHigh) {
        this.bdeLabHigh = bdeLabHigh;
    }

    public Double getBdeOtherLow() {
        return bdeOtherLow;
    }

    public void setBdeOtherLow(Double bdeOtherLow) {
        this.bdeOtherLow = bdeOtherLow;
    }

    public String getBdeOtherOpt() {
        return bdeOtherOpt;
    }

    public void setBdeOtherOpt(String bdeOtherOpt) {
        this.bdeOtherOpt = bdeOtherOpt;
    }

    public Double getBdeOtherHigh() {
        return bdeOtherHigh;
    }

    public void setBdeOtherHigh(Double bdeOtherHigh) {
        this.bdeOtherHigh = bdeOtherHigh;
    }

    public Double getBdeContactLow() {
        return bdeContactLow;
    }

    public void setBdeContactLow(Double bdeContactLow) {
        this.bdeContactLow = bdeContactLow;
    }

    public String getBdeContactOpt() {
        return bdeContactOpt;
    }

    public void setBdeContactOpt(String bdeContactOpt) {
        this.bdeContactOpt = bdeContactOpt;
    }

    public Double getBdeContactHigh() {
        return bdeContactHigh;
    }

    public void setBdeContactHigh(Double bdeContactHigh) {
        this.bdeContactHigh = bdeContactHigh;
    }

    public Double getBdeRepeatLimit() {
        return bdeRepeatLimit;
    }

    public void setBdeRepeatLimit(Double bdeRepeatLimit) {
        this.bdeRepeatLimit = bdeRepeatLimit;
    }

    public Double getBdeMaxCredits() {
        return bdeMaxCredits;
    }

    public void setBdeMaxCredits(Double bdeMaxCredits) {
        this.bdeMaxCredits = bdeMaxCredits;
    }

    public Boolean getBannerIntegrationFlag() {
        // To allow for misspelling
        switch (this.instCode) {
            case "MAN":
                return bannerintegrationFlag;
            default:
                return bannerintegrationFlag;
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

    public CourseTerm getStartTerm() {
        return startTerm;
    }

    public void setStartTerm(CourseTerm startTerm) {
        this.startTerm = startTerm;
    }

    public String getBdeGradingOptionDef() {
        return bdeGradingOptionDef;
    }

    public void setBdeGradingOptionDef(String bdeGradingOptionDef) {
        this.bdeGradingOptionDef = bdeGradingOptionDef;
    }

    public CourseGradingOptions getBdeGradingOptions() {
        return bdeGradingOptions;
    }

    public void setBdeGradingOptions(CourseGradingOptions bdeGradingOptions) {
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
    public CourseScheduleTypes getBdeScheduleTypes() {
        return bdeScheduleTypes;
    }

    public void setBdeScheduleTypes(CourseScheduleTypes bdeScheduleTypes) {
        this.bdeScheduleTypes = bdeScheduleTypes;
    }

    public CourseDegreeAttributes getBdeDegreeAttributes() {
        switch (this.instCode) {
            case "WOA":
                // Hard coded until schema is fixed, to get through testing
                CourseDegreeAttributes da = new CourseDegreeAttributes();
                da.setDegreeAttribute("attrDA", true);
                return da;
            default:
                return bdeDegreeAttributes;
        }
    }

    public void setBdeDegreeAttributes(CourseDegreeAttributes bdeDegreeAttributes) {
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


}
