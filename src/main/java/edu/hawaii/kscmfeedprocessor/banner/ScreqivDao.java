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
public class ScreqivDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "bannerJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;

    /**
     *  Get versions effective at given term
     */
    public List<Screqiv> getEffective(String subjCode, String crseNumb, String effTerm) {
        String sqlEffRecord = BannerUtil.sqlEffRecord("SCREQIV", "SCREQIV_EFF_TERM");
        logger.trace(sqlEffRecord);
        return this.jdbcTemplate.query(sqlEffRecord, new Object[]{subjCode, crseNumb, effTerm}, new ScreqivMapper());
    }

    public int insert(Screqiv screqiv) {
        DmlBuilder dmlBuilder = new DmlBuilder("SCREQIV");
        dmlBuilder.add("SCREQIV_SUBJ_CODE", screqiv.getSubjCode());
        dmlBuilder.add("SCREQIV_CRSE_NUMB", screqiv.getCrseNumb());
        dmlBuilder.add("SCREQIV_EFF_TERM", screqiv.getEffTerm());
        dmlBuilder.add("SCREQIV_ACTIVITY_DATE", screqiv.getActivityDate());
        dmlBuilder.add("SCREQIV_USER_ID", screqiv.getUserId());
        dmlBuilder.add("SCREQIV_DATA_ORIGIN", screqiv.getDataOrigin());
        dmlBuilder.add("SCREQIV_SUBJ_CODE_EQIV", screqiv.getSubjCodeEqiv());
        dmlBuilder.add("SCREQIV_CRSE_NUMB_EQIV", screqiv.getCrseNumbEqiv());
        dmlBuilder.add("SCREQIV_START_TERM", screqiv.getStartTerm());
        dmlBuilder.add("SCREQIV_END_TERM", screqiv.getEndTerm());

        return this.jdbcTemplate.update(dmlBuilder.getInsert(), dmlBuilder.getParamValueArray());
    }

    public int delete(String subjCode, String crseNumb, String effTerm) {
        StringBuffer sb = new StringBuffer();
        DmlBuilder dmlBuilder = new DmlBuilder("SCREQIV");
        dmlBuilder.add("SCREQIV_SUBJ_CODE", subjCode);
        dmlBuilder.add("SCREQIV_CRSE_NUMB", crseNumb);
        dmlBuilder.add("SCREQIV_EFF_TERM", effTerm);
        sb.append(dmlBuilder.getDelete());
        sb.append("\n");
        sb.append(dmlBuilder.getWhereClause());
        return this.jdbcTemplate.update(sb.toString(), dmlBuilder.getParamValueArray());
    }


    private static final class ScreqivMapper implements RowMapper<Screqiv> {
        public Screqiv mapRow(ResultSet rs, int rowNum) throws SQLException {
            Screqiv screqiv = new Screqiv();
            // Common columns
            screqiv.setSubjCode(rs.getString("SCREQIV_SUBJ_CODE"));
            screqiv.setCrseNumb(rs.getString("SCREQIV_CRSE_NUMB"));
            screqiv.setVpdiCode(rs.getString("SCREQIV_VPDI_CODE"));
            screqiv.setSurrogateId(rs.getBigDecimal("SCREQIV_SURROGATE_ID"));
            screqiv.setVersion(rs.getBigDecimal("SCREQIV_VERSION"));
            screqiv.setDataOrigin(rs.getString("SCREQIV_DATA_ORIGIN"));
            screqiv.setUserId(rs.getString("SCREQIV_USER_ID"));
            screqiv.setActivityDate(rs.getTimestamp("SCREQIV_ACTIVITY_DATE"));
            // Unique to this table
            screqiv.setEffTerm(rs.getString("SCREQIV_EFF_TERM"));
            screqiv.setSubjCodeEqiv(rs.getString("SCREQIV_SUBJ_CODE_EQIV"));
            screqiv.setCrseNumbEqiv(rs.getString("SCREQIV_CRSE_NUMB_EQIV"));
            screqiv.setStartTerm(rs.getString("SCREQIV_START_TERM"));
            screqiv.setEndTerm(rs.getString("SCREQIV_END_TERM"));
            return screqiv;
        }
    }
}
