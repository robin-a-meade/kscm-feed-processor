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
public class ScrschdDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "bannerJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;

    /**
     *  Get versions effective at given term
     */
    public List<Scrschd> getEffective(String subjCode, String crseNumb, String effTerm) {
        String sqlEffRecord = BannerUtil.sqlEffRecord("SCRSCHD", "SCRSCHD_EFF_TERM");
        logger.trace(sqlEffRecord);
        return this.jdbcTemplate.query(sqlEffRecord, new Object[]{subjCode, crseNumb, effTerm}, new ScrschdMapper());
    }


    public int insert(Scrschd scrschd) {
        DmlBuilder ib = new DmlBuilder("SCRSCHD");
        ib.add("SCRSCHD_SUBJ_CODE", scrschd.getSubjCode());
        ib.add("SCRSCHD_CRSE_NUMB", scrschd.getCrseNumb());
        ib.add("SCRSCHD_EFF_TERM", scrschd.getEffTerm());
        ib.add("SCRSCHD_ACTIVITY_DATE", scrschd.getActivityDate());
        ib.add("SCRSCHD_USER_ID", scrschd.getUserId());
        ib.add("SCRSCHD_DATA_ORIGIN", scrschd.getDataOrigin());
        ib.add("SCRSCHD_SCHD_CODE", scrschd.getSchdCode());
        return this.jdbcTemplate.update(ib.getInsert(), ib.getParamValueArray());
    }

    public int delete(String subjCode, String crseNumb, String effTerm) {
        StringBuffer sb = new StringBuffer();
        DmlBuilder dmlBuilder = new DmlBuilder("SCRSCHD");
        dmlBuilder.add("SCRSCHD_SUBJ_CODE", subjCode);
        dmlBuilder.add("SCRSCHD_CRSE_NUMB", crseNumb);
        dmlBuilder.add("SCRSCHD_EFF_TERM", effTerm);
        sb.append(dmlBuilder.getDelete());
        sb.append("\n");
        sb.append(dmlBuilder.getWhereClause());
        return this.jdbcTemplate.update(sb.toString(), dmlBuilder.getParamValueArray());
    }

    private static final class ScrschdMapper implements RowMapper<Scrschd> {
        public Scrschd mapRow(ResultSet rs, int rowNum) throws SQLException {
            Scrschd scrschd = new Scrschd();
            // Common columns
            scrschd.setSubjCode(rs.getString("SCRSCHD_SUBJ_CODE"));
            scrschd.setCrseNumb(rs.getString("SCRSCHD_CRSE_NUMB"));
            scrschd.setVpdiCode(rs.getString("SCRSCHD_VPDI_CODE"));
            scrschd.setSurrogateId(rs.getBigDecimal("SCRSCHD_SURROGATE_ID"));
            scrschd.setVersion(rs.getBigDecimal("SCRSCHD_VERSION"));
            scrschd.setDataOrigin(rs.getString("SCRSCHD_DATA_ORIGIN"));
            scrschd.setUserId(rs.getString("SCRSCHD_USER_ID"));
            scrschd.setActivityDate(rs.getTimestamp("SCRSCHD_ACTIVITY_DATE"));
            // Unique to this table
            scrschd.setEffTerm(rs.getString("SCRSCHD_EFF_TERM"));
            scrschd.setSchdCode(rs.getString("SCRSCHD_SCHD_CODE"));
            scrschd.setWorkload(rs.getBigDecimal("SCRSCHD_WORKLOAD"));
            scrschd.setMaxEnrl(rs.getBigDecimal("SCRSCHD_MAX_ENRL"));
            scrschd.setAdjWorkload(rs.getBigDecimal("SCRSCHD_ADJ_WORKLOAD"));
            scrschd.setInsmCode(rs.getString("SCRSCHD_INSM_CODE"));
            return scrschd;
        }
    }
}
