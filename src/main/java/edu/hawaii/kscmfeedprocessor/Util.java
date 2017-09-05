package edu.hawaii.kscmfeedprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Throwables;
import edu.hawaii.kscmfeedprocessor.banner.*;
import org.apache.commons.lang3.StringEscapeUtils;
import static java.lang.String.format;
import static java.lang.String.join;
import java.util.ArrayList;
import java.util.List;

public class Util {

    public static String toJson(Object object) {
        String json = null;
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            json = ow.writeValueAsString(object);
        } catch (Exception e) {
            json = "Exception occurred while converting to JSON: " + Throwables.getStackTraceAsString(e);
        }
        return json;
    }

    /**
     * Converts a string into a double-quoted Java string.
     *
     * Returns "null" (unquoted) if input string is null.
     *
     * Optionally ellipsizes (if sizeLimit > 0).
     *
     * Known issue: the returned string length isn't guaranteed to be <= sizeLimit because we truncat before the
     * escape step. This approach was taken to avoid possibility of a partial escape sequence at end of the string.
     */
    public static String javaQuote(String s, int sizeLimit) {
        if (s == null) {
            return "null";
        }
        boolean addEllipsis = false;
        if (sizeLimit > 0 && s.length() > sizeLimit) {
            s = s.substring(0, sizeLimit - 1);
            addEllipsis = true;
        }
        s = StringEscapeUtils.escapeJava(s);
        if (addEllipsis) return "\"" + s + "…\"";
        else return "\"" + s + "\"";
    }

    public static String summarizeConverted(Scbcrse c) {
        StringBuilder sb = new StringBuilder();
        sb.append("Summary of converted data that we're about to apply to banner:\n");
        if (c == null) {
            sb.append("  null");
            return sb.toString();
        }
        sb.append(format("   %s %s %s %s %s\n", c.getVpdiCode(), c.getSubjCode(), c.getCrseNumb(), c.getEffTerm(), javaQuote(c.getTitle(), 0)));
        // scbdesc
        String description = null;
        if (c.getScbdesc() != null) {
            description = c.getScbdesc().getTextNarrative();
        }
        sb.append(format("   %s: %s\n", "Description", javaQuote(description, 65)));
        sb.append(format("   Coll:%s, Divs:%s, Dept:%s, Csta:%s, Aprv:%s\n", c.getCollCode(), c.getDivsCode(), c.getDeptCode(), c.getCstaCode(),
                c.getAprvCode()));
        sb.append(format("   RepeatLimit:%s, MaxRptUnits:%s\n", c.getRepeatLimit(), c.getMaxRptUnits()));
        sb.append(format("   CreditHrs (low/ind/high): %s %s %s\n", c.getCreditHrLow(), c.getCreditHrInd(), c.getCreditHrHigh()));
        sb.append(format("   BillHrs (low/ind/high): %s %s %s\n", c.getBillHrLow(), c.getBillHrInd(), c.getBillHrHigh()));
        sb.append(format("   LecHrs (low/ind/high): %s %s %s\n", c.getLecHrLow(), c.getLecHrInd(), c.getLecHrHigh()));
        sb.append(format("   LabHrs (low/ind/high): %s %s %s\n", c.getLabHrLow(), c.getLabHrInd(), c.getLabHrHigh()));
        sb.append(format("   OthHrs (low/ind/high): %s %s %s\n", c.getOthHrLow(), c.getOthHrInd(), c.getOthHrHigh()));
        sb.append(format("   ContHrs (low/ind/high): %s %s %s\n", c.getContHrLow(), c.getContHrInd(), c.getContHrHigh()));
        // scrlevl
        List<String> levlCodes = new ArrayList<>();
        for (Scrlevl scrlevl : c.getListOfScrlevl()) {
            levlCodes.add(scrlevl.getLevlCode());
        }
        sb.append(format("   LevlCodes: %s\n", join(", ", levlCodes)));
        // scrattr
        List<String> attrCodes = new ArrayList<>();
        for (Scrattr scrattr : c.getListOfScrattr()) {
            attrCodes.add(scrattr.getAttrCode());
        }
        sb.append(format("   AttrCodes: %s\n", join(", ", attrCodes)));
        // scrgmod
        List<String> gmodCodes = new ArrayList<>();
        for (Scrgmod scrgmod : c.getListOfScrgmod()) {
            String def= "";
            if (scrgmod.getDefaultInd().equals("D")) {
                def = " (default)";
            }
            gmodCodes.add(scrgmod.getGmodCode() + def);
        }
        sb.append(format("   GmodCodes: %s\n", join(", ", gmodCodes)));
        // scrschd
        List<String> schdCodes = new ArrayList<>();
        for (Scrschd scrschd : c.getListOfScrschd()) {
            schdCodes.add(scrschd.getSchdCode());
        }
        sb.append(format("   SchdCodes: %s\n", join(", ", schdCodes)));

        // scrsyln
        if (c.getScrsyln() != null) {
            String longCourseTitle = null;
            String courseUrl = null;
            longCourseTitle = c.getScrsyln().getLongCourseTitle();
            courseUrl = c.getScrsyln().getCourseUrl();
            if (longCourseTitle != null) {
                sb.append(format("   %s: %s\n", "LongCourseTitle", javaQuote(longCourseTitle, 65)));
            }
            if (courseUrl != null) {
                sb.append(format("   %s: %s\n", "CourseUrl", javaQuote(courseUrl, 65)));
            }
        }

        // scbsupp
        if (c.getScbsupp() != null) {
            String cudaCode = c.getScbsupp().getCudaCode();
            if (cudaCode != null) {
                sb.append(format("   %s: %s\n", "Institutional Reporting Code (SCBSUPP_CUDA)", javaQuote(cudaCode, 60)));
            }
        }
        // scrintg
        List<String> intgCodes = new ArrayList<>();
        for (Scrintg scrintg : c.getListOfScrintg()) {
            intgCodes.add(scrintg.getIntgCde());
        }
        sb.append(format("   Integration Partner Codes (SCRINTG): %s\n", join(", ", intgCodes)));

        // scrtext
        sb.append(format("   Course Text (SCRTEXT): %d rows\n", c.getListOfScrtext().size()));
        if (c.getListOfScrtext().size() > 0) {
            int index = 0;
            for (Scrtext scrtext : c.getListOfScrtext()) {
                index++;
                sb.append(format("      %d: %s\n", index, scrtext.getText()));
            }
        }

        // scrfees
        List<String> fees = new ArrayList<>();
        for (Scrfees scrfees : c.getListOfScrfees()) {
            fees.add(scrfees.getDetlCode() + ":" + scrfees.getFeeAmount());
        }
        sb.append(format("   Fees (SCRFEES): %s\n", fees.size() == 0 ? "(none)" : join(", ", fees)));

        // screqiv
        List<String> eqiv = new ArrayList<>();
        for (Screqiv screqiv : c.getListOfScreqiv()) {
            eqiv.add(screqiv.getSubjCodeEqiv() + " " + screqiv.getCrseNumbEqiv() + " " + screqiv.getStartTerm() + "-" + screqiv.getEndTerm());
        }
        sb.append(format("   Equiv. Courses (SCREQIV): %s\n", eqiv.size() == 0 ? "(none)" : join(", ", eqiv)));

        // scrcorq
        List<String> corq = new ArrayList<>();
        for (Scrcorq scrcorq : c.getListOfScrcorq()) {
            corq.add(scrcorq.getSubjCodeCorq() + " " + scrcorq.getCrseNumbCorq());
        }
        sb.append(format("   Corequisites (SCRCORQ): %s\n", corq.size() == 0 ? "(none)" : join(", ", corq)));

        return sb.toString();
    }

}
