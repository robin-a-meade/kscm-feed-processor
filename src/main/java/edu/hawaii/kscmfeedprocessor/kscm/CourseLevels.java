package edu.hawaii.kscmfeedprocessor.kscm;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
