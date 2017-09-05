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
public class ScrtextDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "bannerJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;

    /**
     *  Get versions effective at given term
     */
    public List<Scrtext> getEffective(String subjCode, String crseNumb, String effTerm) {
        String sqlEffRecord = BannerUtil.sqlEffRecord("SCRTEXT", "SCRTEXT_EFF_TERM");
        logger.trace(sqlEffRecord);
        return this.jdbcTemplate.query(sqlEffRecord, new Object[]{subjCode, crseNumb, effTerm}, new ScrtextMapper());
    }

    public int insert(Scrtext scrtext) {
        DmlBuilder dmlBuilder = new DmlBuilder("SCRTEXT");
        dmlBuilder.add("SCRTEXT_SUBJ_CODE", scrtext.getSubjCode());
        dmlBuilder.add("SCRTEXT_CRSE_NUMB", scrtext.getCrseNumb());
        dmlBuilder.add("SCRTEXT_EFF_TERM", scrtext.getEffTerm());
        dmlBuilder.add("SCRTEXT_ACTIVITY_DATE", scrtext.getActivityDate());
        dmlBuilder.add("SCRTEXT_USER_ID", scrtext.getUserId());
        dmlBuilder.add("SCRTEXT_DATA_ORIGIN", scrtext.getDataOrigin());
        dmlBuilder.add("SCRTEXT_TEXT_CODE", scrtext.getTextCode());
        dmlBuilder.add("SCRTEXT_TEXT", scrtext.getText());
        dmlBuilder.add("SCRTEXT_SEQNO", scrtext.getSeqno());

        return this.jdbcTemplate.update(dmlBuilder.getInsert(), dmlBuilder.getParamValueArray());
    }

    public int delete(String subjCode, String crseNumb, String effTerm) {
        StringBuffer sb = new StringBuffer();
        DmlBuilder dmlBuilder = new DmlBuilder("SCRTEXT");
        dmlBuilder.add("SCRTEXT_SUBJ_CODE", subjCode);
        dmlBuilder.add("SCRTEXT_CRSE_NUMB", crseNumb);
        dmlBuilder.add("SCRTEXT_EFF_TERM", effTerm);
        sb.append(dmlBuilder.getDelete());
        sb.append("\n");
        sb.append(dmlBuilder.getWhereClause());
        return this.jdbcTemplate.update(sb.toString(), dmlBuilder.getParamValueArray());
    }


    private static final class ScrtextMapper implements RowMapper<Scrtext> {
        public Scrtext mapRow(ResultSet rs, int rowNum) throws SQLException {
            Scrtext scrtext = new Scrtext();
            // Common columns
            scrtext.setSubjCode(rs.getString("SCRTEXT_SUBJ_CODE"));
            scrtext.setCrseNumb(rs.getString("SCRTEXT_CRSE_NUMB"));
            scrtext.setVpdiCode(rs.getString("SCRTEXT_VPDI_CODE"));
            scrtext.setSurrogateId(rs.getBigDecimal("SCRTEXT_SURROGATE_ID"));
            scrtext.setVersion(rs.getBigDecimal("SCRTEXT_VERSION"));
            scrtext.setDataOrigin(rs.getString("SCRTEXT_DATA_ORIGIN"));
            scrtext.setUserId(rs.getString("SCRTEXT_USER_ID"));
            scrtext.setActivityDate(rs.getTimestamp("SCRTEXT_ACTIVITY_DATE"));
            // Unique to this table
            scrtext.setEffTerm(rs.getString("SCRTEXT_EFF_TERM"));
            scrtext.setTextCode(rs.getString("SCRTEXT_TEXT_CODE"));
            scrtext.setText(rs.getString("SCRTEXT_TEXT"));
            scrtext.setSeqno(rs.getInt("SCRTEXT_SEQNO"));
            return scrtext;
        }
    }
}
