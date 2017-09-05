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
    private ScrsyloDao scrsyloDao;

    @Autowired
    private ScrlevlDao scrlevlDao;

    @Autowired
    private ScrgmodDao scrgmodDao;

    @Autowired
    private ScrschdDao scrschdDao;

    @Autowired
    private ScrattrDao scrattrDao;

    @Autowired
    private ScbsuppDao scbsuppDao;

    @Autowired
    private ScrintgDao scrintgDao;

    @Autowired
    private ScrtextDao scrtextDao;

    @Autowired
    private ScrfeesDao scrfeesDao;

    @Autowired
    private ScreqivDao screqivDao;

    @Autowired
    private ScrcorqDao scrcorqDao;

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
     * Throws an exception if not successful. The exception will cause the transaction to rollback. In such case, the logs will show messages like this
     * for logger <code>o.s.t.i.TransactionInterceptor</code>: <ul> <li>Completing transaction for [edu.hawaii.kscmfeedprocessor.BannerUpdaterService
     * .bannerUpdater]
     * after exception: java.lang.RuntimeException <li>Applying rules to determine whether transaction should rollback on java.lang.RuntimeException...
     * <li>Initiating transaction rollback <li>Rolling back JDBC transaction on Connection [com.sun.proxy.$Proxy77@64f89238] </ul>
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

            //runData.addMessage(format("BannerUpdater: VPDI context was verified for %s in database '%s'", instCode, databaseName));
            // Until we properly set databaseName
            runData.addMessage(format("BannerUpdater: VPDI context was verified for %s", instCode));

            //  Attempt to retrieve master course record
            Scbcrky scbcrky = scbcrkyDao.get(subjCode, crseNumb);

            if (scbcrky == null) {

                // New course.

                runData.addMessage(format("BannerUpdater: No SCBCRKY record yet exists for %s %s %s", instCode,
                        subjCode,
                        crseNumb));

                // Insert SCBCRKY record.
                recCount = scbcrkyDao.insert(subjCode, crseNumb, effTerm, runData.getUserId(), runData.getDataOrigin());
                runData.addMessage(format("BannerUpdater: Inserted %d new SCBCRKY record: %s:%s:%s:%s:999999.", recCount,
                        instCode, subjCode, crseNumb, effTerm));

            } else {
                runData.addMessage(format("BannerUpdater: SCBCRKY record already exists for %s:%s:%s:%s:%s. No need to create.", scbcrky.getVpdiCode(),
                        scbcrky.getSubjCode(), scbcrky.getCrseNumb(), scbcrky.getTermCodeStart(), scbcrky.getTermCodeEnd()));
                // Check if within SCABASE range; throw Exception if not.
                if (scbcrky.getTermCodeStart().compareTo(effTerm) > 0
                        || scbcrky.getTermCodeEnd().compareTo(effTerm) < 0) {
                    runData.addMessage(format("BannerUpdater: KSCM course start term '%s' is not within SCABASE range (%s - %s)", effTerm, scbcrky
                            .getTermCodeStart(), scbcrky.getTermCodeEnd()));
                    runData.setStatus(Status.FAILED);
                    throw new Exception("KSCM course start term wasn't within SCABASE range");
                }
            }
            // For each record type, update or insert depending on whether existing record's effTerm equals the new scbcrse record's effTerm.
            // Comparisons with existing Banner data are done to see if the UPDATE/INSERT is truly needed.
            insertOrUpdateScbcrse(runData);
            insertOrUpdateScbdesc(runData);
            insertOrUpdateScrsyln(runData);
            insertOrUpdateScrsylo(runData);
            insertOrUpdateScrlevl(runData);
            insertOrUpdateScrgmod(runData);
            insertOrUpdateScrschd(runData);
            insertOrUpdateScrattr(runData);
            insertOrUpdateScbsupp(runData);
            insertOrUpdateScrintg(runData);
            insertOrUpdateScrtext(runData);
            insertOrUpdateScrfees(runData);
            insertOrUpdateScreqiv(runData);
            insertOrUpdateScrcorq(runData);
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
            // To handle this situation, we need to apply a SCRSYLN record with LONG_COURSE_TITLE and COURSE_URL value of null
            convertedScrsyln = new Scrsyln();
            BeanUtils.copyProperties(effScrsyln, convertedScrsyln);
            convertedScrsyln.setLongCourseTitle(null);
            convertedScrsyln.setCourseUrl(null);
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
                            "%s:%s:%s:%s:{LongTitle: %s, URL: %s}", op.getPresentTense(), runData.getInstCode(), subjCode, crseNumb, effTerm,
                    Util.javaQuote(convertedScrsyln.getLongCourseTitle(), 40), Util.javaQuote(convertedScrsyln.getCourseUrl(), 40)));
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
                    runData.addMessage(format("BannerUpdater: inserted %d SCRSYLN record for %s:%s:%s:%s:{LongTitle: %s, URL: %s}", recCount,
                            runData.getInstCode(), subjCode, crseNumb, effTerm, Util.javaQuote(convertedScrsyln.getLongCourseTitle(), 40), Util
                                    .javaQuote(convertedScrsyln.getCourseUrl(), 40)));
                    break;
                case UPDATE:
                    recCount = scrsylnDao.update(convertedScrsyln);
                    runData.addMessage(format("BannerUpdater: updated %d SCRSYLN record: %s:%s:%s:%s:{LongTitle: %s, URL: %s}", recCount,
                            runData.getInstCode(), subjCode, crseNumb, effTerm, Util.javaQuote(convertedScrsyln.getLongCourseTitle(), 40), Util
                                    .javaQuote(convertedScrsyln.getCourseUrl(), 40)));
                    break;
            }
        }
    }

    private void insertOrUpdateScrsylo(RunData runData) throws Exception {
        int recCount = 0;
        String subjCode = runData.getSubjCode();
        String crseNumb = runData.getCrseNumb();
        String effTerm = runData.getEffTerm();
        Scrsylo convertedScrsylo = runData.getConvertedScbcrse().getScrsylo();
        // Get SCRSYLO record in effect at effTerm
        Scrsylo effScrsylo = scrsyloDao.getEffective(subjCode, crseNumb, effTerm);
        if (convertedScrsylo == null && effScrsylo != null) {
            // To handle this situation, we need to apply a SCRSYLO record with Learning Objectives value of null
            convertedScrsylo = new Scrsylo();
            BeanUtils.copyProperties(effScrsylo, convertedScrsylo);
            convertedScrsylo.setLearningObjectives(null);
        }
        Op op;
        if (effScrsylo == null) {
            op = Op.INSERT;
        } else {
            if (effScrsylo.getTermCodeEff().equals(effTerm)) {
                op = Op.UPDATE;
            } else {
                op = Op.INSERT;
            }
        }
        if (!different(convertedScrsylo, effScrsylo)) {
            runData.addMessage(format("BannerUpdater: No changes detected to Learning Objectives. Skipping %s of SCRSYLO record for " +
                            "%s:%s:%s:%s:%s", op.getPresentTense(), runData.getInstCode(), subjCode, crseNumb, effTerm,
                    Util.javaQuote(convertedScrsylo == null ? null : convertedScrsylo.getLearningObjectives(), 40)));
        } else {
            switch (op) {
                case INSERT:
                    // Get future record to determine TERM_CODE_END
                    Scrsylo futureScrsylo = scrsyloDao.getFuture(subjCode, crseNumb, effTerm);
                    if (futureScrsylo != null) {
                        convertedScrsylo.setTermCodeEnd(futureScrsylo.getTermCodeEff());
                    }
                    recCount = scrsyloDao.insert(convertedScrsylo);
                    if (effScrsylo != null) {
                        // Need to update effScrsylo to update its TermCodeEnd to the newly inserted record
                        effScrsylo.setTermCodeEnd(effTerm);
                        recCount = scrsyloDao.update(effScrsylo);
                        if (recCount != 1) {
                            throw new Exception(format("Problem updating older (%s) SCRSYLO record's TERM_CODE_END: RecCount: %d for %s %s %s %s",
                                    effScrsylo.getTermCodeEff(), recCount, runData.getInstCode(), subjCode, crseNumb, effTerm));
                        }
                    }
                    runData.addMessage(format("BannerUpdater: inserted %d SCRSYLO record for %s:%s:%s:%s:%s", recCount,
                            runData.getInstCode(), subjCode, crseNumb, effTerm, Util.javaQuote(convertedScrsylo.getLearningObjectives(), 40)));
                    break;
                case UPDATE:
                    recCount = scrsyloDao.update(convertedScrsylo);
                    runData.addMessage(format("BannerUpdater: updated %d SCRSYLO record: %s:%s:%s:%s:%s", recCount,
                            runData.getInstCode(), subjCode, crseNumb, effTerm, Util.javaQuote(convertedScrsylo.getLearningObjectives(), 40)));
                    break;
            }
        }
    }

    private void insertOrUpdateScrlevl(RunData runData) throws Exception {
        int recCount = 0;
        String subjCode = runData.getSubjCode();
        String crseNumb = runData.getCrseNumb();
        String effTerm = runData.getEffTerm();
        List<Scrlevl> convertedListOfScrlevl = runData.getConvertedScbcrse().getListOfScrlevl();
        // Get list of SCRLEVL records in effect at effTerm
        List<Scrlevl> effListOfScrlevl = scrlevlDao.getEffective(subjCode, crseNumb, effTerm);
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

    private void insertOrUpdateScrgmod(RunData runData) throws Exception {
        int recCount = 0;
        String subjCode = runData.getSubjCode();
        String crseNumb = runData.getCrseNumb();
        String effTerm = runData.getEffTerm();
        List<Scrgmod> convertedListOfScrgmod = runData.getConvertedScbcrse().getListOfScrgmod();
        // Get list of SCRGMOD records in effect at effTerm
        List<Scrgmod> effListOfScrgmod = scrgmodDao.getEffective(subjCode, crseNumb, effTerm);
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

    private void insertOrUpdateScrschd(RunData runData) throws Exception {
        int recCount = 0;
        String subjCode = runData.getSubjCode();
        String crseNumb = runData.getCrseNumb();
        String effTerm = runData.getEffTerm();
        List<Scrschd> convertedListOfScrschd = runData.getConvertedScbcrse().getListOfScrschd();
        // Get list of SCRSCHD records in effect at effTerm
        List<Scrschd> effListOfScrschd = scrschdDao.getEffective(subjCode, crseNumb, effTerm);
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

    private void insertOrUpdateScrattr(RunData runData) throws Exception {
        int recCount = 0;
        String subjCode = runData.getSubjCode();
        String crseNumb = runData.getCrseNumb();
        String effTerm = runData.getEffTerm();
        List<Scrattr> convertedListOfScrattr = runData.getConvertedScbcrse().getListOfScrattr();
        // Get list of SCRATTR records in effect at effTerm
        List<Scrattr> effListOfScrattr = scrattrDao.getEffective(subjCode, crseNumb, effTerm);
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

    private void insertOrUpdateScbsupp(RunData runData) throws Exception {
        int recCount = 0;
        String subjCode = runData.getSubjCode();
        String crseNumb = runData.getCrseNumb();
        String effTerm = runData.getEffTerm();
        Scbsupp convertedScbsupp = runData.getConvertedScbcrse().getScbsupp();
        // Get SCBSUPP record in effect at effTerm
        Scbsupp effScbsupp = scbsuppDao.getEffective(subjCode, crseNumb, effTerm);
        if (convertedScbsupp == null && effScbsupp != null) {
            // To handle this situation, we need to apply a SCBSUPP record with SCBSUPP_CUDA_CODE value of null
            convertedScbsupp = new Scbsupp();
            BeanUtils.copyProperties(effScbsupp, convertedScbsupp);
            convertedScbsupp.setCudaCode(null);
        }
        Op op;
        if (effScbsupp == null) {
            op = Op.INSERT;
        } else {
            if (effScbsupp.getEffTerm().equals(effTerm)) {
                op = Op.UPDATE;
            } else {
                op = Op.INSERT;
            }
        }
        if (!different(convertedScbsupp, effScbsupp)) {
            runData.addMessage(format("BannerUpdater: No changes detected to SCBSUPP_CUDA_CODE. Skipping %s of SCBSUPP record for " +
                            "%s:%s:%s:%s:%s", op.getPresentTense(), runData.getInstCode(), subjCode, crseNumb, effTerm,
                    Util.javaQuote(convertedScbsupp == null ? null : convertedScbsupp.getCudaCode(), 40)));
        } else {
            switch (op) {
                case INSERT:
                    recCount = scbsuppDao.insert(convertedScbsupp);
                    runData.addMessage(format("BannerUpdater: inserted %d SCBSUPP record for %s:%s:%s:%s:%s", recCount,
                            runData.getInstCode(), subjCode, crseNumb, effTerm, Util.javaQuote(convertedScbsupp.getCudaCode(), 40)));
                    break;
                case UPDATE:
                    recCount = scbsuppDao.update(convertedScbsupp);
                    runData.addMessage(format("BannerUpdater: updated %d SCBSUPP record: %s:%s:%s:%s:%s", recCount,
                            runData.getInstCode(), subjCode, crseNumb, effTerm, Util.javaQuote(convertedScbsupp.getCudaCode(), 40)));
                    break;
            }
        }
    }

    private void insertOrUpdateScrintg(RunData runData) throws Exception {
        int recCount = 0;
        String subjCode = runData.getSubjCode();
        String crseNumb = runData.getCrseNumb();
        String effTerm = runData.getEffTerm();
        List<Scrintg> convertedListOfScrintg = runData.getConvertedScbcrse().getListOfScrintg();
        // Get list of SCRINTG records in effect at effTerm
        List<Scrintg> effListOfScrintg = scrintgDao.getEffective(subjCode, crseNumb, effTerm);
        Op op;
        if (effListOfScrintg.size() == 0) {
            op = Op.INSERT;
        } else {
            if (effListOfScrintg.get(0).getTermCodeEff().equals(effTerm)) {
                op = Op.UPDATE;
            } else {
                op = Op.INSERT;
            }
        }
        if (!differentListOfScrintg(convertedListOfScrintg, effListOfScrintg)) {
            runData.addMessage(format("BannerUpdater: No changes detected to Integration Partner Codes. Skipping %s of SCRINTG records for " +
                    "%s:%s:%s:%s", op.getPresentTense(), runData.getInstCode(), subjCode, crseNumb, effTerm));
        } else {
            if (op == Op.UPDATE) {
                // UPDATE is accomplished by DELETE + INSERT
                recCount = scrintgDao.delete(subjCode, crseNumb, effTerm);
                runData.addMessage(format("BannerUpdater: deleted %d SCRINTG records for %s:%s:%s:%s in preparation for inserts.", recCount,
                        runData.getInstCode(), subjCode, crseNumb, effTerm));
            }
            recCount = 0;
            List<String> intgCodes = new ArrayList<>();
            for (Scrintg convertedScrintg : convertedListOfScrintg) {
                intgCodes.add(convertedScrintg.getIntgCde());
                recCount += scrintgDao.insert(convertedScrintg);
            }
            runData.addMessage(format("BannerUpdater: inserted %d SCRINTG records for %s:%s:%s:%s:%s", recCount,
                    runData.getInstCode(), subjCode, crseNumb, effTerm, join(", ", intgCodes)));
        }
    }

    private void insertOrUpdateScrtext(RunData runData) throws Exception {
        int recCount = 0;
        String subjCode = runData.getSubjCode();
        String crseNumb = runData.getCrseNumb();
        String effTerm = runData.getEffTerm();
        List<Scrtext> convertedListOfScrtext = runData.getConvertedScbcrse().getListOfScrtext();
        // Get list of SCRTEXT records in effect at effTerm
        List<Scrtext> effListOfScrtext = scrtextDao.getEffective(subjCode, crseNumb, effTerm);
        Op op;
        if (effListOfScrtext.size() == 0) {
            op = Op.INSERT;
        } else {
            if (effListOfScrtext.get(0).getEffTerm().equals(effTerm)) {
                op = Op.UPDATE;
            } else {
                op = Op.INSERT;
            }
        }
        if (!differentListOfScrtext(convertedListOfScrtext, effListOfScrtext)) {
            runData.addMessage(format("BannerUpdater: No changes detected to SCRTEXT records. Skipping %s of SCRTEXT records for " +
                    "%s:%s:%s:%s", op.getPresentTense(), runData.getInstCode(), subjCode, crseNumb, effTerm));
        } else {
            if (op == Op.UPDATE) {
                // UPDATE is accomplished by DELETE + INSERT
                recCount = scrtextDao.delete(subjCode, crseNumb, effTerm);
                runData.addMessage(format("BannerUpdater: deleted %d SCRTEXT records for %s:%s:%s:%s in preparation for inserts.", recCount,
                        runData.getInstCode(), subjCode, crseNumb, effTerm));
            }
            recCount = 0;
            for (Scrtext convertedScrtext : convertedListOfScrtext) {
                recCount += scrtextDao.insert(convertedScrtext);
            }
            runData.addMessage(format("BannerUpdater: inserted %d SCRTEXT records for %s:%s:%s:%s", recCount,
                    runData.getInstCode(), subjCode, crseNumb, effTerm));
        }
    }

    private void insertOrUpdateScrfees(RunData runData) throws Exception {
        int recCount = 0;
        String subjCode = runData.getSubjCode();
        String crseNumb = runData.getCrseNumb();
        String effTerm = runData.getEffTerm();
        List<Scrfees> convertedListOfScrfees = runData.getConvertedScbcrse().getListOfScrfees();
        // Get list of SCRFEES records in effect at effTerm
        List<Scrfees> effListOfScrfees = scrfeesDao.getEffective(subjCode, crseNumb, effTerm);
        Op op;
        if (effListOfScrfees.size() == 0) {
            op = Op.INSERT;
        } else {
            if (effListOfScrfees.get(0).getEffTerm().equals(effTerm)) {
                op = Op.UPDATE;
            } else {
                op = Op.INSERT;
            }
        }
        if (!differentListOfScrfees(convertedListOfScrfees, effListOfScrfees)) {
            runData.addMessage(format("BannerUpdater: No changes detected to Fees. Skipping %s of SCRFEES records for " +
                    "%s:%s:%s:%s", op.getPresentTense(), runData.getInstCode(), subjCode, crseNumb, effTerm));
        } else {
            if (op == Op.UPDATE) {
                // UPDATE is accomplished by DELETE + INSERT
                recCount = scrfeesDao.delete(subjCode, crseNumb, effTerm);
                runData.addMessage(format("BannerUpdater: deleted %d SCRFEES records for %s:%s:%s:%s in preparation for inserts.", recCount,
                        runData.getInstCode(), subjCode, crseNumb, effTerm));
            }
            recCount = 0;
            List<String> fees = new ArrayList<>();
            for (Scrfees convertedScrfees : convertedListOfScrfees) {
                fees.add(convertedScrfees.getDetlCode() + ":" + convertedScrfees.getFeeAmount());
                recCount += scrfeesDao.insert(convertedScrfees);
            }
            runData.addMessage(format("BannerUpdater: inserted %d SCRFEES records for %s:%s:%s:%s:%s", recCount,
                    runData.getInstCode(), subjCode, crseNumb, effTerm, join(", ", fees)));
        }
    }

    private void insertOrUpdateScreqiv(RunData runData) throws Exception {
        int recCount = 0;
        String subjCode = runData.getSubjCode();
        String crseNumb = runData.getCrseNumb();
        String effTerm = runData.getEffTerm();
        List<Screqiv> convertedListOfScreqiv = runData.getConvertedScbcrse().getListOfScreqiv();
        // Get list of SCREQIV records in effect at effTerm
        List<Screqiv> effListOfScreqiv = screqivDao.getEffective(subjCode, crseNumb, effTerm);
        Op op;
        if (effListOfScreqiv.size() == 0) {
            op = Op.INSERT;
        } else {
            if (effListOfScreqiv.get(0).getEffTerm().equals(effTerm)) {
                op = Op.UPDATE;
            } else {
                op = Op.INSERT;
            }
        }
        if (!differentListOfScreqiv(convertedListOfScreqiv, effListOfScreqiv)) {
            runData.addMessage(format("BannerUpdater: No changes detected to equivalent courses. Skipping %s of SCREQIV records for " +
                    "%s:%s:%s:%s", op.getPresentTense(), runData.getInstCode(), subjCode, crseNumb, effTerm));
        } else {
            if (op == Op.UPDATE) {
                // UPDATE is accomplished by DELETE + INSERT
                recCount = screqivDao.delete(subjCode, crseNumb, effTerm);
                runData.addMessage(format("BannerUpdater: deleted %d SCREQIV records for %s:%s:%s:%s in preparation for inserts.", recCount,
                        runData.getInstCode(), subjCode, crseNumb, effTerm));
            }
            recCount = 0;
            List<String> eqiv = new ArrayList<>();
            for (Screqiv convertedScreqiv : convertedListOfScreqiv) {
                eqiv.add(convertedScreqiv.getSubjCodeEqiv() + convertedScreqiv.getCrseNumbEqiv());
                recCount += screqivDao.insert(convertedScreqiv);
            }
            runData.addMessage(format("BannerUpdater: inserted %d SCREQIV records for %s:%s:%s:%s:%s", recCount,
                    runData.getInstCode(), subjCode, crseNumb, effTerm, join(", ", eqiv)));
        }
    }

    private void insertOrUpdateScrcorq(RunData runData) throws Exception {
        int recCount = 0;
        String subjCode = runData.getSubjCode();
        String crseNumb = runData.getCrseNumb();
        String effTerm = runData.getEffTerm();
        List<Scrcorq> convertedListOfScrcorq = runData.getConvertedScbcrse().getListOfScrcorq();
        // Get list of SCRCORQ records in effect at effTerm
        List<Scrcorq> effListOfScrcorq = scrcorqDao.getEffective(subjCode, crseNumb, effTerm);
        Op op;
        if (effListOfScrcorq.size() == 0) {
            op = Op.INSERT;
        } else {
            if (effListOfScrcorq.get(0).getEffTerm().equals(effTerm)) {
                op = Op.UPDATE;
            } else {
                op = Op.INSERT;
            }
        }
        if (!differentListOfScrcorq(convertedListOfScrcorq, effListOfScrcorq)) {
            runData.addMessage(format("BannerUpdater: No changes detected to equivalent courses. Skipping %s of SCRCORQ records for " +
                    "%s:%s:%s:%s", op.getPresentTense(), runData.getInstCode(), subjCode, crseNumb, effTerm));
        } else {
            if (op == Op.UPDATE) {
                // UPDATE is accomplished by DELETE + INSERT
                recCount = scrcorqDao.delete(subjCode, crseNumb, effTerm);
                runData.addMessage(format("BannerUpdater: deleted %d SCRCORQ records for %s:%s:%s:%s in preparation for inserts.", recCount,
                        runData.getInstCode(), subjCode, crseNumb, effTerm));
            }
            recCount = 0;
            List<String> corq = new ArrayList<>();
            for (Scrcorq convertedScrcorq : convertedListOfScrcorq) {
                corq.add(convertedScrcorq.getSubjCodeCorq() + convertedScrcorq.getCrseNumbCorq());
                recCount += scrcorqDao.insert(convertedScrcorq);
            }
            runData.addMessage(format("BannerUpdater: inserted %d SCRCORQ records for %s:%s:%s:%s:%s", recCount,
                    runData.getInstCode(), subjCode, crseNumb, effTerm, join(", ", corq)));
        }
    }

    boolean different(Scbsupp n, Scbsupp o) {
        // return true at first detected difference
        if (n == null && o == null) return false;
        if (n == null && o != null) return true;
        if (n != null && o == null) return true;
        if (n.getCudaCode() == null && o.getCudaCode() == null) return false;
        if (n.getCudaCode() == null && o.getCudaCode() != null) return true;
        if (n.getCudaCode() != null && o.getCudaCode() == null) return true;
        if (!n.getCudaCode().equals(o.getCudaCode())) {
            return true;
        }
        return false;
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
        if (n.getLongCourseTitle() != null && o.getLongCourseTitle() == null) return true;
        if (n.getLongCourseTitle() == null && o.getLongCourseTitle() != null) return true;
        if (n.getLongCourseTitle() != null && !n.getLongCourseTitle().equals(o.getLongCourseTitle())) {
            return true;
        }
        if (n.getCourseUrl() != null && o.getCourseUrl() == null) return true;
        if (n.getCourseUrl() == null && o.getCourseUrl() != null) return true;
        if (n.getCourseUrl() != null && !n.getCourseUrl().equals(o.getCourseUrl())) {
            return true;
        }
        return false;
    }

    boolean different(Scrsylo n, Scrsylo o) {
        // return true at first detected difference
        if (n == null && o == null) return false;
        if (n == null && o != null) return true;
        if (n != null && o == null) return true;
        if (n.getLearningObjectives() == null && o.getLearningObjectives() == null) return false;
        if (n.getLearningObjectives() != null && o.getLearningObjectives() == null) return true;
        if (n.getLearningObjectives() != null && !n.getLearningObjectives().equals(o.getLearningObjectives())) {
            return true;
        }
        return false;
    }

    boolean different(Scrlevl n, Scrlevl o) {
        // return true at first detected difference
        // levlCode should never be null
        if (!n.getLevlCode().equals(o.getLevlCode())) {
            return true;
        }
        return false;
    }

    boolean differentListOfScrlevl(List<Scrlevl> n, List<Scrlevl> o) {
        // return true at first detected difference
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
        // note: gmodCode is never null
        assert n.getGmodCode() != null;
        assert o.getGmodCode() != null;
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
        // schdCode should never be null
        assert n.getSchdCode() != null;
        assert o.getSchdCode() != null;
        if (!n.getSchdCode().equals(o.getSchdCode())) {
            return true;
        }
        return false;
    }

    boolean differentListOfScrschd(List<Scrschd> n, List<Scrschd> o) {
        // return true at first detected difference
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
        // attrCode should never be null
        assert n.getAttrCode() != null;
        assert o.getAttrCode() != null;
        if (!n.getAttrCode().equals(o.getAttrCode())) {
            return true;
        }
        return false;
    }

    boolean differentListOfScrattr(List<Scrattr> n, List<Scrattr> o) {
        // return true at first detected difference
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

    boolean different(Scrintg n, Scrintg o) {
        // return true at first detected difference
        // intgCode should never be null
        assert n.getIntgCde() != null;
        assert o.getIntgCde() != null;
        if (!n.getIntgCde().equals(o.getIntgCde())) {
            return true;
        }
        return false;
    }

    boolean differentListOfScrintg(List<Scrintg> n, List<Scrintg> o) {
        // return true at first detected difference
        if (n.size() != o.size()) return true;
        boolean has = false;
        for (Scrintg nScrintg : n) {
            for (Scrintg oScrintg : o) {
                if (!different(nScrintg, oScrintg)) {
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

    boolean differentListOfScrtext(List<Scrtext> n, List<Scrtext> o) {
        // return true at first detected difference
        if (n.size() != o.size()) return true;
        for (int i = 0; i < n.size(); i++) {
            if (n.get(i).getText() == null) return true;
            if (!n.get(i).getText().equals(o.get(i).getText())) return true;
        }
        return false;
    }

    boolean differentListOfScrfees(List<Scrfees> n, List<Scrfees> o) {
        // return true at first detected difference
        if (n.size() != o.size()) return true;
        for (int i = 0; i < n.size(); i++) {
            if (!n.get(i).getDetlCode().equals(o.get(i).getDetlCode())) return true;
            if (!n.get(i).getFeeAmount().equals(o.get(i).getFeeAmount())) return true;
            if (n.get(i).getSeqno() != o.get(i).getSeqno()) return true;
            if (!n.get(i).getFtypCode().equals(o.get(i).getFtypCode())) return true;
        }
        return false;
    }

    boolean differentListOfScreqiv(List<Screqiv> n, List<Screqiv> o) {
        // return true at first detected difference
        if (n.size() != o.size()) return true;
        for (int i = 0; i < n.size(); i++) {
            if (!n.get(i).getCrseNumbEqiv().equals(o.get(i).getCrseNumbEqiv())) return true;
            if (!n.get(i).getSubjCodeEqiv().equals(o.get(i).getSubjCodeEqiv())) return true;
            if (!n.get(i).getStartTerm().equals(o.get(i).getStartTerm())) return true;
            if (!n.get(i).getEndTerm().equals(o.get(i).getEndTerm())) return true;
        }
        return false;
    }

    boolean differentListOfScrcorq(List<Scrcorq> n, List<Scrcorq> o) {
        // return true at first detected difference
        if (n.size() != o.size()) return true;
        for (int i = 0; i < n.size(); i++) {
            if (!n.get(i).getCrseNumbCorq().equals(o.get(i).getCrseNumbCorq())) return true;
            if (!n.get(i).getSubjCodeCorq().equals(o.get(i).getSubjCodeCorq())) return true;
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
