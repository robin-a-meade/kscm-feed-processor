package edu.hawaii.kscmfeedprocessor.kscm;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class GradingOptions {

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
