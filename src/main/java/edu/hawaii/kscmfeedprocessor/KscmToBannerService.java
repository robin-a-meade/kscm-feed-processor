package edu.hawaii.kscmfeedprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static java.lang.String.format;

@Repository
public class KscmToBannerService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    KscmService kscmService;

    @Autowired
    CourseConverter courseConverter;

    @Autowired
    BannerUpdaterService bannerUpdaterService;

    public void handleRequest(RunData runData) {
        String msg = null;
        msg = format("KscmToBannerService: Received request to process KSCM course version %s %s", runData.getHostPrefix(), runData
                .getKscmCourseVersionId());
        runData.addMessage(msg);

        // Attempt to retrieve KSCM course data
        try {
            runData.setKcv(kscmService.retrieveKscmCourseVersion(runData, runData.getKscmCourseVersionId()));
        } catch (Exception e) {
            runData.setStatus(Status.FAILED);
            msg = format("KscmToBannerService: Error: Couldn't retrieve KSCM course version %s %s", runData.getHostPrefix(), runData.getKscmCourseVersionId());
            runData.addMessage(msg, e);
            return;
        }
        msg = format("KscmToBannerService: Retrieved KSCM course version %s %s", runData.getHostPrefix(), runData.getKscmCourseVersionId());
        runData.addMessage(msg);

        // Attempt to retrieve KSCM user
        String updatedBy = runData.getKcv().getUpdatedBy();
        try {
            runData.setKscmUser(kscmService.retrieveKscmUser(runData));
        } catch (Exception e) {
            runData.setStatus(Status.FAILED);
            msg = format("KscmToBannerService: Error: Couldn't retrieve KSCM user %s at %s for course id %s", updatedBy, runData.getHostPrefix(), runData
                    .getKscmCourseVersionId());
            runData.addMessage(msg, e);
            return;
        }
        runData.setUserId(runData.getKscmUser().getUsername().toUpperCase() + "(" + runData.getDataOrigin() + ")");
        msg = format("KscmToBannerService: Retrieved KSCM user %s at %s for course id %s", runData.getUserId(), runData.getHostPrefix(), runData
                .getKscmCourseVersionId());
        runData.addMessage(msg);

        // Attempt to convert KSCM data model to Banner data model
        try {
            courseConverter.convert(runData);
        } catch (Exception e) {
            runData.setStatus(Status.FAILED);
            msg = format("KscmToBannerService: Error: The data conversion step failed for %s %s ", runData.getHostPrefix(), runData.getKscmCourseVersionId());
            runData.addMessage(msg, e);
            runData.addMessage(Util.summarizeConverted(runData.getConvertedScbcrse()));
            return;
        }

        msg = format("KscmToBannerService: Converted KSCM course data model to Banner data model for %s %s %s %s",
                runData.getInstCode(), runData.getSubjCode(), runData.getCrseNumb(), runData.getEffTerm());
        runData.addMessage(msg);
        logger.trace("scbcrse: {}", runData.getConvertedScbcrse().toJson());
        runData.addMessage(Util.summarizeConverted(runData.getConvertedScbcrse()));

        // Attempt to apply to Banner
        try {
            bannerUpdaterService.updateBanner(runData); // Transactional
        } catch (Exception e) {
            runData.setStatus(Status.FAILED);
            msg = format("KscmToBannerService: Error: An exception occurred while applying the data to Banner database for %s %s %s %s. Banner database transacton " +
                            "was rolled back.",
                    runData.getInstCode(), runData.getSubjCode(), runData.getCrseNumb(), runData.getEffTerm());
            runData.addMessage(msg, e);
            return;
        }

        return;

    }

}
