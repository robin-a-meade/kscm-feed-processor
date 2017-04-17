package edu.hawaii.kscm;

import edu.hawaii.kscm.banner.SCBCRSE;
import edu.hawaii.kscm.domain.Course;
import edu.hawaii.kscm.domain.CourseTerm;
import edu.hawaii.kscm.domain.SubjectCodeOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 *
 */
@Component
public class CourseConverter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    KscmService kscmService;

    String convertTerm(CourseTerm t) {
        String year = t.getYear();
        String twoDigitTermCode = t.getTermOptionId().substring(4);
        if (twoDigitTermCode.startsWith("1")) {
            year = "" + (Integer.parseInt(year, 10) + 1);
        }
        return year + twoDigitTermCode;
    }

    String convertCstaCode(String cstaCodeId) {
        return cstaCodeId.substring(4);
    }

    String convertHrOpt(String opt, String type) throws Exception {
        switch (opt) {
            case "or":
                return "OR";
            case "to":
                return "TO";
            default:
                throw new Exception(String.format("{} Hours indicator must be set to OR or TO", type));
        }
    }

    public SCBCRSE convert(Course c, String instCode) throws Exception {
        KscmOptions kscmOptions = kscmService.getKscmOptionsFor(instCode);
        SCBCRSE scbcrse = new SCBCRSE();

        // subjCode
        SubjectCodeOption subjectCodeOption = kscmOptions.getSubjectCodesById().get(c.getSubjectCode());
        if (subjectCodeOption == null) {
            throw new Exception(String.format("%s Couldn't find SubjectCodeOption.id '%s'", instCode, c.getSubjectCode(), c.getId()));
        }
        scbcrse.setSubjCode(subjectCodeOption.getName());

        // crseNumb
        scbcrse.setCrseNumb(c.getNumber());

        // effTerm
        scbcrse.setEffTerm(convertTerm(c.getStartTerm()));

        // vpdiCode
        scbcrse.setVpdiCode(instCode);

        // title
        scbcrse.setTitle(c.getBdeTranscriptTitle());

        // collCode
        scbcrse.setCollCode(c.getBdeCollCode());

        // divsCode
        scbcrse.setDivsCode(c.getBdeDivsCode());

        // deptCode
        scbcrse.setDeptCode(c.getBdeDeptCode());

        // cstaCode
        scbcrse.setCstaCode(convertCstaCode(c.getBdeCstaCode()));

        // aprvCode
        scbcrse.setAprvCode(c.getBdeAprvCode());

        // repeatLimit
        scbcrse.setRepeatLimit(c.getBdeRepeatLimit());

        // maxRptUnits
        scbcrse.setMaxRptUnits(c.getBdeMaxCredits());

        // prereqChkMethodCde
        // Skipping

        // dataOrigin
        scbcrse.setDataOrigin("KSCM");

        // userId
        // TODO: see if we can pull the KSCM user
        scbcrse.setUserId("KSCM");

        // activityDate
        scbcrse.setActivityDate(new Date());

        scbcrse.setCreditHrHigh(c.getBdeCreditHigh());
        scbcrse.setCreditHrInd(convertHrOpt(c.getBdeCreditOpt(), "Credit"));
        scbcrse.setCreditHrLow(c.getBdeCreditLow());

        scbcrse.setLecHrHigh(c.getBdeLectureHigh());
        scbcrse.setLecHrInd(convertHrOpt(c.getBdeCreditOpt(), "Lecture"));
        scbcrse.setLecHrLow(c.getBdeCreditLow());

        scbcrse.setLabHrHigh(c.getBdeLabHigh());
        scbcrse.setLabHrInd(convertHrOpt(c.getBdeLabOpt(), "Lab"));
        scbcrse.setLabHrLow(c.getBdeLabLow());

        scbcrse.setOthHrHigh(c.getBdeOtherHigh());
        scbcrse.setOthHrInd(convertHrOpt(c.getBdeOtherOpt(), "Other"));
        scbcrse.setOthHrLow(c.getBdeOtherLow());

        scbcrse.setBillHrHigh(c.getBdeBillingHigh());
        scbcrse.setBillHrInd(convertHrOpt(c.getBdeBillingOpt(), "Billing"));
        scbcrse.setBillHrLow(c.getBdeBillingLow());

        scbcrse.setContHrHigh(c.getBdeContactHigh());
        scbcrse.setContHrInd(convertHrOpt(c.getBdeContactOpt(), "Contact"));
        scbcrse.setContHrLow(c.getBdeContactLow());

        return scbcrse;
    }
}
