package edu.hawaii.kscmfeedprocessor;

import edu.hawaii.kscmfeedprocessor.banner.*;
import edu.hawaii.kscmfeedprocessor.banner.vpdi.MultiUseContext;
import edu.hawaii.kscmfeedprocessor.banner.vpdi.VpdiContextManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.lang.String.join;

@Repository
public class BannerUpdaterService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "bannerJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;

    @Resource(name = "bannerDataSource")
    private DataSource dataSource;

    @Autowired
    private ScbcrkyDao scbcrkyDao;

    @Autowired
    private ScbcrseDao scbcrseDao;

    @Autowired
    private ScbdescDao scbdescDao;

    @Autowired
    private ScrsylnDao scrsylnDao;

    @Autowired
    private ScrlevlDao scrlevlDao;

    @Autowired
    private ScrgmodDao scrgmodDao;

    @Autowired
    private ScrschdDao scrschdDao;

    @Autowired
    private ScrattrDao scrattrDao;

    /**
     * Verify that the VPDI context has been set.
     */
    private void verifyVpdiContext(String instCode) throws Exception {
        List<String> listOfVpdiCodes = jdbcTemplate.query(
                "SELECT SCBCRKY_VPDI_CODE FROM SCBCRKY WHERE SCBCRKY_SUBJ_CODE = 'ENG' AND SCBCRKY_CRSE_NUMB = '100'",
                new RowMapper<String>() {
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString(1);
                    }
                });
        if (listOfVpdiCodes.size() == 1 && !listOfVpdiCodes.get(0).equals(instCode)) {
            throw new Exception(format("BannerUpdater: Error: VPDI context not set to '%s'. Was: '%s'", instCode, listOfVpdiCodes.get(0)));
        } else if (listOfVpdiCodes.size() > 1) {
            throw new Exception(format("BannerUpdater: Error: VPDI context not set to '%s'. Was: '%s'", instCode));
        } else if (listOfVpdiCodes.size() == 0) {
            throw new Exception(format("BannerUpdater: Error: Could not verify VPDI context for '%s'", instCode));
        }
    }

    private VpdiContextManager getVpdiContextManager() {
        return VpdiContextManager.get(dataSource);
    }

    /**
     * Throws an exception if not successful. The exception will cause the transaction to rollback.
     * In such case, the logs will show messages like this for logger <code>o.s.t.i.TransactionInterceptor</code>:
     * <ul>
     * <li>Completing transaction for [edu.hawaii.kscmfeedprocessor.BannerUpdaterService.bannerUpdater] after exception: java.lang.RuntimeException
     * <li>Applying rules to determine whether transaction should rollback on java.lang.RuntimeException...
     * <li>Initiating transaction rollback
     * <li>Rolling back JDBC transaction on Connection [com.sun.proxy.$Proxy77@64f89238]
     * </ul>
     */
    @Transactional
    public void updateBanner(RunData runData) throws Exception {
        String msg;
        Scbcrse convertedScbcrse = runData.getConvertedScbcrse();
        int recCount;
        String instCode = runData.getInstCode();
        String subjCode = convertedScbcrse.getSubjCode();
        String crseNumb = convertedScbcrse.getCrseNumb();
        String effTerm = convertedScbcrse.getEffTerm();

        try (AutoCloseable c = getVpdiContextManager().push(MultiUseContext.OVERRIDE, instCode)) {

            // Attempt to verify VPDI context
            verifyVpdiContext(instCode);
            runData.addMessage(format("BannerUpdater: VPDI context was verified for %s in database '%s'", instCode, databaseName));

            //  Attempt to retrieve master course record
            Scbcrky scbcrky = scbcrkyDao.get(subjCode, crseNumb);

            if (scbcrky == null) {

                // New course.  Do inserts for everything

                runData.addMessage(format("BannerUpdater: No SCBCRKY record yet exists for %s %s %s", instCode,
                        subjCode,
                        crseNumb));

                // Scbcrky
                recCount = scbcrkyDao.insert(subjCode, crseNumb, effTerm, runData.getUserId(), runData.getDataOrigin());
                runData.addMessage(format("BannerUpdater: Inserted %d new SCBCRKY record: %s:%s:%s:%s:999999.", recCount,
                        instCode, subjCode, crseNumb, effTerm));

                // Scbcrse
                recCount = scbcrseDao.insert(convertedScbcrse);
                runData.addMessage(format("BannerUpdater: Inserted %d SCBCRSE record: %s:%s:%s:%s:\"%s\"", recCount,
                        instCode, subjCode, crseNumb, effTerm, convertedScbcrse.getTitle()));

                // Scbdesc
                Scbdesc scbdesc = convertedScbcrse.getScbdesc();
                if (scbdesc != null) {
                    recCount = scbdescDao.insert(scbdesc);
                    runData.addMessage(format("BannerUpdater: Inserted %d SCBDESC record: %s:%s:%s:%s:%s", recCount,
                            instCode, subjCode, crseNumb, effTerm, Util.javaQuote(scbdesc.getTextNarrative(), 40)));
                }

                // Scrsyln
                Scrsyln scrsyln = convertedScbcrse.getScrsyln();
                if (scrsyln != null) {
                    recCount = scrsylnDao.insert(scrsyln);
                    runData.addMessage(format("BannerUpdater: Inserted %d SCRSYLN record: %s:%s:%s:%s:%s", recCount,
                            instCode, subjCode, crseNumb, effTerm, Util.javaQuote(scrsyln.getLongCourseTitle(), 40)));
                }

                // Scrlevl
                int levlCodesCount = 0;
                StringBuilder levlCodesAsString = new StringBuilder();
                for (Scrlevl scrlevl : convertedScbcrse.getListOfScrlevl()) {
                    recCount = scrlevlDao.insert(scrlevl);
                    levlCodesCount += recCount;
                    if (levlCodesCount > 1) levlCodesAsString.append(", ");
                    levlCodesAsString.append(scrlevl.getLevlCode());
                }
                runData.addMessage(format("BannerUpdater: Inserted %d new SCRLEVL records: %s, for: %s:%s:%s:%s", levlCodesCount,
                        levlCodesAsString.toString(), instCode, subjCode, crseNumb, effTerm));

                // Scrgmod
                int gmodCodesCount = 0;
                StringBuilder gmodCodesAsString = new StringBuilder();
                for (Scrgmod scrgmod : convertedScbcrse.getListOfScrgmod()) {
                    recCount = scrgmodDao.insert(scrgmod);
                    gmodCodesCount += recCount;
                    if (gmodCodesCount > 1) gmodCodesAsString.append(", ");
                    gmodCodesAsString.append(scrgmod.getGmodCode());
                    if (scrgmod.getDefaultInd().equals("D")) gmodCodesAsString.append(" (default)");
                }
                runData.addMessage(format("BannerUpdater: Inserted %d new SCRGMOD records: %s, for: %s:%s:%s:%s", gmodCodesCount,
                        gmodCodesAsString.toString(), instCode, subjCode, crseNumb, effTerm));

                // Scrschd
                int schdCodesCount = 0;
                StringBuilder schdCodesAsString = new StringBuilder();
                for (Scrschd scrschd : convertedScbcrse.getListOfScrschd()) {
                    recCount = scrschdDao.insert(scrschd);
                    schdCodesCount += recCount;
                    if (schdCodesCount > 1) schdCodesAsString.append(", ");
                    schdCodesAsString.append(scrschd.getSchdCode());
                }
                runData.addMessage(format("BannerUpdater: Inserted %d new SCRSCHD records: %s, for: %s:%s:%s:%s", schdCodesCount,
                        schdCodesAsString.toString(), instCode, subjCode, crseNumb, effTerm));

                // Scrattr
                int attrCodesCount = 0;
                StringBuilder attrCodesAsString = new StringBuilder();
                for (Scrattr scrattr : convertedScbcrse.getListOfScrattr()) {
                    recCount = scrattrDao.insert(scrattr);
                    attrCodesCount += recCount;
                    if (attrCodesCount > 1) attrCodesAsString.append(", ");
                    attrCodesAsString.append(scrattr.getAttrCode());
                }
                runData.addMessage(format("BannerUpdater: Inserted %d new SCRATTR records: %s, for: %s:%s:%s:%s", attrCodesCount,
                        attrCodesAsString.toString(), instCode, subjCode, crseNumb, effTerm));
            } else {
                runData.addMessage(format("BannerUpdater: SCBCRKY record already exists for %s:%s:%s:%s:%s. No need to create.", scbcrky.getVpdiCode(),
                        scbcrky.getSubjCode(), scbcrky.getCrseNumb(), scbcrky.getTermCodeStart(), scbcrky.getTermCodeEnd()));
                // Check if within SCABASE range; throw Exception if not.
                if (scbcrky.getTermCodeStart().compareTo(effTerm) > 0
                        || scbcrky.getTermCodeEnd().compareTo(effTerm) < 0) {
                    runData.addMessage(format("BannerUpdater: KSCM course start term '%s' is not within SCABASE range (%s - %s)", effTerm, scbcrky
                            .getTermCodeStart(), scbcrky.getTermCodeEnd()));
                    runData.setStatus(Status.FAILURE);
                    throw new Exception("KSCM course start term wasn't within SCABASE range");
                }
                // For each record type, update or insert depending on whether existing record's effTerm equals the new scbcrse record's effTerm.
                // Comparisons are done to see if the UPDATE/INSERT is truly needed.
                insertOrUpdateScbcrse(runData);
                insertOrUpdateScbdesc(runData);
                insertOrUpdateScrsyln(runData);
                insertOrUpdateScrlevl(runData);
                insertOrUpdateScrgmod(runData);
                insertOrUpdateScrschd(runData);
                insertOrUpdateScrattr(runData);

            }
        }
    }

    private void insertOrUpdateScbcrse(RunData runData) throws Exception {
        int recCount = 0;
        String subjCode = runData.getSubjCode();
        String crseNumb = runData.getCrseNumb();
        String effTerm = runData.getEffTerm();
        Scbcrse convertedScbcrse = runData.getConvertedScbcrse();
        // SCBCRSE. Get SCBCRSE record in effect at effTerm
        Scbcrse effScbcrse = scbcrseDao.getEffective(subjCode, crseNumb, effTerm);
        Op op;
        if (effScbcrse == null) {
            op = Op.INSERT;
        } else {
            // Preserve values that we aren't handling yet
            convertedScbcrse.setRepsCode(effScbcrse.getRepsCode());
            convertedScbcrse.setTuiwInd(effScbcrse.getTuiwInd());
            convertedScbcrse.setPwavCode(effScbcrse.getPwavCode());
            convertedScbcrse.setAddFeesInd(effScbcrse.getAddFeesInd());
            convertedScbcrse.setPrereqChkMethodCde(effScbcrse.getPrereqChkMethodCde());
            convertedScbcrse.setCappPrereqTestInd(effScbcrse.getCappPrereqTestInd());
            if (effScbcrse.getEffTerm().equals(effTerm)) {
                op = Op.UPDATE;
            } else {
                op = Op.INSERT;
            }
        }
        switch (op) {
            case INSERT:
                recCount = scbcrseDao.insert(convertedScbcrse);
                break;
            case UPDATE:
                recCount = scbcrseDao.update(convertedScbcrse);
                break;
        }
        runData.addMessage(format("BannerUpdater: %s %d SCBCRSE record: %s:%s:%s:%s:\"%s\"", op.getPastTense(), recCount,
                runData.getInstCode(), subjCode, crseNumb, effTerm, convertedScbcrse.getTitle()));
    }

    private void insertOrUpdateScbdesc(RunData runData) throws Exception {
        int recCount = 0;
        String subjCode = runData.getSubjCode();
        String crseNumb = runData.getCrseNumb();
        String effTerm = runData.getEffTerm();
        Scbdesc convertedScbdesc = runData.getConvertedScbcrse().getScbdesc();
        if (convertedScbdesc == null) {
            // KSCM courses require descriptions.
            // (This should never happend because this condition is checked in the conversion step.)
            throw new Exception("KSCM course did not have description.");
        } else {
            // Get SCBDESC record in effect at effTerm
            Scbdesc effScbdesc = scbdescDao.getEffective(subjCode, crseNumb, effTerm);
            Op op;
            if (effScbdesc == null) {
                op = Op.INSERT;
            } else {
                if (effScbdesc.getTermCodeEff().equals(effTerm)) {
                    op = Op.UPDATE;
                } else {
                    op = Op.INSERT;
                }
            }
            if (!different(convertedScbdesc, effScbdesc)) {
                runData.addMessage(format("BannerUpdater: No changes detected to description. Skipping %s of SCBDESC record for " +
                                "%s:%s:%s:%s:%s", op.getPresentTense(), runData.getInstCode(), subjCode, crseNumb, effTerm,
                        Util.javaQuote(convertedScbdesc.getTextNarrative(), 40)));
            } else {
                switch (op) {
                    case INSERT:
                        // Get future record to determine TERM_CODE_END
                        Scbdesc futureScbdesc = scbdescDao.getFuture(subjCode, crseNumb, effTerm);
                        if (futureScbdesc != null) {
                            convertedScbdesc.setTermCodeEnd(futureScbdesc.getTermCodeEff());
                        }
                        recCount = scbdescDao.insert(convertedScbdesc);
                        if (effScbdesc != null) {
                            // Need to update effScbdesc to update its TermCodeEnd to the newly inserted record
                            effScbdesc.setTermCodeEnd(effTerm);
                            recCount = scbdescDao.update(effScbdesc);
                            if (recCount != 1) {
                                throw new Exception(format("Problem updating older (%s) SCBDESC record's TERM_CODE_END: RecCount: %d for %s %s %s %s",
                                        effScbdesc.getTermCodeEff(), recCount, runData.getInstCode(), subjCode, crseNumb, effTerm));
                            }
                        }
                        runData.addMessage(format("BannerUpdater: inserted %d SCBDESC record for %s:%s:%s:%s:\"%s\"", recCount,
                                runData.getInstCode(), subjCode, crseNumb, effTerm, Util.javaQuote(convertedScbdesc.getTextNarrative(), 40)));
                        break;
                    case UPDATE:
                        recCount = scbdescDao.update(convertedScbdesc);
                        runData.addMessage(format("BannerUpdater: updated %d SCBDESC record: %s:%s:%s:%s:\"%s\"", recCount,
                                runData.getInstCode(), subjCode, crseNumb, effTerm, Util.javaQuote(convertedScbdesc.getTextNarrative(), 40)));
                        break;
                }
            }
        }
    }

    private void insertOrUpdateScrsyln(RunData runData) throws Exception {
        int recCount = 0;
        String subjCode = runData.getSubjCode();
        String crseNumb = runData.getCrseNumb();
        String effTerm = runData.getEffTerm();
        Scrsyln convertedScrsyln = runData.getConvertedScbcrse().getScrsyln();
        // Get SCRSYLN record in effect at effTerm
        Scrsyln effScrsyln = scrsylnDao.getEffective(subjCode, crseNumb, effTerm);
        if (convertedScrsyln == null && effScrsyln != null) {
            // To handle this situation, we need to apply a SCRSYLN record with LONG_COURSE_TITLE value of null
            convertedScrsyln = new Scrsyln();
            BeanUtils.copyProperties(effScrsyln, convertedScrsyln);
            convertedScrsyln.setLongCourseTitle(null);
        }
        Op op;
        if (effScrsyln == null) {
            op = Op.INSERT;
        } else {
            if (effScrsyln.getTermCodeEff().equals(effTerm)) {
                op = Op.UPDATE;
            } else {
                op = Op.INSERT;
            }
        }
        if (!different(convertedScrsyln, effScrsyln)) {
            runData.addMessage(format("BannerUpdater: No changes detected to long course title. Skipping %s of SCRSYLN record for " +
                            "%s:%s:%s:%s:%s", op.getPresentTense(), runData.getInstCode(), subjCode, crseNumb, effTerm,
                    Util.javaQuote(convertedScrsyln.getLongCourseTitle(), 40)));
        } else {
            switch (op) {
                case INSERT:
                    // Get future record to determine TERM_CODE_END
                    Scrsyln futureScrsyln = scrsylnDao.getFuture(subjCode, crseNumb, effTerm);
                    if (futureScrsyln != null) {
                        convertedScrsyln.setTermCodeEnd(futureScrsyln.getTermCodeEff());
                    }
                    recCount = scrsylnDao.insert(convertedScrsyln);
                    if (effScrsyln != null) {
                        // Need to update effScrsyln to update its TermCodeEnd to the newly inserted record
                        effScrsyln.setTermCodeEnd(effTerm);
                        recCount = scrsylnDao.update(effScrsyln);
                        if (recCount != 1) {
                            throw new Exception(format("Problem updating older (%s) SCRSYLN record's TERM_CODE_END: RecCount: %d for %s %s %s %s",
                                    effScrsyln.getTermCodeEff(), recCount, runData.getInstCode(), subjCode, crseNumb, effTerm));
                        }
                    }
                    runData.addMessage(format("BannerUpdater: inserted %d SCRSYLN record for %s:%s:%s:%s:%s", recCount,
                            runData.getInstCode(), subjCode, crseNumb, effTerm, Util.javaQuote(convertedScrsyln.getLongCourseTitle(), 40)));
                    break;
                case UPDATE:
                    recCount = scrsylnDao.update(convertedScrsyln);
                    runData.addMessage(format("BannerUpdater: updated %d SCRSYLN record: %s:%s:%s:%s:%s", recCount,
                            runData.getInstCode(), subjCode, crseNumb, effTerm, Util.javaQuote(convertedScrsyln.getLongCourseTitle(), 40)));
                    break;
            }
        }
    }

    private void insertOrUpdateScrlevl(RunData runData) throws Exception{
        int recCount = 0;
        String subjCode = runData.getSubjCode();
        String crseNumb = runData.getCrseNumb();
        String effTerm = runData.getEffTerm();
        List<Scrlevl> convertedListOfScrlevl = runData.getConvertedScbcrse().getListOfScrlevl();
        // Get list of SCRLEVL records in effect at effTerm
        List<Scrlevl>  effListOfScrlevl = scrlevlDao.getEffective(subjCode, crseNumb, effTerm);
        Op op;
        if (effListOfScrlevl.size() == 0) {
            op = Op.INSERT;
        } else {
            if (effListOfScrlevl.get(0).getEffTerm().equals(effTerm)) {
                op = Op.UPDATE;
            } else {
                op = Op.INSERT;
            }
        }
        if (!differentListOfScrlevl(convertedListOfScrlevl, effListOfScrlevl)) {
            runData.addMessage(format("BannerUpdater: No changes detected to Level Codes. Skipping %s of SCRLEVL records for " +
                            "%s:%s:%s:%s", op.getPresentTense(), runData.getInstCode(), subjCode, crseNumb, effTerm));
        } else {
            if (op == Op.UPDATE) {
                // UPDATE is accomplished by DELETE + INSERT
                recCount = scrlevlDao.delete(subjCode, crseNumb, effTerm);
                runData.addMessage(format("BannerUpdater: deleted %d SCRLEVL records for %s:%s:%s:%s in preparation for inserts.", recCount,
                        runData.getInstCode(), subjCode, crseNumb, effTerm));
            }
            recCount = 0;
            List<String> levlCodes = new ArrayList<>();
            for (Scrlevl convertedScrlevl : convertedListOfScrlevl) {
                levlCodes.add(convertedScrlevl.getLevlCode());
                recCount += scrlevlDao.insert(convertedScrlevl);
            }
            runData.addMessage(format("BannerUpdater: inserted %d SCRLEVL records for %s:%s:%s:%s:%s", recCount,
                    runData.getInstCode(), subjCode, crseNumb, effTerm, join(", ", levlCodes)));
        }
    }

    private void insertOrUpdateScrgmod(RunData runData) throws Exception{
        int recCount = 0;
        String subjCode = runData.getSubjCode();
        String crseNumb = runData.getCrseNumb();
        String effTerm = runData.getEffTerm();
        List<Scrgmod> convertedListOfScrgmod = runData.getConvertedScbcrse().getListOfScrgmod();
        // Get list of SCRGMOD records in effect at effTerm
        List<Scrgmod>  effListOfScrgmod = scrgmodDao.getEffective(subjCode, crseNumb, effTerm);
        Op op;
        if (effListOfScrgmod.size() == 0) {
            op = Op.INSERT;
        } else {
            if (effListOfScrgmod.get(0).getEffTerm().equals(effTerm)) {
                op = Op.UPDATE;
            } else {
                op = Op.INSERT;
            }
        }
        if (!differentListOfScrgmod(convertedListOfScrgmod, effListOfScrgmod)) {
            runData.addMessage(format("BannerUpdater: No changes detected to Grading Modes. Skipping %s of SCRGMOD records for " +
                    "%s:%s:%s:%s", op.getPresentTense(), runData.getInstCode(), subjCode, crseNumb, effTerm));
        } else {
            if (op == Op.UPDATE) {
                // UPDATE is accomplished by DELETE + INSERT
                recCount = scrgmodDao.delete(subjCode, crseNumb, effTerm);
                runData.addMessage(format("BannerUpdater: deleted %d SCRGMOD records for %s:%s:%s:%s in preparation for inserts.", recCount,
                        runData.getInstCode(), subjCode, crseNumb, effTerm));
            }
            recCount = 0;
            List<String> gmodCodes = new ArrayList<>();
            for (Scrgmod convertedScrgmod : convertedListOfScrgmod) {
                gmodCodes.add(convertedScrgmod.getGmodCode() + convertedScrgmod.getDefaultInd() == "D" ? " (default)" : "");
                recCount += scrgmodDao.insert(convertedScrgmod);
            }
            runData.addMessage(format("BannerUpdater: inserted %d SCRGMOD records for %s:%s:%s:%s:%s", recCount,
                    runData.getInstCode(), subjCode, crseNumb, effTerm, join(", ", gmodCodes)));
        }
    }

    private void insertOrUpdateScrschd(RunData runData) throws Exception{
        int recCount = 0;
        String subjCode = runData.getSubjCode();
        String crseNumb = runData.getCrseNumb();
        String effTerm = runData.getEffTerm();
        List<Scrschd> convertedListOfScrschd = runData.getConvertedScbcrse().getListOfScrschd();
        // Get list of SCRSCHD records in effect at effTerm
        List<Scrschd>  effListOfScrschd = scrschdDao.getEffective(subjCode, crseNumb, effTerm);
        Op op;
        if (effListOfScrschd.size() == 0) {
            op = Op.INSERT;
        } else {
            if (effListOfScrschd.get(0).getEffTerm().equals(effTerm)) {
                op = Op.UPDATE;
            } else {
                op = Op.INSERT;
            }
        }
        if (!differentListOfScrschd(convertedListOfScrschd, effListOfScrschd)) {
            runData.addMessage(format("BannerUpdater: No changes detected to SCHD Codes. Skipping %s of SCRSCHD records for " +
                    "%s:%s:%s:%s", op.getPresentTense(), runData.getInstCode(), subjCode, crseNumb, effTerm));
        } else {
            if (op == Op.UPDATE) {
                // UPDATE is accomplished by DELETE + INSERT
                recCount = scrschdDao.delete(subjCode, crseNumb, effTerm);
                runData.addMessage(format("BannerUpdater: deleted %d SCRSCHD records for %s:%s:%s:%s in preparation for inserts.", recCount,
                        runData.getInstCode(), subjCode, crseNumb, effTerm));
            }
            recCount = 0;
            List<String> schdCodes = new ArrayList<>();
            for (Scrschd convertedScrschd : convertedListOfScrschd) {
                schdCodes.add(convertedScrschd.getSchdCode());
                recCount += scrschdDao.insert(convertedScrschd);
            }
            runData.addMessage(format("BannerUpdater: inserted %d SCRSCHD records for %s:%s:%s:%s:%s", recCount,
                    runData.getInstCode(), subjCode, crseNumb, effTerm, join(", ", schdCodes)));
        }
    }

    private void insertOrUpdateScrattr(RunData runData) throws Exception{
        int recCount = 0;
        String subjCode = runData.getSubjCode();
        String crseNumb = runData.getCrseNumb();
        String effTerm = runData.getEffTerm();
        List<Scrattr> convertedListOfScrattr = runData.getConvertedScbcrse().getListOfScrattr();
        // Get list of SCRATTR records in effect at effTerm
        List<Scrattr>  effListOfScrattr = scrattrDao.getEffective(subjCode, crseNumb, effTerm);
        Op op;
        if (effListOfScrattr.size() == 0) {
            op = Op.INSERT;
        } else {
            if (effListOfScrattr.get(0).getEffTerm().equals(effTerm)) {
                op = Op.UPDATE;
            } else {
                op = Op.INSERT;
            }
        }
        if (!differentListOfScrattr(convertedListOfScrattr, effListOfScrattr)) {
            runData.addMessage(format("BannerUpdater: No changes detected to Degree Attribute Codes. Skipping %s of SCRATTR records for " +
                    "%s:%s:%s:%s", op.getPresentTense(), runData.getInstCode(), subjCode, crseNumb, effTerm));
        } else {
            if (op == Op.UPDATE) {
                // UPDATE is accomplished by DELETE + INSERT
                recCount = scrattrDao.delete(subjCode, crseNumb, effTerm);
                runData.addMessage(format("BannerUpdater: deleted %d SCRATTR records for %s:%s:%s:%s in preparation for inserts.", recCount,
                        runData.getInstCode(), subjCode, crseNumb, effTerm));
            }
            recCount = 0;
            List<String> attrCodes = new ArrayList<>();
            for (Scrattr convertedScrattr : convertedListOfScrattr) {
                attrCodes.add(convertedScrattr.getAttrCode());
                recCount += scrattrDao.insert(convertedScrattr);
            }
            runData.addMessage(format("BannerUpdater: inserted %d SCRATTR records for %s:%s:%s:%s:%s", recCount,
                    runData.getInstCode(), subjCode, crseNumb, effTerm, join(", ", attrCodes)));
        }
    }

    boolean different(Scbdesc n, Scbdesc o) {
        // return true at first detected difference
        assert n != null;
        if (o == null) return true;
        if (!n.getTextNarrative().equals(o.getTextNarrative())) {
            return true;
        }
        return false;
    }

    boolean different(Scrsyln n, Scrsyln o) {
        // return true at first detected difference
        assert n != null;
        if (o == null) return true;
        if (n.getLongCourseTitle() == null && o.getLongCourseTitle() == null) return false;
        if (n.getLongCourseTitle() != null && o.getLongCourseTitle() == null) return true;
        if (n.getLongCourseTitle() != null && !n.getLongCourseTitle().equals(o.getLongCourseTitle())) {
            return true;
        }
        return false;
    }

    boolean different(Scrlevl n, Scrlevl o) {
        // return true at first detected difference
        assert n != null; assert o != null;
        // levlCode should never be null
        assert n.getLevlCode() != null; assert o.getLevlCode() != null;
        if (!n.getLevlCode().equals(o.getLevlCode())) {
            return true;
        }
        return false;
    }

    boolean differentListOfScrlevl(List<Scrlevl> n, List<Scrlevl> o) {
        // return true at first detected difference
        assert n != null; assert o != null;
        if (n.size() != o.size()) return true;
        boolean has = false;
        for (Scrlevl nScrlevl : n) {
            for (Scrlevl oScrlevl : o) {
                if (!different(nScrlevl, oScrlevl)) {
                    has = true;
                    break;
                }
            }
            if (!has) {
                // n has value that o does not have
                return true;
            }
        }
        return false;
    }

    boolean different(Scrgmod n, Scrgmod o) {
        // return true at first detected difference
        assert n != null; assert o != null;
        // note: gmodCode is never null
        assert n.getGmodCode() != null; assert o.getGmodCode() != null;
        if (!n.getGmodCode().equals(o.getGmodCode())) {
            return true;
        }
        if (!n.getDefaultInd().equals(o.getDefaultInd())) {
            return true;
        }
        return false;
    }

    boolean differentListOfScrgmod(List<Scrgmod> n, List<Scrgmod> o) {
        // return true at first detected difference
        assert n != null; assert o != null;
        if (n.size() != o.size()) return true;
        boolean has = false;
        for (Scrgmod nScrgmod : n) {
            for (Scrgmod oScrgmod : o) {
                if (!different(nScrgmod, oScrgmod)) {
                    has = true;
                    break;
                }
            }
            if (!has) {
                // n has value that o does not have
                return true;
            }
        }
        return false;
    }

    boolean different(Scrschd n, Scrschd o) {
        // return true at first detected difference
        assert n != null; assert o != null;
        // schdCode should never be null
        assert n.getSchdCode() != null; assert o.getSchdCode() != null;
        if (!n.getSchdCode().equals(o.getSchdCode())) {
            return true;
        }
        return false;
    }

    boolean differentListOfScrschd(List<Scrschd> n, List<Scrschd> o) {
        // return true at first detected difference
        assert n != null; assert o != null;
        if (n.size() != o.size()) return true;
        boolean has = false;
        for (Scrschd nScrschd : n) {
            for (Scrschd oScrschd : o) {
                if (!different(nScrschd, oScrschd)) {
                    has = true;
                    break;
                }
            }
            if (!has) {
                // n has value that o does not have
                return true;
            }
        }
        return false;
    }

    boolean different(Scrattr n, Scrattr o) {
        // return true at first detected difference
        assert n != null; assert o != null;
        // attrCode should never be null
        assert n.getAttrCode() != null; assert o.getAttrCode() != null;
        if (!n.getAttrCode().equals(o.getAttrCode())) {
            return true;
        }
        return false;
    }

    boolean differentListOfScrattr(List<Scrattr> n, List<Scrattr> o) {
        // return true at first detected difference
        assert n != null; assert o != null;
        if (n.size() != o.size()) return true;
        boolean has = false;
        for (Scrattr nScrattr : n) {
            for (Scrattr oScrattr : o) {
                if (!different(nScrattr, oScrattr)) {
                    has = true;
                    break;
                }
            }
            if (!has) {
                // n has value that o does not have
                return true;
            }
        }
        return false;
    }

    private String databaseName;

    public String getDatabaseName() {
        return databaseName;
    }

    private void initDatabaseName() {
        // TODO: Need to grant SELECT ON v$database to KSCM_APP_ROLE
        // (KSCM_APP_ROLE is the role used by UHAPP_KSCM_USER)
        // Until that is done, return a hard-coded value.
        //this.databaseName = this.jdbcTemplate.queryForObject("SELECT NAME FROM v$database", String.class);
        this.databaseName = "TBD";
    }

    void init() {
        validateConnection();
        initDatabaseName();
    }

    private void validateConnection() {
        try {
            DataSource dataSource = jdbcTemplate.getDataSource();
            Connection conn = dataSource.getConnection();
            boolean isValid = conn.isValid(5);
            logger.info("DB connection {} valid", isValid ? "is" : "is not");
        } catch (SQLException e) {
            logger.error("Exception while testing whether connection is valid", e);
            throw (new RuntimeException(e));
        }
    }
}
