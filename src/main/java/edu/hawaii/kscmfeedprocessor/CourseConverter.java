package edu.hawaii.kscmfeedprocessor;

import edu.hawaii.kscmfeedprocessor.banner.*;
import edu.hawaii.kscmfeedprocessor.kscm.CourseLevels;
import edu.hawaii.kscmfeedprocessor.kscm.KscmCourseVersion;
import edu.hawaii.kscmfeedprocessor.kscm.Term;
import edu.hawaii.kscmfeedprocessor.kscm.SubjectCodeOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static java.lang.String.format;

/**
 * Converts KSCM course data to Banner course data
 */
@Component
public class CourseConverter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    KscmService kscmService;

    /**
     *  Convert kcv.subjectCode to corresponding Banner value.
     *  If can't, set runData.status to FAILURE and add message to runData.
     */
    String convertToSubjCode(RunData runData) {
        String msg;
        KscmOptions kscmOptions = kscmService.getKscmOptionsFor(runData.getInstCode());

        String subjCode = null;
        try {
            SubjectCodeOption subjectCodeOption = kscmOptions.getSubjectCodesById().get(runData.getKcv().getSubjectCode());
            assert subjectCodeOption != null : runData.getKcv().getSubjectCode();
            subjCode = subjectCodeOption.getName();
        } catch (Exception e) {
            // Set runData.status to FAILURE
            // Add message, including stack trace
            // Swallow exception
            msg = format("Convert: Error: couldn't get subject code for course version %s %s", runData.getHostPrefix(), runData
                    .getKscmCourseVersionId());
            runData.addMessage(msg, e);
            runData.setStatus(Status.FAILURE);
        }
        // TODO: additional validation checks here. Not null. Length <= 4.
        msg = format("Convert: Retrieved subject code %s for course version %s %s", subjCode, runData.getHostPrefix(), runData
                .getKscmCourseVersionId());
        runData.addMessage(msg);
        return subjCode;
    }

    /**
     * Convert kcv.number to corresponding Banner value.
     * If can't, set runData.status to FAILURE and add message to runData.
     */
    String convertToCrseNumb(RunData runData) {
        // TODO: add additional validation checks. Not null. Length <= 4.
        return runData.getKcv().getNumber();
    }

    /**
     * Convert KSCM term to Banner term. Helper function.
     */
    String convertKscmTermToBannerTerm(Term t) {
        //  TODO: add validation checks and throw exception if they fail
        String year = t.getYear();
        String twoDigitTermCode = t.getTermOptionId().substring(4);
        if (twoDigitTermCode.startsWith("1")) {
            year = "" + (Integer.parseInt(year, 10) + 1);
        }
        return year + twoDigitTermCode;
    }

    /**
     * Convert kcv.startTerm to corresponding Banner value.
     * If can't, set runData.status to FAILURE and add message to runData.
     */
    String convertKcvStartTermToBannerTerm(RunData runData) {
        //  TODO: if can't, set runData.status to FAILURE and add message to runData.
        String bannerTerm = convertKscmTermToBannerTerm(runData.getKcv().getStartTerm());
        return bannerTerm;
    }

    /**
     * Convert kcv.bdeTranscriptTitle to corresponding Banner value.
     * If can't, set runData.status to FAILURE and add message to runData.
     */
    String convertToTitle(RunData runData) {
        // TODO: add additional validation checks. Not null. Length <= 30.
        return runData.getKcv().getBdeTranscriptTitle();
    }

    /**
     * Convert kcv.bdeCollCode to corresponding Banner value.
     * If can't, set runData.status to FAILURE and add message to runData.
     */
    String convertToCollCode(RunData runData) {
        // TODO: if can't, set runData.status to FAILURE and add message to runData.
        String collCodeId = runData.getKcv().getBdeCollCode();
        return collCodeId.substring(4);
    }

    /**
     * Convert kcv.bdeDivsCode to corresponding Banner value.
     * If can't, set runData.status to FAILURE and add message to runData.
     */
    String convertToDivsCode(RunData runData) {
        // TODO: if can't, set runData.status to FAILURE and add message to runData.
        String divsCodeId = runData.getKcv().getBdeDivsCode();
        return divsCodeId.substring(4);
    }

    /**
     * Convert kcv.bdeDeptCode to corresponding Banner value.
     * If can't, set runData.status to FAILURE and add message to runData.
     */
    String convertToDeptCode(RunData runData) {
        // TODO: if can't, set runData.status to FAILURE and add message to runData.
        String deptCodeId = runData.getKcv().getBdeDeptCode();
        return deptCodeId.substring(4);
    }

    /**
     * Convert kcv.bdeCstaCode to corresponding Banner value.
     * If can't, set runData.status to FAILURE and add message to runData.
     */
    String convertToCstaCode(RunData runData) {
        // TODO: if can't, set runData.status to FAILURE and add message to runData.
        String cstaCodeId = runData.getKcv().getBdeCstaCode();
        return cstaCodeId.substring(4);
    }

    /**
     * Convert kcv.bdeAprvCode to corresponding Banner value.
     * If can't, set runData.status to FAILURE, add message to runData, and return null;
     */
    String convertToAprvCode(RunData runData) throws Exception {
        String aprvCodeString = runData.getKcv().getBdeAprvCode();
        switch (aprvCodeString) {
            case "experimental":
                return "E";
            case "approved":
                return "A";
            default:
                runData.setStatus(Status.FAILURE);
                String msg = format("Convert: Unexpected value for bdeAprvCode: '%s'", aprvCodeString);
                runData.addMessage(msg);
                return null;
        }
    }

    /**
     *  Convert kcv.bdeRepeatLimit to value needed by Banner value scbcrse.repeat_limit
     *  If can't, set runData.status to FAILURE and add message to runData.
     */
    BigDecimal convertToRepeatLimit(RunData runData) {
        // TODO: if can't, set runData.status to FAILURE and add message to runData.
        BigDecimal repeatLimit = runData.getKcv().getBdeRepeatLimit();
        return repeatLimit;
    }

    /**
     * Convert kcv.bdeMaxCredits to value needed by Banner value scbcrse.MaxRptUnits.
     * If can't, set runData.status to FAILURE and add message to runData.
     */
    BigDecimal convertToMaxRptUnits(RunData runData) {
        // TODO: if can't, set runData.status to FAILURE and add message to runData.
        BigDecimal maxRptUnits = runData.getKcv().getBdeMaxCredits();
        return maxRptUnits;
    }

    String convertToHrInd(RunData runData, String opt, String type) throws Exception {
        switch (opt) {
            case "or":
                return "OR";
            case "to":
                return "TO";
            case "none":
                return null;
            default:
                runData.setStatus(Status.FAILURE);
                String msg = format("Convert: Error: %s Hours indicator must be set to OR, TO, or null (\"None\")", type);
                runData.addMessage(msg);
                return null;
        }
    }

    public void convertHr(Scbcrse scbcrse, RunData runData, HrType hrType) {
        try {

            // no parameters
            final Class noparams[] = {};

            // String parameter
            final Class[] paramString = new Class[1];
            paramString[0] = String.class;

            // BigDecimal parameter
            final Class[] paramBigDecimal = new Class[1];
            paramBigDecimal[0] = BigDecimal.class;

            Method getHrLow = KscmCourseVersion.class.getDeclaredMethod("getBde" + hrType.getKscmName() + "Low", noparams);
            Method getHrOpt = KscmCourseVersion.class.getDeclaredMethod("getBde" + hrType.getKscmName() + "Opt", noparams);
            Method getHrHigh = KscmCourseVersion.class.getDeclaredMethod("getBde" + hrType.getKscmName() + "High", noparams);

            Method setHrLow = Scbcrse.class.getDeclaredMethod("set" + hrType.getBannerName() + "HrLow", paramBigDecimal);
            Method setHrInd = Scbcrse.class.getDeclaredMethod("set" + hrType.getBannerName() + "HrInd", paramString);
            Method setHrHigh = Scbcrse.class.getDeclaredMethod("set" + hrType.getBannerName() + "HrHigh", paramBigDecimal);

            BigDecimal hrLow = (BigDecimal) getHrLow.invoke(runData.getKcv());
            String hrOpt = (String) getHrOpt.invoke(runData.getKcv());
            BigDecimal hrHigh = (BigDecimal) getHrHigh.invoke(runData.getKcv());

            String hrInd = convertToHrInd(runData, hrOpt, hrType.getKscmName());

            // hrHigh and hrOpt must be both null or both non-null
            if (hrHigh != null && hrInd == null) {
                runData.setStatus(Status.FAILURE);
                String msg = format("Convert: Error: %s hours High value was specified but not the OR/TO option", hrType.getKscmName());
                runData.addMessage(msg);
            } else if (hrHigh == null && hrInd != null) {
                runData.setStatus(Status.FAILURE);
                String msg = format("Convert: Error: %s hours OR/TO option was specified but not the High value", hrType.getKscmName());
                runData.addMessage(msg);
            }

            setHrLow.invoke(scbcrse, hrLow);
            setHrHigh.invoke(scbcrse, hrHigh);
            setHrInd.invoke(scbcrse, hrInd);

        } catch (Exception e) {
            runData.setStatus(Status.FAILURE);
            String msg = format("Convert: Exception occurred converting %s hours", hrType.getKscmName());
            runData.addMessage(msg, e);
        }
    }

    Scbdesc convertToScbdesc(RunData runData) {
        if (runData.getKcv().getDescription() != null) {
            Scbdesc scbdesc = new Scbdesc();
            scbdesc.setVpdiCode(runData.getInstCode());
            scbdesc.setSubjCode(runData.getSubjCode());
            scbdesc.setCrseNumb(runData.getCrseNumb());
            scbdesc.setTermCodeEff(runData.getEffTerm());
            scbdesc.setTermCodeEnd(null);
            scbdesc.setDataOrigin(runData.getDataOrigin());
            scbdesc.setActivityDate(runData.getActivityDate());
            scbdesc.setTextNarrative(runData.getKcv().getDescription());
            scbdesc.setUserId(runData.getUserId());
            return scbdesc;
        } else {
            runData.setStatus(Status.FAILURE);
            String msg = format("Convert: Error: KSCM course did not have 'description' field");
            runData.addMessage(msg);
            return null;
        }
    }

    Scrsyln convertToScrsyln(RunData runData) {
        // TODO: document concern regarding KSCM 'title' field not being in Banner Data Section
        if (runData.getKcv().getTitle() != null) {
            Scrsyln scrsyln = new Scrsyln();
            scrsyln.setVpdiCode(runData.getInstCode());
            scrsyln.setSubjCode(runData.getSubjCode());
            scrsyln.setCrseNumb(runData.getCrseNumb());
            scrsyln.setTermCodeEff(runData.getEffTerm());
            scrsyln.setTermCodeEnd(null);
            scrsyln.setDataOrigin(runData.getDataOrigin());
            scrsyln.setActivityDate(runData.getActivityDate());
            scrsyln.setLongCourseTitle(runData.getKcv().getTitle());
            scrsyln.setUserId(runData.getUserId());
            return scrsyln;
        } else {
            return null;
        }
    }


    List<Scrlevl> convertToListOfScrlevl(RunData runData) {
        List<Scrlevl> listOfScrlevl = new ArrayList<Scrlevl>();
        Map<String, Object> map = runData.getKcv().getBdeCourseLevels().getCourseLevels();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue().equals(true)) {
                Scrlevl scrlevl = new Scrlevl();
                scrlevl.setVpdiCode(runData.getInstCode());
                scrlevl.setSubjCode(runData.getSubjCode());
                scrlevl.setCrseNumb(runData.getCrseNumb());
                scrlevl.setEffTerm(runData.getEffTerm());
                scrlevl.setDataOrigin(runData.getDataOrigin());
                scrlevl.setActivityDate(runData.getActivityDate());
                scrlevl.setUserId(runData.getUserId());
                scrlevl.setLevlCode(entry.getKey().substring(4));
                listOfScrlevl.add(scrlevl);
            }
        }
        return listOfScrlevl;
    }

    List<Scrgmod> convertToListOfScrgmod(RunData runData) {
        List<Scrgmod> listOfScrgmod = new ArrayList<Scrgmod>();
        Map<String, Object> map = runData.getKcv().getBdeGradingOptions().getGradingOptions();
        String gradingOptionDef = runData.getKcv().getBdeGradingOptionDef();
        if (gradingOptionDef == null) {
            runData.setStatus(Status.FAILURE);
            String msg = "Convert: Error: Default grading mode was not set";
            runData.addMessage(msg);
            return null;
        }
        gradingOptionDef = gradingOptionDef.substring(4);
        boolean defaultWasSet = false;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue().equals(true)) {
                String gmodCode = entry.getKey();
                gmodCode = gmodCode.substring(gmodCode.length() - 1); // last character
                Scrgmod scrgmod = new Scrgmod();
                scrgmod.setVpdiCode(runData.getInstCode());
                scrgmod.setSubjCode(runData.getSubjCode());
                scrgmod.setCrseNumb(runData.getCrseNumb());
                scrgmod.setEffTerm(runData.getEffTerm());
                scrgmod.setDataOrigin(runData.getDataOrigin());
                scrgmod.setActivityDate(runData.getActivityDate());
                scrgmod.setUserId(runData.getUserId());
                scrgmod.setGmodCode(gmodCode);
                if (gmodCode.equals(gradingOptionDef)) {
                    scrgmod.setDefaultInd("D");
                    defaultWasSet = true;
                } else {
                    scrgmod.setDefaultInd("N");
                }
                listOfScrgmod.add(scrgmod);
            }
        }
        if (!defaultWasSet) {
            runData.setStatus(Status.FAILURE);
            String msg = format("Convert: Error: default grading mode was set to %s, but this is not among the selected options",
                    gradingOptionDef);
            runData.addMessage(msg);
            return listOfScrgmod;
        }
        return listOfScrgmod;
    }

    List<Scrschd> convertToListOfScrschd(RunData runData) {
        List<Scrschd> listOfScrschd = new ArrayList<Scrschd>();
        Map<String, Object> map = runData.getKcv().getBdeScheduleTypes().getScheduleTypes();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue().equals(true)) {
                Scrschd scrschd = new Scrschd();
                scrschd.setVpdiCode(runData.getInstCode());
                scrschd.setSubjCode(runData.getSubjCode());
                scrschd.setCrseNumb(runData.getCrseNumb());
                scrschd.setEffTerm(runData.getEffTerm());
                scrschd.setDataOrigin(runData.getDataOrigin());
                scrschd.setActivityDate(runData.getActivityDate());
                scrschd.setUserId(runData.getUserId());
                scrschd.setSchdCode(entry.getKey().substring(4));
                listOfScrschd.add(scrschd);
            }
        }
        return listOfScrschd;
    }

    List<Scrattr> convertToListOfScrattr(RunData runData) {
        List<Scrattr> listOfScrattr = new ArrayList<Scrattr>();
        Map<String, Object> map = runData.getKcv().getBdeDegreeAttributes().getDegreeAttributes();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue().equals(true)) {
                Scrattr scrattr = new Scrattr();
                scrattr.setVpdiCode(runData.getInstCode());
                scrattr.setSubjCode(runData.getSubjCode());
                scrattr.setCrseNumb(runData.getCrseNumb());
                scrattr.setEffTerm(runData.getEffTerm());
                scrattr.setDataOrigin(runData.getDataOrigin());
                scrattr.setActivityDate(runData.getActivityDate());
                scrattr.setUserId(runData.getUserId());
                scrattr.setAttrCode(entry.getKey().substring(4));
                listOfScrattr.add(scrattr);
            }
        }
        return listOfScrattr;
    }

    /**
     * @throws Exception if conversion failed to produce valid banner data model
     */
    public void convert(RunData runData) throws Exception {
        String msg = null;
        Scbcrse scbcrse = new Scbcrse();
        //try {
        KscmCourseVersion kcv = runData.getKcv();

        // vpdiCode
        scbcrse.setVpdiCode(kcv.getInstCode());

        // subjCode
        scbcrse.setSubjCode(convertToSubjCode(runData));
        runData.setSubjCode(scbcrse.getSubjCode());

        // crseNumb
        scbcrse.setCrseNumb(convertToCrseNumb(runData));
        runData.setCrseNumb(scbcrse.getCrseNumb());

        // effTerm
        scbcrse.setEffTerm(convertKcvStartTermToBannerTerm(runData));
        runData.setEffTerm(scbcrse.getEffTerm());

        // userId
        scbcrse.setUserId(runData.getUserId());

        // dataOrigin
        scbcrse.setDataOrigin(runData.getDataOrigin());

        // activityDate
        scbcrse.setActivityDate(runData.getActivityDate());

        // title
        scbcrse.setTitle(convertToTitle(runData));

        // collCode
        scbcrse.setCollCode(convertToCollCode(runData));

        // divsCode
        scbcrse.setDivsCode(convertToDivsCode(runData));

        // deptCode
        scbcrse.setDeptCode(convertToDeptCode(runData));

        // cstaCode
        scbcrse.setCstaCode(convertToCstaCode(runData));

        // aprvCode
        scbcrse.setAprvCode(convertToAprvCode(runData));

        // repeatLimit // kcv.getBdeRepeatLimit()
        scbcrse.setRepeatLimit(convertToRepeatLimit(runData));

        // maxRptUnits
        scbcrse.setMaxRptUnits(kcv.getBdeMaxCredits());

        // prereqChkMethodCde
        // We're not handling this yet. Setting default for new records.
        scbcrse.setPrereqChkMethodCde("B");

        // cappPrereqTestInd
        // Setting default for new records.
        scbcrse.setCappPrereqTestInd("N");

        // Skipping for now

        // Hours
        convertHr(scbcrse, runData, HrType.Credit);
        convertHr(scbcrse, runData, HrType.Billing);
        convertHr(scbcrse, runData, HrType.Lecture);
        convertHr(scbcrse, runData, HrType.Lab);
        convertHr(scbcrse, runData, HrType.Other);
        convertHr(scbcrse, runData, HrType.Contact);

        // Scbdesc
        scbcrse.setScbdesc(convertToScbdesc(runData));

        // Scrlevl
        scbcrse.setListOfScrlevl(convertToListOfScrlevl(runData));

        // Scrattr
        scbcrse.setListOfScrattr(convertToListOfScrattr(runData));

        // scrgmod
        scbcrse.setListOfScrgmod(convertToListOfScrgmod(runData));

        // Scrschd
        scbcrse.setListOfScrschd(convertToListOfScrschd(runData));

        // Scrsyln
        scbcrse.setScrsyln(convertToScrsyln(runData));

        runData.setConvertedScbcrse(scbcrse);

        //} catch (Exception e) {
        //    runData.setStatus(Status.FAILURE);
        //    msg = format("An exception occurred during conversion from KSCM course version data to Banner course version data for %s %s",
        //        runData
        //            .getHostPrefix(), runData
        //            .getKscmCourseVersionId());
        //    runData.addMessage(msg, e);
        //}
        if (runData.getStatus() == Status.FAILURE) {
            msg = format("Convert: Error: Was not able to convert KSCM data to equivalent Banner data for %s %s",
                    runData.getHostPrefix(), runData.getKscmCourseVersionId());
            throw new Exception(msg);
        }
    }
}
