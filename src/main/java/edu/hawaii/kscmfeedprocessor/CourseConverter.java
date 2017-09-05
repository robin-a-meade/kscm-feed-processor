package edu.hawaii.kscmfeedprocessor;

import edu.hawaii.kscmfeedprocessor.banner.*;
import edu.hawaii.kscmfeedprocessor.kscm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
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
     * Convert kcv.subjectCode to corresponding Banner value. If can't, set runData.status to FAILED and add message to runData.
     */
    String convertToSubjCode(RunData runData) {
        String msg;
        KscmOptions kscmOptions = kscmService.getKscmOptionsFor(runData.getInstCode());

        String subjCode = null;
        // Check for natural key (We've found that newly created courses sometimes have natural key for subjectCode!)
        // A natural key would have length less than 7 and start with an uppercase character.
        String kcvSubjectCode = runData.getKcv().getSubjectCode();
        if (kcvSubjectCode.length() < 7 && Character.isUpperCase(kcvSubjectCode.charAt(0))) {
            msg = format("Convert: Detected natural key for subject code: %s", kcvSubjectCode);
            runData.addMessage(msg);
            subjCode = kcvSubjectCode;
        } else {
            try {
                SubjectCodeOption subjectCodeOption = kscmOptions.getSubjectCodesById().get(runData.getKcv().getSubjectCode());
                subjCode = subjectCodeOption.getName();
                msg = format("Convert: Retrieved subject code %s", subjCode);
                runData.addMessage(msg);
            } catch (Exception e) {
                // Set runData.status to FAILED
                // Add message, including stack trace
                // Swallow exception because we want to continue to collect any additional errors
                msg = format("Convert: Error: couldn't lookup subject code option for course version %s %s", runData.getHostPrefix(), runData
                        .getKscmCourseVersionId());
                runData.addMessage(msg, e);
                runData.setStatus(Status.FAILED);
            }
        }
        // TODO: additional validation checks here. Not null. Length <= 4.
        return subjCode;
    }

    /**
     * Convert kcv.number to corresponding Banner value. If can't, set runData.status to FAILED and add message to runData.
     */
    String convertToCrseNumb(RunData runData) {
        // TODO: add additional validation checks. Not null. Length <= 4.
        return runData.getKcv().getNumber();
    }

    /**
     * OBSOLETE TERMS 1.0 Convert KSCM term to Banner term. Helper function.
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
     * Convert KSCM term to Banner term. (New Terms 3.0 way)
     */
    String convertKscmDateLabelToBannerTerm(String dateLabel) throws Exception {
        //  TODO: add validation checks and throw exception if they fail
        String[] split = dateLabel.split(" ");
        if (split.length != 2) {
            throw new Exception("Unexpected date label: " + dateLabel);
        }
        String termName = split[0];
        String year = split[1];
        String twoDigitTermCode;
        switch (termName) {
            case "Fall":
                twoDigitTermCode = "10";
                break;
            case "Spring":
                twoDigitTermCode = "30";
                break;
            case "Summer":
                twoDigitTermCode = "40";
                break;
            default:
                throw new Exception("Unexpected date label: " + dateLabel);
        }
        if (twoDigitTermCode.startsWith("1")) {
            year = "" + (Integer.parseInt(year, 10) + 1);
        }
        return year + twoDigitTermCode;
    }


    /**
     * Obsolete Terms 1.0 way Convert kcv.startTerm to corresponding Banner value. If can't, set runData.status to FAILED and add message to runData.
     */
//    String convertKcvStartTermToBannerTerm(RunData runData) {
//        //  TODO: if can't, set runData.status to FAILED and add message to runData.
//        String bannerTerm = convertKscmTermToBannerTerm(runData.getKcv().getStartTerm());
//        return bannerTerm;
//    }

    String convertKcvDateStartLabelToBannerTerm(RunData runData) throws Exception {
        return convertKscmDateLabelToBannerTerm(runData.getKcv().getDateStartLabel());
    }


    /**
     * Convert kcv.bdeTranscriptTitle to corresponding Banner value. If can't, set runData.status to FAILED and add message to runData.
     */
    String convertToTitle(RunData runData) {
        // TODO: add additional validation checks. Not null. Length <= 30.
        return runData.getKcv().getBdeTranscriptTitle();
    }

    /**
     * Convert kcv.bdeCollCode to corresponding Banner value. If can't, set runData.status to FAILED and add message to runData.
     */
    String convertToCollCode(RunData runData) {
        // TODO: if can't, set runData.status to FAILED and add message to runData.
        String collCodeId = runData.getKcv().getBdeCollCode();
        return collCodeId.substring(4);
    }

    /**
     * Convert kcv.bdeDivsCode to corresponding Banner value. If can't, set runData.status to FAILED and add message to runData.
     */
    String convertToDivsCode(RunData runData) {
        // TODO: if can't, set runData.status to FAILED and add message to runData.
        String divsCodeId = runData.getKcv().getBdeDivsCode();
        return divsCodeId.substring(4);
    }

    /**
     * Convert kcv.bdeDeptCode to corresponding Banner value. If can't, set runData.status to FAILED and add message to runData.
     */
    String convertToDeptCode(RunData runData) {
        // TODO: if can't, set runData.status to FAILED and add message to runData.
        String deptCodeId = runData.getKcv().getBdeDeptCode();
        return deptCodeId.substring(4);
    }

    /**
     * Convert kcv.bdeCstaCode to corresponding Banner value. If can't, set runData.status to FAILED and add message to runData.
     */
    String convertToCstaCode(RunData runData) {
        // TODO: if can't, set runData.status to FAILED and add message to runData.
        String cstaCodeId = runData.getKcv().getBdeCstaCode();
        return cstaCodeId.substring(4);
    }

    /**
     * Convert kcv.bdeAprvCode to corresponding Banner value. If can't, set runData.status to FAILED, add message to runData, and return null;
     */
    String convertToAprvCode(RunData runData) throws Exception {
        String aprvCodeString = runData.getKcv().getBdeAprvCode();
        if (aprvCodeString == null) {
            return null;
        }
        switch (aprvCodeString) {
            case "experimental":
                return "E";
            case "approved":
                return "A";
            default:
                runData.setStatus(Status.FAILED);
                String msg = format("Convert: Unexpected value for bdeAprvCode: '%s'", aprvCodeString);
                runData.addMessage(msg);
                return null;
        }
    }

    /**
     * Convert kcv.bdeRepeatLimit to value needed by Banner value scbcrse.repeat_limit If can't, set runData.status to FAILED and add message to
     * runData.
     */
    BigDecimal convertToRepeatLimit(RunData runData) {
        // TODO: if can't, set runData.status to FAILED and add message to runData.
        BigDecimal repeatLimit = runData.getKcv().getBdeRepeatLimit();
        return repeatLimit;
    }

    /**
     * Convert kcv.bdeMaxCredits to value needed by Banner value scbcrse.MaxRptUnits. If can't, set runData.status to FAILED and add message to
     * runData.
     */
    BigDecimal convertToMaxRptUnits(RunData runData) {
        // TODO: if can't, set runData.status to FAILED and add message to runData.
        BigDecimal maxRptUnits = runData.getKcv().getBdeMaxCredits();
        return maxRptUnits;
    }

    String convertToHrInd(RunData runData, String opt, String type) throws Exception {
        if (opt == null) return null;
        switch (opt) {
            case "or":
                return "OR";
            case "to":
                return "TO";
            case "none":
                return null;
            default:
                runData.setStatus(Status.FAILED);
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

            // hrHigh and hrInd must be both null or both non-null
            if (hrHigh != null && hrInd == null) {
                runData.setStatus(Status.FAILED);
                String msg = format("Convert: Error: %s hours High value was specified but not the OR/TO option", hrType.getKscmName());
                runData.addMessage(msg);
            } else if (hrHigh == null && hrInd != null) {
                runData.setStatus(Status.FAILED);
                String msg = format("Convert: Error: %s hours OR/TO option was specified but not the High value", hrType.getKscmName());
                runData.addMessage(msg);
            }

            setHrLow.invoke(scbcrse, hrLow);
            setHrHigh.invoke(scbcrse, hrHigh);
            setHrInd.invoke(scbcrse, hrInd);

        } catch (Exception e) {
            runData.setStatus(Status.FAILED);
            String msg = format("Convert: Exception occurred converting %s hours", hrType.getKscmName());
            runData.addMessage(msg, e);
        }
    }

    Scbdesc convertToScbdesc(RunData runData) {
        if (runData.getKcv().getBdeDescription() != null) {
            Scbdesc scbdesc = new Scbdesc();
            scbdesc.setVpdiCode(runData.getInstCode());
            scbdesc.setSubjCode(runData.getSubjCode());
            scbdesc.setCrseNumb(runData.getCrseNumb());
            scbdesc.setTermCodeEff(runData.getEffTerm());
            scbdesc.setTermCodeEnd(null);
            scbdesc.setDataOrigin(runData.getDataOrigin());
            scbdesc.setActivityDate(runData.getActivityDate());
            scbdesc.setTextNarrative(runData.getKcv().getBdeDescription());
            scbdesc.setUserId(runData.getUserId());
            return scbdesc;
        } else {
            runData.setStatus(Status.FAILED);
            String msg = format("Convert: Error: KSCM course did not have 'description' field");
            runData.addMessage(msg);
            return null;
        }
    }

    Scrsyln convertToScrsyln(RunData runData) {
        // TODO: document concern regarding KSCM 'title' field not being in Banner Data Section
        if (runData.getKcv().getBdeLongTitle() != null || runData.getKcv().getBdeCourseUrl() != null) {
            Scrsyln scrsyln = new Scrsyln();
            scrsyln.setVpdiCode(runData.getInstCode());
            scrsyln.setSubjCode(runData.getSubjCode());
            scrsyln.setCrseNumb(runData.getCrseNumb());
            scrsyln.setTermCodeEff(runData.getEffTerm());
            scrsyln.setTermCodeEnd(null);
            scrsyln.setDataOrigin(runData.getDataOrigin());
            scrsyln.setActivityDate(runData.getActivityDate());
            scrsyln.setLongCourseTitle(runData.getKcv().getBdeLongTitle());
            scrsyln.setCourseUrl(runData.getKcv().getBdeCourseUrl());
            scrsyln.setUserId(runData.getUserId());
            return scrsyln;
        } else {
            return null;
        }
    }

    Scrsylo convertToScrsylo(RunData runData) {
        if (runData.getKcv().getBdeLearningObjectives() != null) {
            Scrsylo scrsylo = new Scrsylo();
            scrsylo.setVpdiCode(runData.getInstCode());
            scrsylo.setSubjCode(runData.getSubjCode());
            scrsylo.setCrseNumb(runData.getCrseNumb());
            scrsylo.setTermCodeEff(runData.getEffTerm());
            scrsylo.setTermCodeEnd(null);
            scrsylo.setDataOrigin(runData.getDataOrigin());
            scrsylo.setActivityDate(runData.getActivityDate());
            scrsylo.setLearningObjectives(runData.getKcv().getBdeLearningObjectives());
            scrsylo.setUserId(runData.getUserId());
            return scrsylo;
        } else {
            return null;
        }
    }

    Scbsupp convertToScbsupp(RunData runData) {
        if (runData.getKcv().getBdeInstRpt() != null) {
            Scbsupp scbsupp = new Scbsupp();
            scbsupp.setVpdiCode(runData.getInstCode());
            scbsupp.setSubjCode(runData.getSubjCode());
            scbsupp.setCrseNumb(runData.getCrseNumb());
            scbsupp.setEffTerm(runData.getEffTerm());
            scbsupp.setDataOrigin(runData.getDataOrigin());
            scbsupp.setActivityDate(runData.getActivityDate());
            scbsupp.setCudaCode(runData.getKcv().getBdeInstRpt().substring(4));
            scbsupp.setUserId(runData.getUserId());
            return scbsupp;
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
            runData.setStatus(Status.FAILED);
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
            runData.setStatus(Status.FAILED);
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
        if (runData.getKcv().getBdeDegreeAttributes() == null) {
            return listOfScrattr;
        }
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

    List<Scrintg> convertToListOfScrintg(RunData runData) {
        List<Scrintg> listOfScrintg = new ArrayList<Scrintg>();
        if (runData.getKcv().getBdeIntPartners() == null) {
            return listOfScrintg;
        }
        String[] list = runData.getKcv().getBdeIntPartners();
        for (String val : list) {
            Scrintg scrintg = new Scrintg();
            scrintg.setVpdiCode(runData.getInstCode());
            scrintg.setSubjCode(runData.getSubjCode());
            scrintg.setCrseNumb(runData.getCrseNumb());
            scrintg.setTermCodeEff(runData.getEffTerm());
            scrintg.setDataOrigin(runData.getDataOrigin());
            scrintg.setActivityDate(runData.getActivityDate());
            scrintg.setUserId(runData.getUserId());
            scrintg.setIntgCde(val.substring(4));
            listOfScrintg.add(scrintg);
        }
        return listOfScrintg;
    }

    List<Scrtext> convertToListOfScrtext(RunData runData) {
        List<Scrtext> listOfScrtext = new ArrayList<Scrtext>();
        List<CourseTextRow> listCourseTextRow = runData.getKcv().getBdeCourseText();
        // while (listCourseTextRow.remove(null));  // Remove nulls (This is now done in KscmCourseVersion.afterDeserialization)
        int seqno = 0;
        for (CourseTextRow courseTextRow : listCourseTextRow) {
            if (courseTextRow.getText() == null) continue; // must be an empty default row
            Scrtext scrtext = new Scrtext();
            scrtext.setVpdiCode(runData.getInstCode());
            scrtext.setSubjCode(runData.getSubjCode());
            scrtext.setCrseNumb(runData.getCrseNumb());
            scrtext.setEffTerm(runData.getEffTerm());
            scrtext.setDataOrigin(runData.getDataOrigin());
            scrtext.setActivityDate(runData.getActivityDate());
            scrtext.setUserId(runData.getUserId());
            scrtext.setTextCode("A");
            scrtext.setText(courseTextRow.getText());
            scrtext.setSeqno(++seqno);
            listOfScrtext.add(scrtext);
        }
        return listOfScrtext;
    }

    List<Scrfees> convertToListOfScrfees(RunData runData) {
        List<Scrfees> listOfScrfees = new ArrayList<Scrfees>();
        List<FeesRow> listFeesRow = runData.getKcv().getBdeFees();
        //while (listFeesRow.remove(null)); // Remove nulls (This is now done in KscmCourseVersion.afterDeserialization)
        int seqno = 0;
        for (FeesRow feesRow : listFeesRow) {
            if (feesRow.getDetlCode() == null) continue; // must be an empty default row
            Scrfees scrfees = new Scrfees();
            scrfees.setVpdiCode(runData.getInstCode());
            scrfees.setSubjCode(runData.getSubjCode());
            scrfees.setCrseNumb(runData.getCrseNumb());
            scrfees.setEffTerm(runData.getEffTerm());
            scrfees.setDataOrigin(runData.getDataOrigin());
            scrfees.setActivityDate(runData.getActivityDate());
            scrfees.setUserId(runData.getUserId());
            scrfees.setDetlCode(feesRow.getDetlCode());
            scrfees.setFeeAmount(new BigDecimal(feesRow.getFeeAmount()));
            scrfees.setFtypCode("FLAT"); // TODO: check if satisfactory. Do we need to support "CRED"?
            scrfees.setSeqno(++seqno);
            listOfScrfees.add(scrfees);
        }
        return listOfScrfees;
    }

    List<Screqiv> convertToListOfScreqiv(RunData runData) /* throws Exception */ {
        List<Screqiv> listOfScreqiv = new ArrayList<Screqiv>();
        List<EquivalentCoursesRow> listEquivalentCoursesRow = runData.getKcv().getBdeEquivalentCourses();
        // while (listEquivalentCoursesRow.remove(null));  // Remove nulls (This is now done in KscmCourseVersion.afterDeserialization)
        int seqno = 0;
        for (EquivalentCoursesRow equivalentCoursesRow : listEquivalentCoursesRow) {
            if (equivalentCoursesRow.getSubjNumb() == null) continue; // must be an empty default row
            Screqiv screqiv = new Screqiv();
            screqiv.setVpdiCode(runData.getInstCode());
            screqiv.setSubjCode(runData.getSubjCode());
            screqiv.setCrseNumb(runData.getCrseNumb());
            screqiv.setEffTerm(runData.getEffTerm());
            screqiv.setDataOrigin(runData.getDataOrigin());
            screqiv.setActivityDate(runData.getActivityDate());
            screqiv.setUserId(runData.getUserId());
            screqiv.setSubjCodeEqiv(equivalentCoursesRow.getSubjCodeEqiv());
            screqiv.setCrseNumbEqiv(equivalentCoursesRow.getCrseNumbEqiv());
            screqiv.setStartTerm(equivalentCoursesRow.getStartTerm());
            screqiv.setEndTerm(equivalentCoursesRow.getEndTerm());
            listOfScreqiv.add(screqiv);
        }
        return listOfScreqiv;
    }

    List<Scrcorq> convertToListOfScrcorq(RunData runData) throws Exception {
        List<Scrcorq> listOfScrcorq = new ArrayList<Scrcorq>();
        if (runData.getKcv().getBdeCorequisites() == null) {
            return listOfScrcorq;
        }
        String[] bdeCorequisites = runData.getKcv().getBdeCorequisites();
        int seqno = 0;
        String msg;
        KscmOptions kscmOptions = kscmService.getKscmOptionsFor(runData.getInstCode());
        KscmCourseVersion coreqKcv;
        for (String id : bdeCorequisites) {
            // Attempt to retrieve KSCM course data
            try {
                coreqKcv = kscmService.retrieveKscmCourseVersion(runData, id);
            } catch (Exception e) {
                runData.setStatus(Status.FAILED);
                msg = format("CourseConverter: Error: Couldn't retrieve Coreq KSCM course version %s %s", runData.getHostPrefix(), id);
                runData.addMessage(msg, e);
                // throw a new Exception with the context message and root cause
                throw new Exception(msg, e);
            }
            msg = format("CourseConverter: Retrieved Coreq KSCM course version %s %s", runData.getHostPrefix(), id);
            runData.addMessage(msg);

//            // Replaced with code to account for possiblity of natural key
//            // Attempt to convert subjectCodeOption to Banner subjCode
//            SubjectCodeOption subjectCodeOption = kscmOptions.getSubjectCodesById().get(coreqKcv.getSubjectCode());
//            if (subjectCodeOption == null) {
//                msg = format("CourseConverter: Error: couldn't find subjectCodeOption with id %s (for coreqId: %s)", coreqKcv.getSubjectCode(), id);
//                runData.addMessage(msg);
//                runData.setStatus(Status.FAILED);
//                throw new Exception(msg);
//            }
//            String subjCode = subjectCodeOption.getName();

            String subjCode = null;
            // Check for natural key (We've found that newly created courses sometimes have natural key for subjectCode!)
            // A natural key would have length less than 7 and start with an uppercase character.
            //String kcvSubjectCode = runData.getKcv().getSubjectCode();
            String kcvSubjectCode = coreqKcv.getSubjectCode();
            if (kcvSubjectCode.length() < 7 && Character.isUpperCase(kcvSubjectCode.charAt(0))) {
                msg = format("Convert: Detected natural key for Coreq subject code: %s", kcvSubjectCode);
                runData.addMessage(msg);
                subjCode = kcvSubjectCode;
            } else {
                try {
                    SubjectCodeOption subjectCodeOption = kscmOptions.getSubjectCodesById().get(kcvSubjectCode);
                    subjCode = subjectCodeOption.getName();
                    msg = format("Convert: Retrieved subject code %s", subjCode);
                    runData.addMessage(msg);
                } catch (Exception e) {
                    // Set runData.status to FAILED
                    // Add message, including stack trace
                    // Swallow exception because we want to continue to collect any additional errors
                    msg = format("Convert: Error: couldn't lookup subject code option for course version %s %s", runData.getHostPrefix(), runData
                            .getKscmCourseVersionId());
                    runData.addMessage(msg, e);
                    runData.setStatus(Status.FAILED);
                }
            }

            // Get crseNumb
            String crseNumb = coreqKcv.getNumber();

            Scrcorq scrcorq = new Scrcorq();
            scrcorq.setVpdiCode(runData.getInstCode());
            scrcorq.setSubjCode(runData.getSubjCode());
            scrcorq.setCrseNumb(runData.getCrseNumb());
            scrcorq.setEffTerm(runData.getEffTerm());
            scrcorq.setDataOrigin(runData.getDataOrigin());
            scrcorq.setActivityDate(runData.getActivityDate());
            scrcorq.setUserId(runData.getUserId());
            scrcorq.setSubjCodeCorq(subjCode);
            scrcorq.setCrseNumbCorq(crseNumb);
            listOfScrcorq.add(scrcorq);
        }
        return listOfScrcorq;
    }

    /**
     * @throws Exception if conversion failed to produce valid banner data model
     */
    public void convert(RunData runData) throws Exception {
        String msg = null;
        Scbcrse scbcrse = new Scbcrse();

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
        scbcrse.setEffTerm(convertKcvDateStartLabelToBannerTerm(runData));

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

        // Scrsylo
        scbcrse.setScrsylo(convertToScrsylo(runData));

        // Scbsupp
        scbcrse.setScbsupp(convertToScbsupp(runData));

        // Scrintg
        scbcrse.setListOfScrintg(convertToListOfScrintg(runData));

        // Scrtext
        scbcrse.setListOfScrtext(convertToListOfScrtext(runData));

        // Scrfees
        scbcrse.setListOfScrfees(convertToListOfScrfees(runData));

        // Screqiv
        scbcrse.setListOfScreqiv(convertToListOfScreqiv(runData));

        // Scrcorq
        scbcrse.setListOfScrcorq(convertToListOfScrcorq(runData));

        runData.setConvertedScbcrse(scbcrse);

        if (runData.getStatus() == Status.FAILED) {
            msg = format("Convert: Error: Was not able to convert KSCM data to equivalent Banner data for %s %s",
                    runData.getHostPrefix(), runData.getKscmCourseVersionId());
            throw new Exception(msg);
        }
    }
}
