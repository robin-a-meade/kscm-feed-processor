package edu.hawaii.kscmfeedprocessor.kscm;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: don't we need fee type?
 */
public class FeesRow {

    // detlCode is column "0"

    @JsonProperty("0")
    private String detlCode;

    @JsonProperty("0")
    public String getDetlCode() {
        return detlCode;
    }

    @JsonProperty("0")
    public void setDetlCode(String detlCode) {
        this.detlCode = detlCode;
    }

    // feeAmount is column "1"

    @JsonProperty("1")
    private String feeAmount;

    @JsonProperty("1")
    public String getFeeAmount() {
        return feeAmount;
    }

    @JsonProperty("1")
    public void setFeeAmount(String feeAmount) {
        this.feeAmount = feeAmount;
    }

}
