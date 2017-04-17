package edu.hawaii.kscm;

import com.google.common.base.Throwables;
import edu.hawaii.kscm.banner.SCBCRKY;
import edu.hawaii.kscm.banner.SCBCRKYDao;
import edu.hawaii.kscm.banner.SCBCRSE;
import edu.hawaii.kscm.domain.Course;
import edu.hawaii.kscm.domain.SubjectCodeOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
public class BannerService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    KscmService kscmService;

    @Autowired
    CourseConverter courseConverter;

    @Resource(name = "bannerJdbc")
    protected JdbcTemplate jdbc;

    @Autowired
    SCBCRKYDao scbcrkyDao;

    public void push(String instCode, String id) {
        Course c = kscmService.retrieveCourse(instCode, id);
        c.setInstCode(instCode);
        KscmOptions kscmOptions = kscmService.getKscmOptionsFor(instCode);
        SubjectCodeOption subjectCodeOption = kscmOptions.getSubjectCodesById().get(c.getSubjectCode());
        assert subjectCodeOption != null : c.getSubjectCode();
        String subj_code = subjectCodeOption.getName();
        logger.info("{} BannerService.push {} {}", instCode, subj_code, c.getNumber());
        SCBCRSE scbcrse;
        try {
            scbcrse = courseConverter.convert(c, instCode);
        } catch (Exception t) {
            logger.error("{} courseConverter course.id='{}'", instCode, c.getId(), t);
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("error: courseConverter: course.id='%s'", instCode, c.getId()));
            sb.append("\n\n");
            sb.append(Throwables.getStackTraceAsString(t));
            //PostBack(instCode, id, new Result(1, sb.toString()));
            return;
        }
        try {
            Result result = applyToBanner(scbcrse);
        } catch (Exception t) {
            logger.error("{} applyToBanner course.id='{}'", instCode, c.getId(), t);
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("error: applyToBanner: course.id='%s'", c.getId()));
            sb.append("\n\n");
            sb.append(Throwables.getStackTraceAsString(t));
            Result result = new Result();
            result.setStatus(1);
            result.addMsg(sb.toString());
            //PostBack(instCode, id, new Result(1, sb.toString()));
            return;
        }
        //PostBack(result);
    }

    @Transactional
    Result applyToBanner(SCBCRSE scbcrse) throws Exception {
        Result result = new Result();
        result.addMsg("applyToBanner");
        result.setStatus(0);

        /*
         * See if this is a completely new course. If so, we insert everything.
         *
         */
        SCBCRKY scbcrky = scbcrkyDao.getSCBCRKY(scbcrse.getSubjCode(), scbcrse.getCrseNumb(), scbcrse.getVpdiCode());


        if (scbcrky == null) {   // Completely new course

            // Do Inserts

        } else {
            logger.info("Retrieved SCBCRKY: {}:{}:{}:{}:{}", scbcrky.getVpdiCode(), scbcrky.getSubjCode(), scbcrky.getCrseNumb(), scbcrky
                    .getTermCodeStart(), scbcrky.getTermCodeEnd());

            /*
             * Check if this course version is within SCABASE range
             *
             */


            if (scbcrky.getTermCodeStart().compareTo(scbcrse.getEffTerm()) > 0 || scbcrky.getTermCodeEnd().compareTo(scbcrse.getEffTerm()) < 0) {


                throw new Exception(String.format("Course Start Term '%s' not in SCABASE range (%s - %s)", scbcrse.getEffTerm(), scbcrky
                        .getTermCodeStart(), scbcrky.getTermCodeEnd()));

            }

            /*
             * For each record type, update or insert depending on whether existing record's effTerm is == to new scbcrky.getEffTerm
             * More advanced logic could do comparisons to see if an update is truly needed. That can be an enhancement, later.
             *
             */

        }

        return result;
    }

    void init() {
        validateConnection();
    }


    private void validateConnection() {
        try {
            DataSource dataSource = jdbc.getDataSource();
            Connection conn = dataSource.getConnection();
            boolean isValid = conn.isValid(5);
            logger.info("DB connection {} valid", isValid ? "is" : "is not");
        } catch (SQLException e) {
            logger.error("Exception while testing whether connection is valid", e);
            throw (new RuntimeException(e));
        }
    }

}
