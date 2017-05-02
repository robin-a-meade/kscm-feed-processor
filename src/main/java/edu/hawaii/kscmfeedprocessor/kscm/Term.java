package edu.hawaii.kscmfeedprocessor.kscm;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Term {

    private String year;

    private String type;

    private String termOptionId;

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

    public void setYear(Object year) {
        // convert to string
        this.year = year.toString();
    }

    public String getYear() {
        return year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTermOptionId() {
        return termOptionId;
    }

    public void setTermOptionId(String termOptionId) {
        this.termOptionId = termOptionId;
    }

}
