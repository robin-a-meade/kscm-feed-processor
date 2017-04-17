package edu.hawaii.kscm;

import edu.hawaii.kscm.KscmService;
import edu.hawaii.kscm.domain.SubjectCodeOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.client.Invocation;
import java.util.HashMap;
import java.util.Map;

public class KscmOptions {
    private final Logger logger;

    private String instCode;
    private KscmService kscmService;

    Map<String, SubjectCodeOption> subjectCodesById = new HashMap<>();
    public Map<String, SubjectCodeOption> getSubjectCodesById() {
        return subjectCodesById;
    }

    Map<String, SubjectCodeOption> subjectCodesByCode = new HashMap<>();;
    public Map<String, SubjectCodeOption> getSubjectCodesByCode() {
        return subjectCodesByCode;
    }

    public KscmOptions(KscmService kscmService, String instCode) {
        this.logger = LoggerFactory.getLogger("KscmOptions/" + instCode);
        this.kscmService = kscmService;
        this.instCode = instCode;
        init();
    }

    public void initSubjectCodesById () {
        logger.info("{} initSubjectCodesById", this.instCode);
        String path = "/cm/options/types/subjectcodes";
        Invocation.Builder ib = kscmService.getInvocationBuilder(instCode, path);
        Invocation get = ib.buildGet();
        SubjectCodeOption[] s = get.invoke(SubjectCodeOption[].class);
        //logger.info("{} length: {}", this.instCode, s.length);
        //logger.info("{} 1st: {} {}", this.instCode, s[0].getName(), s[0].getDescription());
        //logger.info("{} 2nd: {} {}", this.instCode, s[1].getName(), s[1].getDescription());

        int count = 0;
        int countActive = 0;
        for(SubjectCodeOption subjectCodeOption : s) {
            ++count;
            if (subjectCodeOption.getActive()) {
                ++countActive;
                subjectCodesById.put(subjectCodeOption.getId(), subjectCodeOption);
            }
        }
        logger.info("{} count: {}", this.instCode, count);
        logger.info("{} active: {}", this.instCode, countActive);
    }

    private void initSubjectCodesByCode() {
        logger.info("{} initSubjectCodesByCode", this.instCode);
        int count = 0;
        int countActive = 0;
        for(SubjectCodeOption subjectCodeOption : subjectCodesById.values()) {
            ++count;
            if (subjectCodeOption.getActive()) {
                ++countActive;
                subjectCodesByCode.put(subjectCodeOption.getName(), subjectCodeOption);
            }
        }
        logger.info("{} count: {}", this.instCode, count);
        logger.info("{} active: {}", this.instCode, countActive);
    }

    public void init() {
        logger.info("{} KscmOptions.init()", this.instCode);
        initSubjectCodesById();
        initSubjectCodesByCode();
    }

}
