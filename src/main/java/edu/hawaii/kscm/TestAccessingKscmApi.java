package edu.hawaii.kscm;

import edu.hawaii.kscm.domain.Course;
import edu.hawaii.kscm.domain.SubjectCodeOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/*
options.get("WOA").subjectCodeOptionsByCode.get();
options.get("WOA").subjectCodeOptionsById.get();
*/



/*
$ kscm GET westoahu stg /cm/courses/ACC999_201810 | jq '{
id: .id,
title: .title,
subjectCode: .subjectCode,
number: .number,
startTerm: .startTerm,
bannerIntegrationFlag: .bannerIntegrationFlag
}'
{
"id": "ACC999_201810",
"title": "Map Test: credit opt: fixed",
"subjectCode": "920b5909-96f7-4b42-97ab-2a654a1bcd80",
"number": "999",
"startTerm": {
"year": 2017,
"type": "term",
"termOptionId": "term10"
},
"bannerIntegrationFlag": true
}
*/
public class TestAccessingKscmApi {

    private Client client;

    private Map<String, SubjectCodeOption> subjectCodeOptionsByCode = new HashMap<>();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    String apikey =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjU3OTkzYzY2MzExNGMwYjgwNTAxMmNiNiIsImlzcyI6Imt1YWxpLmNvIiwiZXhwIjoxNTAxMTk2MjYxLCJpYXQiOjE0Njk2NjAyNjJ9.HNSRAUfWzZ8f5Fm2bSxnnmANUyknBOWk_dIsvE80srQ";

    String authorizationHeader = "Authorization: Bearer $apikey";

    void getSubjectCodeOptionsByCode() {
        WebTarget target = client.target(
                "https://westoahu-stg.kuali.co/api/cm/options/types/subjectcodes");

        Invocation invocation = target.request(MediaType.APPLICATION_JSON).header(HttpHeaders
                .AUTHORIZATION, "Bearer " + apikey).buildGet();

        SubjectCodeOption[] s = invocation.invoke(SubjectCodeOption[].class);
        System.out.println(s.length);
        System.out.println(s[0].getName());
        System.out.println(s[0].getDescription());
        int count = 0;
        int countActive = 0;
        for(SubjectCodeOption subjectCodeOption : s) {
            ++count;
            if (subjectCodeOption.getActive()) {
                ++countActive;
                subjectCodeOptionsByCode.put(subjectCodeOption.getName(), subjectCodeOption);
            }
        }
        System.out.println("There were " + count + " subject code options.");
        System.out.println("There were " + countActive + " ACTIVE subject code options.");
        System.out.println("MATH: " + subjectCodeOptionsByCode.get("MATH"));
    }

    void run() {
        client = ClientBuilder.newClient();

        WebTarget target = client.target(
                "https://westoahu-stg.kuali.co/api/cm/courses/ACC999_201810");

        Invocation invocation = target.request(MediaType.APPLICATION_JSON).header(HttpHeaders
                .AUTHORIZATION, "Bearer " + apikey).buildGet();

        Course c = invocation.invoke(Course.class);
        System.out.println("SubjectCode: " + c.getSubjectCode());
        System.out.println("Number: " + c.getNumber());
        System.out.println("bannerIntegrationFlag: " + c.getBannerIntegrationFlag());

        getSubjectCodeOptionsByCode();
    }

    static public void main(String[] args) {
        new TestAccessingKscmApi().run();
    }

}
