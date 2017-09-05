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
public class ScrintgDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "bannerJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;

    /**
     *  Get versions effective at given term
     */
    public List<Scrintg> getEffective(String subjCode, String crseNumb, String effTerm) {
        String sqlEffRecord = BannerUtil.sqlEffRecord("SCRINTG", "SCRINTG_TERM_CODE_EFF");
        logger.trace(sqlEffRecord);
        return this.jdbcTemplate.query(sqlEffRecord, new Object[]{subjCode, crseNumb, effTerm}, new ScrintgMapper());
    }

    public int insert(Scrintg scrintg) {
        DmlBuilder dmlBuilder = new DmlBuilder("SCRINTG");
        dmlBuilder.add("SCRINTG_SUBJ_CODE", scrintg.getSubjCode());
        dmlBuilder.add("SCRINTG_CRSE_NUMB", scrintg.getCrseNumb());
        dmlBuilder.add("SCRINTG_TERM_CODE_EFF", scrintg.getTermCodeEff());
        dmlBuilder.add("SCRINTG_ACTIVITY_DATE", scrintg.getActivityDate());
        dmlBuilder.add("SCRINTG_USER_ID", scrintg.getUserId());
        dmlBuilder.add("SCRINTG_DATA_ORIGIN", scrintg.getDataOrigin());
        dmlBuilder.add("SCRINTG_INTG_CDE", scrintg.getIntgCde());
        return this.jdbcTemplate.update(dmlBuilder.getInsert(), dmlBuilder.getParamValueArray());
    }

    public int delete(String subjCode, String crseNumb, String effTerm) {
        StringBuffer sb = new StringBuffer();
        DmlBuilder dmlBuilder = new DmlBuilder("SCRINTG");
        dmlBuilder.add("SCRINTG_SUBJ_CODE", subjCode);
        dmlBuilder.add("SCRINTG_CRSE_NUMB", crseNumb);
        dmlBuilder.add("SCRINTG_TERM_CODE_EFF", effTerm);
        sb.append(dmlBuilder.getDelete());
        sb.append("\n");
        sb.append(dmlBuilder.getWhereClause());
        return this.jdbcTemplate.update(sb.toString(), dmlBuilder.getParamValueArray());
    }


    private static final class ScrintgMapper implements RowMapper<Scrintg> {
        public Scrintg mapRow(ResultSet rs, int rowNum) throws SQLException {
            Scrintg scrintg = new Scrintg();
            // Common columns
            scrintg.setSubjCode(rs.getString("SCRINTG_SUBJ_CODE"));
            scrintg.setCrseNumb(rs.getString("SCRINTG_CRSE_NUMB"));
            scrintg.setVpdiCode(rs.getString("SCRINTG_VPDI_CODE"));
            scrintg.setSurrogateId(rs.getBigDecimal("SCRINTG_SURROGATE_ID"));
            scrintg.setVersion(rs.getBigDecimal("SCRINTG_VERSION"));
            scrintg.setDataOrigin(rs.getString("SCRINTG_DATA_ORIGIN"));
            scrintg.setUserId(rs.getString("SCRINTG_USER_ID"));
            scrintg.setActivityDate(rs.getTimestamp("SCRINTG_ACTIVITY_DATE"));
            // Unique to this table
            scrintg.setTermCodeEff(rs.getString("SCRINTG_TERM_CODE_EFF"));
            scrintg.setIntgCde(rs.getString("SCRINTG_INTG_CDE"));
            return scrintg;
        }
    }
}
