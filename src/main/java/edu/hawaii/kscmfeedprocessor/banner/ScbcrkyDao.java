package edu.hawaii.kscmfeedprocessor.banner;

import edu.hawaii.kscmfeedprocessor.banner.vpdi.MultiUseContext;
import edu.hawaii.kscmfeedprocessor.banner.vpdi.VpdiContextManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;


@Repository
public class ScbcrkyDao {

    @Resource(name = "bannerJdbcTemplate")
    protected JdbcTemplate jdbc;

    @Resource(name = "bannerDataSource")
    private DataSource dataSource;

    /**
     * gets Scbcrky record
     * @param subjCode
     * @param crseNumb
     * @return Scbcrky or null if not found
     */
    public Scbcrky get(String subjCode, String crseNumb) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM Scbcrky \n");
        sb.append("WHERE SCBCRKY_SUBJ_CODE = ? \n");
        sb.append("AND SCBCRKY_CRSE_NUMB = ? \n");

        try {
            return this.jdbc.queryForObject(sb.toString(), new Object[]{subjCode, crseNumb}, new
                    SCBCRKYMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    public int insert(String subjCode, String crseNumb, String effTerm, String userId, String dataOrigin) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO SCBCRKY\n");
        sb.append("(SCBCRKY_SUBJ_CODE, SCBCRKY_CRSE_NUMB, SCBCRKY_TERM_CODE_START, SCBCRKY_TERM_CODE_END, \n");
        sb.append(" SCBCRKY_ACTIVITY_DATE, SCBCRKY_USER_ID, SCBCRKY_DATA_ORIGIN)\n");
        sb.append("VALUES (?, ?, ?, '999999', sysdate, ?, ?)\n");
        return this.jdbc.update(sb.toString(), subjCode, crseNumb, effTerm, userId, dataOrigin);
    }

    private VpdiContextManager getVpdiContextManager() {
        return VpdiContextManager.get(dataSource);
    }

    @Transactional
    public void testTransaction() throws Exception {
        try (AutoCloseable c = getVpdiContextManager().push(MultiUseContext.OVERRIDE, "WOA")) {
            int count;
            count = insert("ACC", "999", "201710", "KSCMUSER", "KSCM");
            System.out.println("count: " + count);
            throw new RuntimeException("Trigger a rollback!");

        }
    }

    private static final class SCBCRKYMapper implements RowMapper<Scbcrky> {
        public Scbcrky mapRow(ResultSet rs, int rowNum) throws SQLException {
            Scbcrky scbcrky = new Scbcrky();
            // Common columns
            scbcrky.setVpdiCode(rs.getString("SCBCRKY_VPDI_CODE"));
            scbcrky.setSubjCode(rs.getString("SCBCRKY_SUBJ_CODE"));
            scbcrky.setCrseNumb(rs.getString("SCBCRKY_CRSE_NUMB"));
            scbcrky.setUserId(rs.getString("SCBCRKY_USER_ID"));
            scbcrky.setActivityDate(rs.getTimestamp("SCBCRKY_ACTIVITY_DATE"));
            scbcrky.setSurrogateId(rs.getBigDecimal("SCBCRKY_SURROGATE_ID"));
            scbcrky.setVersion(rs.getBigDecimal("SCBCRKY_VERSION"));
            scbcrky.setDataOrigin(rs.getString("SCBCRKY_DATA_ORIGIN"));
            // Unique to this table
            scbcrky.setTermCodeStart(rs.getString("SCBCRKY_TERM_CODE_START"));
            scbcrky.setTermCodeEnd(rs.getString("SCBCRKY_TERM_CODE_END"));
            return scbcrky;
        }
    }

}
