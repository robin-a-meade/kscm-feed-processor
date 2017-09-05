package edu.hawaii.kscmfeedprocessor.banner;

import com.google.common.collect.ObjectArrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ScrfeesDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "bannerJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;

    /**
     *  Get versions effective at given term
     */
    public List<Scrfees> getEffective(String subjCode, String crseNumb, String effTerm) {
        String sqlEffRecord = BannerUtil.sqlEffRecord("SCRFEES", "SCRFEES_EFF_TERM");
        logger.trace(sqlEffRecord);
        return this.jdbcTemplate.query(sqlEffRecord, new Object[]{subjCode, crseNumb, effTerm}, new ScrfeesMapper());
    }

    public int insert(Scrfees scrfees) {
        DmlBuilder dmlBuilder = new DmlBuilder("SCRFEES");
        dmlBuilder.add("SCRFEES_SUBJ_CODE", scrfees.getSubjCode());
        dmlBuilder.add("SCRFEES_CRSE_NUMB", scrfees.getCrseNumb());
        dmlBuilder.add("SCRFEES_EFF_TERM", scrfees.getEffTerm());
        dmlBuilder.add("SCRFEES_ACTIVITY_DATE", scrfees.getActivityDate());
        dmlBuilder.add("SCRFEES_USER_ID", scrfees.getUserId());
        dmlBuilder.add("SCRFEES_DATA_ORIGIN", scrfees.getDataOrigin());
        dmlBuilder.add("SCRFEES_DETL_CODE", scrfees.getDetlCode());
        dmlBuilder.add("SCRFEES_FEE_AMOUNT", scrfees.getFeeAmount());
        dmlBuilder.add("SCRFEES_SEQ_NO", scrfees.getSeqno());
        dmlBuilder.add("SCRFEES_FTYP_CODE", scrfees.getFtypCode());
        dmlBuilder.add("SCRFEES_FEE_IND_IND", scrfees.getFeeIndInd());
        dmlBuilder.add("SCRFEES_FEE_IND", scrfees.getFeeInd());

        return this.jdbcTemplate.update(dmlBuilder.getInsert(), dmlBuilder.getParamValueArray());
    }

    public int delete(String subjCode, String crseNumb, String effTerm) {
        StringBuffer sb = new StringBuffer();
        DmlBuilder dmlBuilder = new DmlBuilder("SCRFEES");
        dmlBuilder.add("SCRFEES_SUBJ_CODE", subjCode);
        dmlBuilder.add("SCRFEES_CRSE_NUMB", crseNumb);
        dmlBuilder.add("SCRFEES_EFF_TERM", effTerm);
        sb.append(dmlBuilder.getDelete());
        sb.append("\n");
        sb.append(dmlBuilder.getWhereClause());
        return this.jdbcTemplate.update(sb.toString(), dmlBuilder.getParamValueArray());
    }


    private static final class ScrfeesMapper implements RowMapper<Scrfees> {
        public Scrfees mapRow(ResultSet rs, int rowNum) throws SQLException {
            Scrfees scrfees = new Scrfees();
            // Common columns
            scrfees.setSubjCode(rs.getString("SCRFEES_SUBJ_CODE"));
            scrfees.setCrseNumb(rs.getString("SCRFEES_CRSE_NUMB"));
            scrfees.setVpdiCode(rs.getString("SCRFEES_VPDI_CODE"));
            scrfees.setSurrogateId(rs.getBigDecimal("SCRFEES_SURROGATE_ID"));
            scrfees.setVersion(rs.getBigDecimal("SCRFEES_VERSION"));
            scrfees.setDataOrigin(rs.getString("SCRFEES_DATA_ORIGIN"));
            scrfees.setUserId(rs.getString("SCRFEES_USER_ID"));
            scrfees.setActivityDate(rs.getTimestamp("SCRFEES_ACTIVITY_DATE"));
            // Unique to this table
            scrfees.setEffTerm(rs.getString("SCRFEES_EFF_TERM"));
            scrfees.setDetlCode(rs.getString("SCRFEES_DETL_CODE"));
            scrfees.setFeeAmount(rs.getBigDecimal("SCRFEES_FEE_AMOUNT"));
            scrfees.setSeqno(rs.getInt("SCRFEES_SEQ_NO"));
            scrfees.setFtypCode(rs.getString("SCRFEES_FTYP_CODE"));
            return scrfees;
        }
    }
}
