package edu.hawaii.kscmfeedprocessor.banner;

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
public class ScrgmodDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "bannerJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;

    /**
     *  Get versions effective at given term
     */
    public List<Scrgmod> getEffective(String subjCode, String crseNumb, String effTerm) {
        String sqlEffRecord = BannerUtil.sqlEffRecord("SCRGMOD", "SCRGMOD_EFF_TERM");
        logger.trace(sqlEffRecord);
        return this.jdbcTemplate.query(sqlEffRecord, new Object[]{subjCode, crseNumb, effTerm}, new ScrgmodMapper());
    }

    public int insert(Scrgmod scrgmod) {
        DmlBuilder dmlBuilder = new DmlBuilder("SCRGMOD");
        dmlBuilder.add("SCRGMOD_SUBJ_CODE", scrgmod.getSubjCode());
        dmlBuilder.add("SCRGMOD_CRSE_NUMB", scrgmod.getCrseNumb());
        dmlBuilder.add("SCRGMOD_EFF_TERM", scrgmod.getEffTerm());
        dmlBuilder.add("SCRGMOD_ACTIVITY_DATE", scrgmod.getActivityDate());
        dmlBuilder.add("SCRGMOD_USER_ID", scrgmod.getUserId());
        dmlBuilder.add("SCRGMOD_DATA_ORIGIN", scrgmod.getDataOrigin());
        dmlBuilder.add("SCRGMOD_GMOD_CODE", scrgmod.getGmodCode());
        dmlBuilder.add("SCRGMOD_DEFAULT_IND", scrgmod.getDefaultInd());
        return this.jdbcTemplate.update(dmlBuilder.getInsert(), dmlBuilder.getParamValueArray());
    }

    public int delete(String subjCode, String crseNumb, String effTerm) {
        StringBuffer sb = new StringBuffer();
        DmlBuilder dmlBuilder = new DmlBuilder("SCRGMOD");
        dmlBuilder.add("SCRGMOD_SUBJ_CODE", subjCode);
        dmlBuilder.add("SCRGMOD_CRSE_NUMB", crseNumb);
        dmlBuilder.add("SCRGMOD_EFF_TERM", effTerm);
        sb.append(dmlBuilder.getDelete());
        sb.append("\n");
        sb.append(dmlBuilder.getWhereClause());
        return this.jdbcTemplate.update(sb.toString(), dmlBuilder.getParamValueArray());
    }

    private static final class ScrgmodMapper implements RowMapper<Scrgmod> {
        public Scrgmod mapRow(ResultSet rs, int rowNum) throws SQLException {
            Scrgmod scrgmod = new Scrgmod();
            // Common columns
            scrgmod.setSubjCode(rs.getString("SCRGMOD_SUBJ_CODE"));
            scrgmod.setCrseNumb(rs.getString("SCRGMOD_CRSE_NUMB"));
            scrgmod.setVpdiCode(rs.getString("SCRGMOD_VPDI_CODE"));
            scrgmod.setSurrogateId(rs.getBigDecimal("SCRGMOD_SURROGATE_ID"));
            scrgmod.setVersion(rs.getBigDecimal("SCRGMOD_VERSION"));
            scrgmod.setDataOrigin(rs.getString("SCRGMOD_DATA_ORIGIN"));
            scrgmod.setUserId(rs.getString("SCRGMOD_USER_ID"));
            scrgmod.setActivityDate(rs.getTimestamp("SCRGMOD_ACTIVITY_DATE"));
            // Unique to this table
            scrgmod.setEffTerm(rs.getString("SCRGMOD_EFF_TERM"));
            scrgmod.setGmodCode(rs.getString("SCRGMOD_GMOD_CODE"));
            scrgmod.setDefaultInd(rs.getString("SCRGMOD_DEFAULT_IND"));

            return scrgmod;
        }
    }
}
