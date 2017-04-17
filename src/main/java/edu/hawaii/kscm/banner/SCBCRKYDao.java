package edu.hawaii.kscm.banner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;


@Repository
public class SCBCRKYDao {

    @Resource(name = "bannerJdbc")
    protected JdbcTemplate jdbc;

    /**
     * gets SCBCRKY record
     * @param subjCode
     * @param crseNumb
     * @param instCode
     * @return SCBCRKY or null if not found
     */
    public SCBCRKY getSCBCRKY(String subjCode, String crseNumb, String instCode) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM SCBCRKY \n");
        sb.append("WHERE SCBCRKY_SUBJ_CODE = ? \n");
        sb.append("AND SCBCRKY_CRSE_NUMB = ? \n");
        sb.append("AND SCBCRKY_VPDI_CODE = ? ");

        try {
            return this.jdbc.queryForObject(sb.toString(), new Object[]{subjCode, crseNumb, instCode}, new
                    SCBCRKYMapper());

        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    private static final class SCBCRKYMapper implements RowMapper<SCBCRKY> {
        public SCBCRKY mapRow(ResultSet rs, int rowNum) throws SQLException {
            SCBCRKY scbcrky = new SCBCRKY();
            scbcrky.setSubjCode(rs.getString("SCBCRKY_SUBJ_CODE"));
            scbcrky.setCrseNumb(rs.getString("SCBCRKY_CRSE_NUMB"));
            scbcrky.setVpdiCode(rs.getString("SCBCRKY_VPDI_CODE"));
            scbcrky.setTermCodeStart(rs.getString("SCBCRKY_TERM_CODE_START"));
            scbcrky.setTermCodeEnd(rs.getString("SCBCRKY_TERM_CODE_END"));
            return scbcrky;
        }
    }

}
