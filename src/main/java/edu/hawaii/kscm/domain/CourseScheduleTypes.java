package edu.hawaii.kscm.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class CourseScheduleTypes {

    @JsonIgnore
    private Map<String, Object> scheduleTypes = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getScheduleTypes() {
        return this.scheduleTypes;
    }

    @JsonAnySetter
    public void setScheduleType(String name, Object value) {
        this.scheduleTypes.put(name, value);
    }

}
