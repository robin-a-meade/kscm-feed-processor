package edu.hawaii.kscmfeedprocessor.banner;

import com.google.common.collect.ObjectArrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;


@Repository
public class ScrsylnDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "bannerJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;

    /**
     *  Get version effective at given term
     */
    public Scrsyln getEffective(String subjCode, String crseNumb, String effTerm) {
        String sqlEffRecord = BannerUtil.sqlEffRecord("SCRSYLN", "SCRSYLN_TERM_CODE_EFF");
        logger.trace(sqlEffRecord);
        try {
            return this.jdbcTemplate.queryForObject(sqlEffRecord, new Object[]{subjCode, crseNumb, effTerm}, new ScrsylnDao.ScrsylnMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     *  Get first future record relative to given term
     */
    public Scrsyln getFuture(String subjCode, String crseNumb, String effTerm) {
        String sqlFutureRecord = BannerUtil.sqlFutureRecord("SCRSYLN", "SCRSYLN_TERM_CODE_EFF");
        logger.trace(sqlFutureRecord);
        try {
            return this.jdbcTemplate.queryForObject(sqlFutureRecord, new Object[]{subjCode, crseNumb, effTerm}, new ScrsylnDao.ScrsylnMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     *  Adds columns that are common to both INSERT and UPDATE statements
     */
    private void addCommonColumns(DmlBuilder dmlBuilder, Scrsyln scrsyln) {
        dmlBuilder.add("SCRSYLN_ACTIVITY_DATE", scrsyln.getActivityDate());
        dmlBuilder.add("SCRSYLN_USER_ID", scrsyln.getUserId());
        dmlBuilder.add("SCRSYLN_DATA_ORIGIN", scrsyln.getDataOrigin());
        dmlBuilder.add("SCRSYLN_LONG_COURSE_TITLE", scrsyln.getLongCourseTitle());
        dmlBuilder.add("SCRSYLN_COURSE_URL", scrsyln.getCourseUrl());
        dmlBuilder.add("SCRSYLN_TERM_CODE_END", scrsyln.getTermCodeEnd());
    }

    public int insert(Scrsyln scrsyln) {
        DmlBuilder dmlBuilder = new DmlBuilder("SCRSYLN");
        dmlBuilder.add("SCRSYLN_SUBJ_CODE", scrsyln.getSubjCode());
        dmlBuilder.add("SCRSYLN_CRSE_NUMB", scrsyln.getCrseNumb());
        dmlBuilder.add("SCRSYLN_TERM_CODE_EFF", scrsyln.getTermCodeEff());
        addCommonColumns(dmlBuilder, scrsyln);
        return this.jdbcTemplate.update(dmlBuilder.getInsert(), dmlBuilder.getParamValueArray());
    }

    public int update(Scrsyln scrsyln) {
        StringBuffer sb = new StringBuffer();
        DmlBuilder dmlBuilder = new DmlBuilder("SCRSYLN");
        // First build and add the UPDATE statement
        addCommonColumns(dmlBuilder, scrsyln);
        sb.append(dmlBuilder.getUpdate());
        Object[] paramValues = dmlBuilder.getParamValueArray();
        // Then build and add the WHERE clause
        dmlBuilder.clear();
        dmlBuilder.add("SCRSYLN_SUBJ_CODE", scrsyln.getSubjCode());
        dmlBuilder.add("SCRSYLN_CRSE_NUMB", scrsyln.getCrseNumb());
        dmlBuilder.add("SCRSYLN_TERM_CODE_EFF", scrsyln.getTermCodeEff());
        sb.append("\n");
        sb.append(dmlBuilder.getWhereClause());
        // Concatenate the parameter values needed by the WHERE clause to the parameter values array we already had for the UPDATE statement
        paramValues = ObjectArrays.concat(paramValues, dmlBuilder.getParamValueArray(), Object.class);
        return this.jdbcTemplate.update(sb.toString(), paramValues);
    }


    private static final class ScrsylnMapper implements RowMapper<Scrsyln> {
        public Scrsyln mapRow(ResultSet rs, int rowNum) throws SQLException {
            Scrsyln scrsyln = new Scrsyln();
            // Common columns
            scrsyln.setSubjCode(rs.getString("SCRSYLN_SUBJ_CODE"));
            scrsyln.setCrseNumb(rs.getString("SCRSYLN_CRSE_NUMB"));
            scrsyln.setVpdiCode(rs.getString("SCRSYLN_VPDI_CODE"));
            scrsyln.setSurrogateId(rs.getBigDecimal("SCRSYLN_SURROGATE_ID"));
            scrsyln.setVersion(rs.getBigDecimal("SCRSYLN_VERSION"));
            scrsyln.setDataOrigin(rs.getString("SCRSYLN_DATA_ORIGIN"));
            scrsyln.setUserId(rs.getString("SCRSYLN_USER_ID"));
            scrsyln.setActivityDate(rs.getTimestamp("SCRSYLN_ACTIVITY_DATE"));
            // Unique to this table
            scrsyln.setTermCodeEff(rs.getString("SCRSYLN_TERM_CODE_EFF"));
            scrsyln.setTermCodeEnd(rs.getString("SCRSYLN_TERM_CODE_END"));
            scrsyln.setLongCourseTitle(rs.getString("SCRSYLN_LONG_COURSE_TITLE"));
            scrsyln.setCourseUrl(rs.getString("SCRSYLN_COURSE_URL"));
            return scrsyln;
        }
    }
}
