package edu.hawaii.kscm.domain;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

public class CourseLevels {

    @JsonIgnore
    private Map<String, Object> courseLevels = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getCourseLevels() {
        return this.courseLevels;
    }

    @JsonAnySetter
    public void setCourseLevel(String name, Object value) {
        this.courseLevels.put(name, value);
    }

}
