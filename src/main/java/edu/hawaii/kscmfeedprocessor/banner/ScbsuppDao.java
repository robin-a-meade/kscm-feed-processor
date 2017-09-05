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
public class ScbsuppDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "bannerJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;


    /**
     *  Get version effective at given term
     */
    public Scbsupp getEffective(String subjCode, String crseNumb, String effTerm) {
        String sqlEffRecord = BannerUtil.sqlEffRecord("SCBSUPP", "SCBSUPP_EFF_TERM");
        logger.trace(sqlEffRecord);
        try {
            return this.jdbcTemplate.queryForObject(sqlEffRecord, new Object[]{subjCode, crseNumb, effTerm}, new ScbsuppMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     *  Get first future record relative to given term
     */
    public Scbsupp getFuture(String subjCode, String crseNumb, String effTerm) {
        String sqlFutureRecord = BannerUtil.sqlFutureRecord("SCBSUPP", "SCBSUPP_EFF_TERM");
        logger.trace(sqlFutureRecord);
        try {
            return this.jdbcTemplate.queryForObject(sqlFutureRecord, new Object[]{subjCode, crseNumb, effTerm}, new ScbsuppMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    /**
     *  Adds columns that are common to both INSERT and UPDATE statements
     */
    private void addCommonColumns(DmlBuilder dmlBuilder, Scbsupp scbsupp) {
        dmlBuilder.add("SCBSUPP_ACTIVITY_DATE", scbsupp.getActivityDate());
        dmlBuilder.add("SCBSUPP_USER_ID", scbsupp.getUserId());
        dmlBuilder.add("SCBSUPP_DATA_ORIGIN", scbsupp.getDataOrigin());
        dmlBuilder.add("SCBSUPP_CUDA_CODE", scbsupp.getCudaCode());
    }

    public int insert(Scbsupp scbsupp) {
        DmlBuilder dmlBuilder = new DmlBuilder("SCBSUPP");
        dmlBuilder.add("SCBSUPP_SUBJ_CODE", scbsupp.getSubjCode());
        dmlBuilder.add("SCBSUPP_CRSE_NUMB", scbsupp.getCrseNumb());
        dmlBuilder.add("SCBSUPP_EFF_TERM", scbsupp.getEffTerm());
        addCommonColumns(dmlBuilder, scbsupp);
        return this.jdbcTemplate.update(dmlBuilder.getInsert(), dmlBuilder.getParamValueArray());
    }

    public int update(Scbsupp scbsupp) {
        StringBuffer sb = new StringBuffer();
        DmlBuilder dmlBuilder = new DmlBuilder("SCBSUPP");
        // First build and add the UPDATE statement
        addCommonColumns(dmlBuilder, scbsupp);
        sb.append(dmlBuilder.getUpdate());
        Object[] paramValues = dmlBuilder.getParamValueArray();
        // Then build and add the WHERE clause
        dmlBuilder.clear();
        dmlBuilder.add("SCBSUPP_SUBJ_CODE", scbsupp.getSubjCode());
        dmlBuilder.add("SCBSUPP_CRSE_NUMB", scbsupp.getCrseNumb());
        dmlBuilder.add("SCBSUPP_EFF_TERM", scbsupp.getEffTerm());
        sb.append("\n");
        sb.append(dmlBuilder.getWhereClause());
        // Concatenate the parameter values needed by the WHERE clause to the parameter values array we already had for the UPDATE statement
        paramValues = ObjectArrays.concat(paramValues, dmlBuilder.getParamValueArray(), Object.class);
        return this.jdbcTemplate.update(sb.toString(), paramValues);
    }


    private static final class ScbsuppMapper implements RowMapper<Scbsupp> {
        public Scbsupp mapRow(ResultSet rs, int rowNum) throws SQLException {
            Scbsupp scbsupp = new Scbsupp();
            // Common columns
            scbsupp.setSubjCode(rs.getString("SCBSUPP_SUBJ_CODE"));
            scbsupp.setCrseNumb(rs.getString("SCBSUPP_CRSE_NUMB"));
            scbsupp.setVpdiCode(rs.getString("SCBSUPP_VPDI_CODE"));
            scbsupp.setSurrogateId(rs.getBigDecimal("SCBSUPP_SURROGATE_ID"));
            scbsupp.setVersion(rs.getBigDecimal("SCBSUPP_VERSION"));
            scbsupp.setDataOrigin(rs.getString("SCBSUPP_DATA_ORIGIN"));
            scbsupp.setUserId(rs.getString("SCBSUPP_USER_ID"));
            scbsupp.setActivityDate(rs.getTimestamp("SCBSUPP_ACTIVITY_DATE"));
            // Unique to this table
            scbsupp.setEffTerm(rs.getString("SCBSUPP_EFF_TERM"));
            scbsupp.setCudaCode(rs.getString("SCBSUPP_CUDA_CODE"));
            return scbsupp;
        }
    }
}
