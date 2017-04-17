package edu.hawaii.kscm.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class CourseDegreeAttributes {

    @JsonIgnore
    private Map<String, Object> degreeAttributes = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getDegreeAttributes() {
        return this.degreeAttributes;
    }

    @JsonAnySetter
    public void setDegreeAttribute(String name, Object value) {
        this.degreeAttributes.put(name, value);
    }

}
