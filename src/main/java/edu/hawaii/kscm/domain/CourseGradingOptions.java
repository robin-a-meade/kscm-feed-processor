package edu.hawaii.kscm.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class CourseGradingOptions {

    @JsonIgnore
    private Map<String, Object> gradingOptions = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getGradingOptions() {
        return this.gradingOptions;
    }

    @JsonAnySetter
    public void setGradingOption(String name, Object value) {
        this.gradingOptions.put(name, value);
    }

}
