package edu.hawaii.kscmfeedprocessor.kscm;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class CourseTextRow {

    @JsonProperty("0")
    private String text;

    @JsonProperty("0")
    public String getText() {
        return text;
    }

    @JsonProperty("0")
    public void setText(String text) {
        this.text = text;
    }

}
