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
import java.util.List;

/**
 *
 */
@Repository
public class ScbdescDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "bannerJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;


    /**
     *  Get version effective at given term
     */
    public Scbdesc getEffective(String subjCode, String crseNumb, String effTerm) {
        String sqlEffRecord = BannerUtil.sqlEffRecord("SCBDESC", "SCBDESC_TERM_CODE_EFF");
        logger.trace(sqlEffRecord);
        try {
            return this.jdbcTemplate.queryForObject(sqlEffRecord, new Object[]{subjCode, crseNumb, effTerm}, new ScbdescMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     *  Get first future record relative to given term
     */
    public Scbdesc getFuture(String subjCode, String crseNumb, String effTerm) {
        String sqlFutureRecord = BannerUtil.sqlFutureRecord("SCBDESC", "SCBDESC_TERM_CODE_EFF");
        logger.trace(sqlFutureRecord);
        try {
            return this.jdbcTemplate.queryForObject(sqlFutureRecord, new Object[]{subjCode, crseNumb, effTerm}, new ScbdescMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    /**
     *  Adds columns that are common to both INSERT and UPDATE statements
     */
    private void addCommonColumns(DmlBuilder dmlBuilder, Scbdesc scbdesc) {
        dmlBuilder.add("SCBDESC_ACTIVITY_DATE", scbdesc.getActivityDate());
        dmlBuilder.add("SCBDESC_USER_ID", scbdesc.getUserId());
        dmlBuilder.add("SCBDESC_DATA_ORIGIN", scbdesc.getDataOrigin());
        dmlBuilder.add("SCBDESC_TEXT_NARRATIVE", scbdesc.getTextNarrative());
        dmlBuilder.add("SCBDESC_TERM_CODE_END", scbdesc.getTermCodeEnd());
    }

    public int insert(Scbdesc scbdesc) {
        DmlBuilder dmlBuilder = new DmlBuilder("SCBDESC");
        dmlBuilder.add("SCBDESC_SUBJ_CODE", scbdesc.getSubjCode());
        dmlBuilder.add("SCBDESC_CRSE_NUMB", scbdesc.getCrseNumb());
        dmlBuilder.add("SCBDESC_TERM_CODE_EFF", scbdesc.getTermCodeEff());
        addCommonColumns(dmlBuilder, scbdesc);
        return this.jdbcTemplate.update(dmlBuilder.getInsert(), dmlBuilder.getParamValueArray());
    }

    public int update(Scbdesc scbdesc) {
        StringBuffer sb = new StringBuffer();
        DmlBuilder dmlBuilder = new DmlBuilder("SCBDESC");
        // First build and add the UPDATE statement
        addCommonColumns(dmlBuilder, scbdesc);
        sb.append(dmlBuilder.getUpdate());
        Object[] paramValues = dmlBuilder.getParamValueArray();
        // Then build and add the WHERE clause
        dmlBuilder.clear();
        dmlBuilder.add("SCBDESC_SUBJ_CODE", scbdesc.getSubjCode());
        dmlBuilder.add("SCBDESC_CRSE_NUMB", scbdesc.getCrseNumb());
        dmlBuilder.add("SCBDESC_TERM_CODE_EFF", scbdesc.getTermCodeEff());
        sb.append("\n");
        sb.append(dmlBuilder.getWhereClause());
        // Concatenate the parameter values needed by the WHERE clause to the parameter values array we already had for the UPDATE statement
        paramValues = ObjectArrays.concat(paramValues, dmlBuilder.getParamValueArray(), Object.class);
        return this.jdbcTemplate.update(sb.toString(), paramValues);
    }


    private static final class ScbdescMapper implements RowMapper<Scbdesc> {
        public Scbdesc mapRow(ResultSet rs, int rowNum) throws SQLException {
            Scbdesc scbdesc = new Scbdesc();
            // Common columns
            scbdesc.setSubjCode(rs.getString("SCBDESC_SUBJ_CODE"));
            scbdesc.setCrseNumb(rs.getString("SCBDESC_CRSE_NUMB"));
            scbdesc.setVpdiCode(rs.getString("SCBDESC_VPDI_CODE"));
            scbdesc.setSurrogateId(rs.getBigDecimal("SCBDESC_SURROGATE_ID"));
            scbdesc.setVersion(rs.getBigDecimal("SCBDESC_VERSION"));
            scbdesc.setDataOrigin(rs.getString("SCBDESC_DATA_ORIGIN"));
            scbdesc.setUserId(rs.getString("SCBDESC_USER_ID"));
            scbdesc.setActivityDate(rs.getTimestamp("SCBDESC_ACTIVITY_DATE"));
            // Unique to this table
            scbdesc.setTermCodeEff(rs.getString("SCBDESC_TERM_CODE_EFF"));
            scbdesc.setTextNarrative(rs.getString("SCBDESC_TEXT_NARRATIVE"));
            scbdesc.setTermCodeEnd(rs.getString("SCBDESC_TERM_CODE_END"));
            return scbdesc;
        }
    }
}
