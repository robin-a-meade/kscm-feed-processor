package edu.hawaii.kscmfeedprocessor.kscm;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: don't we need fee type?
 */
public class EquivalentCoursesRow {

    static private Pattern subjNumbPattern = Pattern.compile("([a-zA-Z]+)(.*)");

    // subjNumb is column "0"
    @JsonProperty("0")
    private String subjNumb;

    @JsonProperty("0")
    public String getSubjNumb() {
        return subjNumb;
    }

    @JsonProperty("0")
    public void setSubjNumb(String subjNumb) {
        this.subjNumb = subjNumb;
    }

    // startTerm (SCREQIV_START_TERM) is column "1"
    @JsonProperty("1")
    private String startTerm;

    @JsonProperty("1")
    public String getStartTerm() {
        return startTerm;
    }

    @JsonProperty("1")
    public void setStartTerm(String startTerm) {
        this.startTerm = startTerm;
    }

    // endTerm (SCREQIV_END_TERM) is column "2"
    @JsonProperty("2")
    private String endTerm;

    @JsonProperty("2")
    public String getEndTerm() {
        return endTerm;
    }

    @JsonProperty("2")
    public void setEndTerm(String endTerm) {
        this.endTerm = endTerm;
    }

    public void parseSubjNumb() throws Exception {
        Matcher m = subjNumbPattern.matcher(subjNumb);
        if (m.find()) {
            subjCodeEqiv = m.group(1);
            crseNumbEqiv = m.group(2);
        } else {
            throw new Exception("Couldn't parse '" + subjNumb + " into Subject and Course Number");
        }
    }

    private String subjCodeEqiv;
    private String crseNumbEqiv;

    /**
     * Be sure to call parseSubjNumb first
     */
    public String getSubjCodeEqiv() {
        if (subjCodeEqiv == null) throw new RuntimeException("parseSubjNumb must be called first");
        return subjCodeEqiv;
    }

    public String getCrseNumbEqiv() {
        if (crseNumbEqiv == null) throw new RuntimeException("parseSubjNumb must be called first");
        return crseNumbEqiv;
    }
}
