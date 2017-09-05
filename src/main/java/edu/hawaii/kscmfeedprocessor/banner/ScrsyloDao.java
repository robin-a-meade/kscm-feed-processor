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
public class ScrsyloDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "bannerJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;

    /**
     *  Get version effective at given term
     */
    public Scrsylo getEffective(String subjCode, String crseNumb, String effTerm) {
        String sqlEffRecord = BannerUtil.sqlEffRecord("SCRSYLO", "SCRSYLO_TERM_CODE_EFF");
        logger.trace(sqlEffRecord);
        try {
            return this.jdbcTemplate.queryForObject(sqlEffRecord, new Object[]{subjCode, crseNumb, effTerm}, new ScrsyloDao.ScrsyloMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     *  Get first future record relative to given term
     */
    public Scrsylo getFuture(String subjCode, String crseNumb, String effTerm) {
        String sqlFutureRecord = BannerUtil.sqlFutureRecord("SCRSYLO", "SCRSYLO_TERM_CODE_EFF");
        logger.trace(sqlFutureRecord);
        try {
            return this.jdbcTemplate.queryForObject(sqlFutureRecord, new Object[]{subjCode, crseNumb, effTerm}, new ScrsyloDao.ScrsyloMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     *  Adds columns that are common to both INSERT and UPDATE statements
     */
    private void addCommonColumns(DmlBuilder dmlBuilder, Scrsylo scrsylo) {
        dmlBuilder.add("SCRSYLO_ACTIVITY_DATE", scrsylo.getActivityDate());
        dmlBuilder.add("SCRSYLO_USER_ID", scrsylo.getUserId());
        dmlBuilder.add("SCRSYLO_DATA_ORIGIN", scrsylo.getDataOrigin());
        dmlBuilder.add("SCRSYLO_LEARNING_OBJECTIVES", scrsylo.getLearningObjectives());
        dmlBuilder.add("SCRSYLO_TERM_CODE_END", scrsylo.getTermCodeEnd());
    }

    public int insert(Scrsylo scrsylo) {
        DmlBuilder dmlBuilder = new DmlBuilder("SCRSYLO");
        dmlBuilder.add("SCRSYLO_SUBJ_CODE", scrsylo.getSubjCode());
        dmlBuilder.add("SCRSYLO_CRSE_NUMB", scrsylo.getCrseNumb());
        dmlBuilder.add("SCRSYLO_TERM_CODE_EFF", scrsylo.getTermCodeEff());
        addCommonColumns(dmlBuilder, scrsylo);
        return this.jdbcTemplate.update(dmlBuilder.getInsert(), dmlBuilder.getParamValueArray());
    }

    public int update(Scrsylo scrsylo) {
        StringBuffer sb = new StringBuffer();
        DmlBuilder dmlBuilder = new DmlBuilder("SCRSYLO");
        // First build and add the UPDATE statement
        addCommonColumns(dmlBuilder, scrsylo);
        sb.append(dmlBuilder.getUpdate());
        Object[] paramValues = dmlBuilder.getParamValueArray();
        // Then build and add the WHERE clause
        dmlBuilder.clear();
        dmlBuilder.add("SCRSYLO_SUBJ_CODE", scrsylo.getSubjCode());
        dmlBuilder.add("SCRSYLO_CRSE_NUMB", scrsylo.getCrseNumb());
        dmlBuilder.add("SCRSYLO_TERM_CODE_EFF", scrsylo.getTermCodeEff());
        sb.append("\n");
        sb.append(dmlBuilder.getWhereClause());
        // Concatenate the parameter values needed by the WHERE clause to the parameter values array we already had for the UPDATE statement
        paramValues = ObjectArrays.concat(paramValues, dmlBuilder.getParamValueArray(), Object.class);
        return this.jdbcTemplate.update(sb.toString(), paramValues);
    }


    private static final class ScrsyloMapper implements RowMapper<Scrsylo> {
        public Scrsylo mapRow(ResultSet rs, int rowNum) throws SQLException {
            Scrsylo scrsylo = new Scrsylo();
            // Common columns
            scrsylo.setSubjCode(rs.getString("SCRSYLO_SUBJ_CODE"));
            scrsylo.setCrseNumb(rs.getString("SCRSYLO_CRSE_NUMB"));
            scrsylo.setVpdiCode(rs.getString("SCRSYLO_VPDI_CODE"));
            scrsylo.setSurrogateId(rs.getBigDecimal("SCRSYLO_SURROGATE_ID"));
            scrsylo.setVersion(rs.getBigDecimal("SCRSYLO_VERSION"));
            scrsylo.setDataOrigin(rs.getString("SCRSYLO_DATA_ORIGIN"));
            scrsylo.setUserId(rs.getString("SCRSYLO_USER_ID"));
            scrsylo.setActivityDate(rs.getTimestamp("SCRSYLO_ACTIVITY_DATE"));
            // Unique to this table
            scrsylo.setTermCodeEff(rs.getString("SCRSYLO_TERM_CODE_EFF"));
            scrsylo.setTermCodeEnd(rs.getString("SCRSYLO_TERM_CODE_END"));
            scrsylo.setLearningObjectives(rs.getString("SCRSYLO_LEARNING_OBJECTIVES"));
            return scrsylo;
        }
    }
}
