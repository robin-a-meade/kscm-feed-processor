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
public class ScrcorqDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "bannerJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;

    /**
     *  Get versions effective at given term
     */
    public List<Scrcorq> getEffective(String subjCode, String crseNumb, String effTerm) {
        String sqlEffRecord = BannerUtil.sqlEffRecord("SCRCORQ", "SCRCORQ_EFF_TERM");
        logger.trace(sqlEffRecord);
        return this.jdbcTemplate.query(sqlEffRecord, new Object[]{subjCode, crseNumb, effTerm}, new ScrcorqMapper());
    }

    public int insert(Scrcorq scrcorq) {
        DmlBuilder dmlBuilder = new DmlBuilder("SCRCORQ");
        dmlBuilder.add("SCRCORQ_SUBJ_CODE", scrcorq.getSubjCode());
        dmlBuilder.add("SCRCORQ_CRSE_NUMB", scrcorq.getCrseNumb());
        dmlBuilder.add("SCRCORQ_EFF_TERM", scrcorq.getEffTerm());
        dmlBuilder.add("SCRCORQ_ACTIVITY_DATE", scrcorq.getActivityDate());
        dmlBuilder.add("SCRCORQ_USER_ID", scrcorq.getUserId());
        dmlBuilder.add("SCRCORQ_DATA_ORIGIN", scrcorq.getDataOrigin());
        dmlBuilder.add("SCRCORQ_SUBJ_CODE_CORQ", scrcorq.getSubjCodeCorq());
        dmlBuilder.add("SCRCORQ_CRSE_NUMB_CORQ", scrcorq.getCrseNumbCorq());

        return this.jdbcTemplate.update(dmlBuilder.getInsert(), dmlBuilder.getParamValueArray());
    }

    public int delete(String subjCode, String crseNumb, String effTerm) {
        StringBuffer sb = new StringBuffer();
        DmlBuilder dmlBuilder = new DmlBuilder("SCRCORQ");
        dmlBuilder.add("SCRCORQ_SUBJ_CODE", subjCode);
        dmlBuilder.add("SCRCORQ_CRSE_NUMB", crseNumb);
        dmlBuilder.add("SCRCORQ_EFF_TERM", effTerm);
        sb.append(dmlBuilder.getDelete());
        sb.append("\n");
        sb.append(dmlBuilder.getWhereClause());
        return this.jdbcTemplate.update(sb.toString(), dmlBuilder.getParamValueArray());
    }


    private static final class ScrcorqMapper implements RowMapper<Scrcorq> {
        public Scrcorq mapRow(ResultSet rs, int rowNum) throws SQLException {
            Scrcorq scrcorq = new Scrcorq();
            // Common columns
            scrcorq.setSubjCode(rs.getString("SCRCORQ_SUBJ_CODE"));
            scrcorq.setCrseNumb(rs.getString("SCRCORQ_CRSE_NUMB"));
            scrcorq.setVpdiCode(rs.getString("SCRCORQ_VPDI_CODE"));
            scrcorq.setSurrogateId(rs.getBigDecimal("SCRCORQ_SURROGATE_ID"));
            scrcorq.setVersion(rs.getBigDecimal("SCRCORQ_VERSION"));
            scrcorq.setDataOrigin(rs.getString("SCRCORQ_DATA_ORIGIN"));
            scrcorq.setUserId(rs.getString("SCRCORQ_USER_ID"));
            scrcorq.setActivityDate(rs.getTimestamp("SCRCORQ_ACTIVITY_DATE"));
            // Unique to this table
            scrcorq.setEffTerm(rs.getString("SCRCORQ_EFF_TERM"));
            scrcorq.setSubjCodeCorq(rs.getString("SCRCORQ_SUBJ_CODE_CORQ"));
            scrcorq.setCrseNumbCorq(rs.getString("SCRCORQ_CRSE_NUMB_CORQ"));
            return scrcorq;
        }
    }
}
