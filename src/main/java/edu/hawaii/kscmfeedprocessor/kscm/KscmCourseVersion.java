package edu.hawaii.kscmfeedprocessor.kscm;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.hawaii.kscmfeedprocessor.Util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
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

    private String bdeLongTitle;

    private String bdeCourseUrl;

    private String bdeDescription;

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

    private String bannerIntegrationResults;

    private Term startTerm; // Terms 1.0 Obsolete

    private String dateStart; // Terms 3.0

    private String dateStartLabel; // Terms 3.0

    private String bdeGradingOptionDef;

    private GradingOptions bdeGradingOptions;

    private CourseLevels bdeCourseLevels;

    private ScheduleTypes bdeScheduleTypes;

    private DegreeAttributes bdeDegreeAttributes;

    private String bdeLearningObjectives;

    private String bdeInstRpt;

    private String[] bdeIntPartners;

    public String[] getBdeIntPartners() {
        return bdeIntPartners;
    }

    public void setBdeIntPartners(String[] bdeIntPartners) {
        this.bdeIntPartners = bdeIntPartners;
    }

    private String[] bdeCorequisites;

    public String[] getBdeCorequisites() {
        return bdeCorequisites;
    }

    public void setBdeCorequisites(String[] bdeCorequisites) {
        this.bdeCorequisites = bdeCorequisites;
    }

    private List<CourseTextRow> bdeCourseText;

    public List<CourseTextRow> getBdeCourseText() {
        return bdeCourseText;
    }

    public void setBdeCourseText(List<CourseTextRow> bdeCourseText) {
        this.bdeCourseText = bdeCourseText;
    }

    private List<FeesRow> bdeFees;


    public List<FeesRow> getBdeFees() {
        return bdeFees;
    }

    public void setBdeFees(List<FeesRow> bdeFees) {
        this.bdeFees = bdeFees;
    }

    private List<EquivalentCoursesRow> bdeEquivalentCourses;

    public List<EquivalentCoursesRow> getBdeEquivalentCourses() {
        return bdeEquivalentCourses;
    }

    public void setBdeEquivalentCourses(List<EquivalentCoursesRow> bdeEquivalentCourses) {
        this.bdeEquivalentCourses = bdeEquivalentCourses;
    }

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

    public String getBdeLongTitle() {
        return bdeLongTitle;
    }

    public void setBdeLongTitle(String bdeLongTitle) {
        this.bdeLongTitle = bdeLongTitle;
    }

    public String getBdeCourseUrl() {
        return bdeCourseUrl;
    }

    public void setBdeCourseUrl(String bdeCourseUrl) {
        this.bdeCourseUrl = bdeCourseUrl;
    }

    public String getBdeDescription() {
        return bdeDescription;
    }

    public void setBdeDescription(String bdeDescription) {
        this.bdeDescription = bdeDescription;
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
        return bannerIntegrationFlag;
    }

    public void setBannerIntegrationFlag(Boolean bannerIntegrationFlag) {
        this.bannerIntegrationFlag = bannerIntegrationFlag;
    }

    public String getBannerIntegrationResults() {
        return bannerIntegrationResults;
    }

    public void setBannerIntegrationResults(String bannerIntegrationResults) {
        this.bannerIntegrationResults = bannerIntegrationResults;
    }

    public Term getStartTerm() {
        return startTerm;
    }

    public void setStartTerm(Term startTerm) {
        this.startTerm = startTerm;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateStartLabel() {
        return dateStartLabel;
    }

    public void setDateStartLabel(String dateStartLabel) {
        this.dateStartLabel = dateStartLabel;
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
        return bdeCourseLevels;
    }

    public void setBdeCourseLevels(CourseLevels bdeCourseLevels) {
        this.bdeCourseLevels = bdeCourseLevels;
    }

    public ScheduleTypes getBdeScheduleTypes() {
        return bdeScheduleTypes;
    }

    public void setBdeScheduleTypes(ScheduleTypes bdeScheduleTypes) {
        this.bdeScheduleTypes = bdeScheduleTypes;
    }

    public DegreeAttributes getBdeDegreeAttributes() {
        return bdeDegreeAttributes;
    }

    public void setBdeDegreeAttributes(DegreeAttributes bdeDegreeAttributes) {
        this.bdeDegreeAttributes = bdeDegreeAttributes;
    }

    public String getBdeLearningObjectives() {
        return bdeLearningObjectives;
    }

    public void setBdeLearningObjectives(String bdeLearningObjectives) {
        this.bdeLearningObjectives = bdeLearningObjectives;
    }

    public String getBdeInstRpt() {
        return bdeInstRpt;
    }

    public void setBdeInstRpt(String bdeInstRpt) {
        this.bdeInstRpt = bdeInstRpt;
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

    public void afterDeserialization() throws Exception {
        // BdeEquivalentCourses
        List<EquivalentCoursesRow> listEquivalentCoursesRow = getBdeEquivalentCourses();
        while (listEquivalentCoursesRow.remove(null));  // Remove nulls
        // Remove rows with null data fields or empty string data fields
        // Use ListIterator technique described here:
        // [Remove elements from collection while iterating]
        // (https://stackoverflow.com/questions/10431981)
        ListIterator<EquivalentCoursesRow> iter = listEquivalentCoursesRow.listIterator();
        while (iter.hasNext()) {
            EquivalentCoursesRow equivalentCoursesRow = iter.next();
            if (equivalentCoursesRow.getSubjNumb() == null || equivalentCoursesRow.getSubjNumb().trim().equals("")) {
                iter.remove();
                continue;
            }
            equivalentCoursesRow.parseSubjNumb();
        }

        // BdeFees
        List<FeesRow> listFeesRow = getBdeFees();
        while (listFeesRow.remove(null));  // Remove nulls

        ListIterator<FeesRow> iterFeesRow = listFeesRow.listIterator();
        while (iterFeesRow.hasNext()) {
            FeesRow feesRow = iterFeesRow.next();
            if (feesRow.getDetlCode() == null || feesRow.getDetlCode().trim().equals("")) {
                iterFeesRow.remove();
                continue;
            }
        }

        // BdeCourseText
        List<CourseTextRow> listCourseTextRow = getBdeCourseText();
        while (listCourseTextRow.remove(null)); // Remove nulls

    }

}
