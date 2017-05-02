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
public class ScrattrDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "bannerJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;

    /**
     *  Get versions effective at given term
     */
    public List<Scrattr> getEffective(String subjCode, String crseNumb, String effTerm) {
        String sqlEffRecord = BannerUtil.sqlEffRecord("SCRATTR", "SCRATTR_EFF_TERM");
        logger.trace(sqlEffRecord);
        return this.jdbcTemplate.query(sqlEffRecord, new Object[]{subjCode, crseNumb, effTerm}, new ScrattrMapper());
    }


    public int insert(Scrattr scrattr) {
        DmlBuilder dmlBuilder = new DmlBuilder("SCRATTR");
        dmlBuilder.add("SCRATTR_SUBJ_CODE", scrattr.getSubjCode());
        dmlBuilder.add("SCRATTR_CRSE_NUMB", scrattr.getCrseNumb());
        dmlBuilder.add("SCRATTR_EFF_TERM", scrattr.getEffTerm());
        dmlBuilder.add("SCRATTR_ACTIVITY_DATE", scrattr.getActivityDate());
        dmlBuilder.add("SCRATTR_USER_ID", scrattr.getUserId());
        dmlBuilder.add("SCRATTR_DATA_ORIGIN", scrattr.getDataOrigin());
        dmlBuilder.add("SCRATTR_ATTR_CODE", scrattr.getAttrCode());
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

    private static final class ScrattrMapper implements RowMapper<Scrattr> {
        public Scrattr mapRow(ResultSet rs, int rowNum) throws SQLException {
            Scrattr scrattr = new Scrattr();
            // Common columns
            scrattr.setSubjCode(rs.getString("SCRATTR_SUBJ_CODE"));
            scrattr.setCrseNumb(rs.getString("SCRATTR_CRSE_NUMB"));
            scrattr.setVpdiCode(rs.getString("SCRATTR_VPDI_CODE"));
            scrattr.setSurrogateId(rs.getBigDecimal("SCRATTR_SURROGATE_ID"));
            scrattr.setVersion(rs.getBigDecimal("SCRATTR_VERSION"));
            scrattr.setDataOrigin(rs.getString("SCRATTR_DATA_ORIGIN"));
            scrattr.setUserId(rs.getString("SCRATTR_USER_ID"));
            scrattr.setActivityDate(rs.getTimestamp("SCRATTR_ACTIVITY_DATE"));
            // Unique to this table
            scrattr.setEffTerm(rs.getString("SCRATTR_EFF_TERM"));
            scrattr.setAttrCode(rs.getString("SCRATTR_ATTR_CODE"));
            return scrattr;
        }
    }
}
