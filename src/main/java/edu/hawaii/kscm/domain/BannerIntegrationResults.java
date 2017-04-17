package edu.hawaii.kscm.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BannerIntegrationResults {

    public BannerIntegrationResults(String results) {
        this.bannerIntegrationFlag = false;
        this.bannerIntegrationResults = results;
        // Account for mispellings at Manoa:
        this.bannerintegrationFlag = false;
        this.bannerintegrationResults = results;
    }

    @JsonProperty("bannerIntegrationFlag")
    public Boolean bannerIntegrationFlag;

    @JsonProperty("bannerIntegrationResults")
    public String bannerIntegrationResults;

    @JsonProperty("bannerIntegrationFlag")
    public Boolean getBannerIntegrationFlag() {
        return bannerIntegrationFlag;
    }

    @JsonProperty("bannerIntegrationResults")
    public String getBannerIntegrationResults() {
        return bannerIntegrationResults;
    }

    /***************************************************************************
     *                     Account for mispellings at Manoa
     ***************************************************************************/

    @JsonProperty("bannerintegrationFlag")
    public Boolean bannerintegrationFlag;

    @JsonProperty("bannerintegrationResults")
    public String bannerintegrationResults;

    @JsonProperty("bannerintegrationFlag")
    public Boolean getBannerintegrationFlag() {
        return bannerintegrationFlag;
    }

    @JsonProperty("bannerintegrationResults")
    public String getBannerintegrationResults() {
        return bannerintegrationResults;
    }

}
