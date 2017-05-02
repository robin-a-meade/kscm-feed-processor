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
public class ScrlevlDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "bannerJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;

    /**
     *  Get versions effective at given term
     */
    public List<Scrlevl> getEffective(String subjCode, String crseNumb, String effTerm) {
        String sqlEffRecord = BannerUtil.sqlEffRecord("SCRLEVL", "SCRLEVL_EFF_TERM");
        logger.trace(sqlEffRecord);
        return this.jdbcTemplate.query(sqlEffRecord, new Object[]{subjCode, crseNumb, effTerm}, new ScrlevlMapper());
    }

    public int insert(Scrlevl scrlevl) {
        DmlBuilder dmlBuilder = new DmlBuilder("SCRLEVL");
        dmlBuilder.add("SCRLEVL_SUBJ_CODE", scrlevl.getSubjCode());
        dmlBuilder.add("SCRLEVL_CRSE_NUMB", scrlevl.getCrseNumb());
        dmlBuilder.add("SCRLEVL_EFF_TERM", scrlevl.getEffTerm());
        dmlBuilder.add("SCRLEVL_ACTIVITY_DATE", scrlevl.getActivityDate());
        dmlBuilder.add("SCRLEVL_USER_ID", scrlevl.getUserId());
        dmlBuilder.add("SCRLEVL_DATA_ORIGIN", scrlevl.getDataOrigin());
        dmlBuilder.add("SCRLEVL_LEVL_CODE", scrlevl.getLevlCode());
        return this.jdbcTemplate.update(dmlBuilder.getInsert(), dmlBuilder.getParamValueArray());
    }

    public int delete(String subjCode, String crseNumb, String effTerm) {
        StringBuffer sb = new StringBuffer();
        DmlBuilder dmlBuilder = new DmlBuilder("SCRLEVL");
        dmlBuilder.add("SCRLEVL_SUBJ_CODE", subjCode);
        dmlBuilder.add("SCRLEVL_CRSE_NUMB", crseNumb);
        dmlBuilder.add("SCRLEVL_EFF_TERM", effTerm);
        sb.append(dmlBuilder.getDelete());
        sb.append("\n");
        sb.append(dmlBuilder.getWhereClause());
        return this.jdbcTemplate.update(sb.toString(), dmlBuilder.getParamValueArray());
    }


    private static final class ScrlevlMapper implements RowMapper<Scrlevl> {
        public Scrlevl mapRow(ResultSet rs, int rowNum) throws SQLException {
            Scrlevl scrlevl = new Scrlevl();
            // Common columns
            scrlevl.setSubjCode(rs.getString("SCRLEVL_SUBJ_CODE"));
            scrlevl.setCrseNumb(rs.getString("SCRLEVL_CRSE_NUMB"));
            scrlevl.setVpdiCode(rs.getString("SCRLEVL_VPDI_CODE"));
            scrlevl.setSurrogateId(rs.getBigDecimal("SCRLEVL_SURROGATE_ID"));
            scrlevl.setVersion(rs.getBigDecimal("SCRLEVL_VERSION"));
            scrlevl.setDataOrigin(rs.getString("SCRLEVL_DATA_ORIGIN"));
            scrlevl.setUserId(rs.getString("SCRLEVL_USER_ID"));
            scrlevl.setActivityDate(rs.getTimestamp("SCRLEVL_ACTIVITY_DATE"));
            // Unique to this table
            scrlevl.setEffTerm(rs.getString("SCRLEVL_EFF_TERM"));
            scrlevl.setLevlCode(rs.getString("SCRLEVL_LEVL_CODE"));
            return scrlevl;
        }
    }
}
