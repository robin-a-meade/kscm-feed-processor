package edu.hawaii.kscmfeedprocessor.banner;

import com.google.common.collect.ObjectArrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 *
 */
@Repository
public class ScbcrseDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "bannerJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    ScbdescDao scbdescDao;

    @Autowired
    ScrlevlDao scrlevlDao;

    @Autowired
    ScrattrDao scrattrDao;

    @Autowired
    ScrgmodDao scrgmodDao;

    @Autowired
    ScrschdDao scrschdDao;

    @Autowired
    ScrsylnDao scrsylnDao;

    /**
     * Get count
     */
    public int getCount(String subjCode, String crseNumb) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(*) FROM SCBCRSE \n");
        sb.append("WHERE SCBCRSE_SUBJ_CODE = ? \n");
        sb.append("AND SCBCRSE_CRSE_NUMB = ?");
        return this.jdbcTemplate.queryForObject(sb.toString(), Integer.class, new Object[]{subjCode, crseNumb});
    }

    /**
     *  Adds columns that are common to both INSERT and UPDATE statements
     */
    private void addCommonColumns(DmlBuilder dmlBuilder, Scbcrse scbcrse) {
        dmlBuilder.add("SCBCRSE_ACTIVITY_DATE", scbcrse.getActivityDate());
        dmlBuilder.add("SCBCRSE_USER_ID", scbcrse.getUserId());
        dmlBuilder.add("SCBCRSE_DATA_ORIGIN", scbcrse.getDataOrigin());
        dmlBuilder.add("SCBCRSE_TITLE", scbcrse.getTitle());
        dmlBuilder.add("SCBCRSE_COLL_CODE", scbcrse.getCollCode());
        dmlBuilder.add("SCBCRSE_DIVS_CODE", scbcrse.getDivsCode());
        dmlBuilder.add("SCBCRSE_DEPT_CODE", scbcrse.getDeptCode());
        dmlBuilder.add("SCBCRSE_CSTA_CODE", scbcrse.getCstaCode());
        dmlBuilder.add("SCBCRSE_APRV_CODE", scbcrse.getAprvCode());
        dmlBuilder.add("SCBCRSE_REPEAT_LIMIT", scbcrse.getRepeatLimit());
        dmlBuilder.add("SCBCRSE_MAX_RPT_UNITS", scbcrse.getMaxRptUnits());
        dmlBuilder.add("SCBCRSE_REPS_CODE", scbcrse.getRepsCode());
        dmlBuilder.add("SCBCRSE_TUIW_IND", scbcrse.getTuiwInd());
        dmlBuilder.add("SCBCRSE_PWAV_CODE", scbcrse.getPwavCode());
        dmlBuilder.add("SCBCRSE_ADD_FEES_IND", scbcrse.getAddFeesInd());
        dmlBuilder.add("SCBCRSE_PREREQ_CHK_METHOD_CDE", scbcrse.getPrereqChkMethodCde());
        dmlBuilder.add("SCBCRSE_CAPP_PREREQ_TEST_IND", scbcrse.getCappPrereqTestInd());
        dmlBuilder.add("SCBCRSE_CREDIT_HR_LOW", scbcrse.getCreditHrLow());
        dmlBuilder.add("SCBCRSE_CREDIT_HR_IND", scbcrse.getCreditHrInd());
        dmlBuilder.add("SCBCRSE_CREDIT_HR_HIGH", scbcrse.getCreditHrHigh());
        dmlBuilder.add("SCBCRSE_BILL_HR_LOW", scbcrse.getBillHrLow());
        dmlBuilder.add("SCBCRSE_BILL_HR_IND", scbcrse.getBillHrInd());
        dmlBuilder.add("SCBCRSE_BILL_HR_HIGH", scbcrse.getBillHrHigh());
        dmlBuilder.add("SCBCRSE_LEC_HR_LOW", scbcrse.getLecHrLow());
        dmlBuilder.add("SCBCRSE_LEC_HR_IND", scbcrse.getLecHrInd());
        dmlBuilder.add("SCBCRSE_LEC_HR_HIGH", scbcrse.getLecHrHigh());
        dmlBuilder.add("SCBCRSE_LAB_HR_LOW", scbcrse.getLabHrLow());
        dmlBuilder.add("SCBCRSE_LAB_HR_IND", scbcrse.getLabHrInd());
        dmlBuilder.add("SCBCRSE_LAB_HR_HIGH", scbcrse.getLabHrHigh());
        dmlBuilder.add("SCBCRSE_OTH_HR_LOW", scbcrse.getOthHrLow());
        dmlBuilder.add("SCBCRSE_OTH_HR_IND", scbcrse.getOthHrInd());
        dmlBuilder.add("SCBCRSE_OTH_HR_HIGH", scbcrse.getOthHrHigh());
        dmlBuilder.add("SCBCRSE_CONT_HR_LOW", scbcrse.getContHrLow());
        dmlBuilder.add("SCBCRSE_CONT_HR_IND", scbcrse.getContHrInd());
        dmlBuilder.add("SCBCRSE_CONT_HR_HIGH", scbcrse.getContHrHigh());
    }

    public int update(Scbcrse scbcrse) {
        StringBuffer sb = new StringBuffer();
        DmlBuilder dmlBuilder = new DmlBuilder("SCBCRSE");
        // First build and add the UPDATE statement
        addCommonColumns(dmlBuilder, scbcrse);
        sb.append(dmlBuilder.getUpdate());
        Object[] paramValues = dmlBuilder.getParamValueArray();
        // Then build and add the WHERE clause
        dmlBuilder.clear();
        dmlBuilder.add("SCBCRSE_SUBJ_CODE", scbcrse.getSubjCode());
        dmlBuilder.add("SCBCRSE_CRSE_NUMB", scbcrse.getCrseNumb());
        dmlBuilder.add("SCBCRSE_EFF_TERM", scbcrse.getEffTerm());
        sb.append("\n");
        sb.append(dmlBuilder.getWhereClause());
        // Concatenate the parameter values needed by the WHERE clause to the parameter values array we already had for the UPDATE statement
        paramValues = ObjectArrays.concat(paramValues, dmlBuilder.getParamValueArray(), Object.class);
        return this.jdbcTemplate.update(sb.toString(), paramValues);
    }

    public int insert(Scbcrse scbcrse) {
        DmlBuilder dmlBuilder = new DmlBuilder("SCBCRSE");
        dmlBuilder.add("SCBCRSE_SUBJ_CODE", scbcrse.getSubjCode());
        dmlBuilder.add("SCBCRSE_CRSE_NUMB", scbcrse.getCrseNumb());
        dmlBuilder.add("SCBCRSE_EFF_TERM", scbcrse.getEffTerm());
        addCommonColumns(dmlBuilder, scbcrse);
        return this.jdbcTemplate.update(dmlBuilder.getInsert(), dmlBuilder.getParamValueArray());
    }

    /**
     * Get version effective at given term
     */
    public Scbcrse getEffective(String subjCode, String crseNumb, String effTerm) {
        String sqlEffRecord = BannerUtil.sqlEffRecord("SCBCRSE", "SCBCRSE_EFF_TERM");
        logger.trace(sqlEffRecord);
        Scbcrse scbcrse = null;
        try {
            scbcrse = this.jdbcTemplate.queryForObject(sqlEffRecord, new Object[]{subjCode, crseNumb, effTerm}, new ScbcrseMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        scbcrse.setScbdesc(scbdescDao.getEffective(subjCode, crseNumb, effTerm));
        scbcrse.setListOfScrlevl(scrlevlDao.getEffective(subjCode, crseNumb, effTerm));
        scbcrse.setListOfScrattr(scrattrDao.getEffective(subjCode, crseNumb, effTerm));
        scbcrse.setListOfScrgmod(scrgmodDao.getEffective(subjCode, crseNumb, effTerm));
        scbcrse.setListOfScrschd(scrschdDao.getEffective(subjCode, crseNumb, effTerm));
        scbcrse.setScrsyln(scrsylnDao.getEffective(subjCode, crseNumb, effTerm));
        return scbcrse;
    }

    /**
     * Get all versions
     */
    public List<Scbcrse> getAll(String subjCode, String crseNumb) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM SCBCRSE \n");
        sb.append("WHERE SCBCRSE_SUBJ_CODE = ? \n");
        sb.append("AND SCBCRSE_CRSE_NUMB = ?");
        return this.jdbcTemplate.query(sb.toString(), new Object[]{subjCode, crseNumb}, new ScbcrseMapper());
    }

    private static final class ScbcrseMapper implements RowMapper<Scbcrse> {
        public Scbcrse mapRow(ResultSet rs, int rowNum) throws SQLException {
            Scbcrse scbcrse = new Scbcrse();
            // Common columns
            scbcrse.setSubjCode(rs.getString("SCBCRSE_SUBJ_CODE"));
            scbcrse.setCrseNumb(rs.getString("SCBCRSE_CRSE_NUMB"));
            scbcrse.setVpdiCode(rs.getString("SCBCRSE_VPDI_CODE"));
            scbcrse.setSurrogateId(rs.getBigDecimal("SCBCRSE_SURROGATE_ID"));
            scbcrse.setVersion(rs.getBigDecimal("SCBCRSE_VERSION"));
            scbcrse.setDataOrigin(rs.getString("SCBCRSE_DATA_ORIGIN"));
            scbcrse.setUserId(rs.getString("SCBCRSE_USER_ID"));
            scbcrse.setActivityDate(rs.getTimestamp("SCBCRSE_ACTIVITY_DATE"));
            // Unique to this table
            scbcrse.setEffTerm(rs.getString("SCBCRSE_EFF_TERM"));
            scbcrse.setTitle(rs.getString("SCBCRSE_TITLE"));
            scbcrse.setCollCode(rs.getString("SCBCRSE_COLL_CODE"));
            scbcrse.setDivsCode(rs.getString("SCBCRSE_DIVS_CODE"));
            scbcrse.setDeptCode(rs.getString("SCBCRSE_DEPT_CODE"));
            scbcrse.setCstaCode(rs.getString("SCBCRSE_CSTA_CODE"));
            scbcrse.setAprvCode(rs.getString("SCBCRSE_APRV_CODE"));
            scbcrse.setRepeatLimit(rs.getBigDecimal("SCBCRSE_REPEAT_LIMIT"));
            scbcrse.setMaxRptUnits(rs.getBigDecimal("SCBCRSE_MAX_RPT_UNITS"));
            scbcrse.setRepsCode(rs.getString("SCBCRSE_REPS_CODE"));
            scbcrse.setTuiwInd(rs.getString("SCBCRSE_TUIW_IND"));
            scbcrse.setPwavCode(rs.getString ("SCBCRSE_PWAV_CODE"));
            scbcrse.setAddFeesInd(rs.getString ("SCBCRSE_ADD_FEES_IND"));
            scbcrse.setPrereqChkMethodCde(rs.getString("SCBCRSE_PREREQ_CHK_METHOD_CDE"));
            scbcrse.setCappPrereqTestInd(rs.getString("SCBCRSE_CAPP_PREREQ_TEST_IND"));
            // creditHr
            scbcrse.setCreditHrInd(rs.getString("SCBCRSE_CREDIT_HR_IND"));
            scbcrse.setCreditHrLow(rs.getBigDecimal("SCBCRSE_CREDIT_HR_LOW"));
            scbcrse.setCreditHrHigh(rs.getBigDecimal("SCBCRSE_CREDIT_HR_HIGH"));
            // billHr
            scbcrse.setBillHrInd(rs.getString("SCBCRSE_BILL_HR_IND"));
            scbcrse.setBillHrLow(rs.getBigDecimal("SCBCRSE_BILL_HR_LOW"));
            scbcrse.setBillHrHigh(rs.getBigDecimal("SCBCRSE_BILL_HR_HIGH"));
            // lecHr
            scbcrse.setLecHrInd(rs.getString("SCBCRSE_LEC_HR_IND"));
            scbcrse.setLecHrLow(rs.getBigDecimal("SCBCRSE_LEC_HR_LOW"));
            scbcrse.setLecHrHigh(rs.getBigDecimal("SCBCRSE_LEC_HR_HIGH"));
            // labHr
            scbcrse.setLabHrInd(rs.getString("SCBCRSE_LAB_HR_IND"));
            scbcrse.setLabHrLow(rs.getBigDecimal("SCBCRSE_LAB_HR_LOW"));
            scbcrse.setLabHrHigh(rs.getBigDecimal("SCBCRSE_LAB_HR_HIGH"));
            // othHr
            scbcrse.setOthHrInd(rs.getString("SCBCRSE_OTH_HR_IND"));
            scbcrse.setOthHrLow(rs.getBigDecimal("SCBCRSE_OTH_HR_LOW"));
            scbcrse.setOthHrHigh(rs.getBigDecimal("SCBCRSE_OTH_HR_HIGH"));
            // contHr
            scbcrse.setContHrInd(rs.getString("SCBCRSE_CONT_HR_IND"));
            scbcrse.setContHrLow(rs.getBigDecimal("SCBCRSE_CONT_HR_LOW"));
            scbcrse.setContHrHigh(rs.getBigDecimal("SCBCRSE_CONT_HR_HIGH"));
            return scbcrse;
        }
    }
}
