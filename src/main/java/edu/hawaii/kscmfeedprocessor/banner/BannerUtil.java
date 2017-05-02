package edu.hawaii.kscmfeedprocessor.banner;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Builds SQL to retrieve the record in effect at given term. This is for course catalog tables that have subjCode, crseNumb, and effTerm columns.
 */
public class BannerUtil {

    private static final Logger staticLogger = LoggerFactory.getLogger(BannerUtil.class);

    // Stored in properties file: src/main/resources/edu/hawaii/kscm/banner/BannerUtil.properties
    private static final String sqlForEffRecord;

    // Stored in properties file: src/main/resources/edu/hawaii/kscm/banner/BannerUtil.properties
    private static final String sqlForFutureRecord;

    /**
     * General utility method for loading the properties of a given class.
     * <p>
     * The properties file is assumed to be on classpath in same package as the class
     * and have same name as the class, but with .properties extension.
     */
    public static Properties getPropertiesForClass(Class<?> clazz) {
        Properties props;
        final String resourcePath = String.format("%s.properties", clazz.getSimpleName());
        staticLogger.trace("resourcePath: {}", resourcePath);
        Resource resource = new ClassPathResource(resourcePath, clazz);
        try {
            props = PropertiesLoaderUtils.loadProperties(resource);
        } catch (Throwable t) {
            String msg = String.format("Exception occurred while loading resource path '%s'", resourcePath);
            staticLogger.info(msg, t);
            throw new RuntimeException(t);
        }
        return props;
    }

    static {
        staticLogger.trace("Static Initializer");
        Properties props = getPropertiesForClass(BannerUtil.class);
        sqlForEffRecord = props.getProperty("sqlForEffRecord");
        staticLogger.trace("{}: {}", "sqlForEffRecord", sqlForEffRecord);
        sqlForFutureRecord = props.getProperty("sqlForFutureRecord");
        staticLogger.trace("{}: {}", "sqlForFutureRecord", sqlForFutureRecord);
    }

    public static String sqlEffRecord(String tableName, String effTermColumnName) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("table_name", tableName);
        valuesMap.put("eff_term_column_name", effTermColumnName);
        String resolvedString = new StrSubstitutor(valuesMap).replace(sqlForEffRecord);
        return resolvedString;
    }

    public static String sqlFutureRecord(String tableName, String effTermColumnName) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("table_name", tableName);
        valuesMap.put("eff_term_column_name", effTermColumnName);
        String resolvedString = new StrSubstitutor(valuesMap).replace(sqlForFutureRecord);
        return resolvedString;
    }

    public static void dumpToLog(Scbcrse scbcrse) {
        staticLogger.trace("Scbcrse:");
        staticLogger.trace("  subjCode: {}", scbcrse.getSubjCode());
        staticLogger.trace("  crseNumb: {}", scbcrse.getCrseNumb());
        staticLogger.trace("  effTerm: {}", scbcrse.getEffTerm());
        staticLogger.trace("  vpdiCode: {}", scbcrse.getVpdiCode());
        staticLogger.trace("  title: {}", scbcrse.getTitle());
        staticLogger.trace("  collCode: {}", scbcrse.getCollCode());
        staticLogger.trace("  divsCode: {}", scbcrse.getDivsCode());
        staticLogger.trace("  deptCode: {}", scbcrse.getDeptCode());
        staticLogger.trace("  cstaCode: {}", scbcrse.getCstaCode());
        staticLogger.trace("  aprvCode: {}", scbcrse.getAprvCode());
        staticLogger.trace("  repeatLimit: {}", scbcrse.getRepeatLimit());
        staticLogger.trace("  maxRptUnits: {}", scbcrse.getMaxRptUnits());
        staticLogger.trace("  prereqChkMethodCde: {}", scbcrse.getPrereqChkMethodCde());
        staticLogger.trace("  dataOrigin: {}", scbcrse.getDataOrigin());
        staticLogger.trace("  userId: {}", scbcrse.getUserId());
        staticLogger.trace("  activityDate: {}", scbcrse.getActivityDate());
        // creditHr
        staticLogger.trace("  creditHrInd: {}", scbcrse.getCreditHrInd());
        staticLogger.trace("  creditHrLow: {}", scbcrse.getCreditHrLow());
        staticLogger.trace("  creditHrHigh: {}", scbcrse.getCreditHrHigh());
        // lecHr
        staticLogger.trace("  lecHrInd: {}", scbcrse.getLecHrInd());
        staticLogger.trace("  lecHrLow: {}", scbcrse.getLecHrLow());
        staticLogger.trace("  lecHrHigh: {}", scbcrse.getLecHrHigh());
        // labHr
        staticLogger.trace("  labHrInd: {}", scbcrse.getLabHrInd());
        staticLogger.trace("  labHrLow: {}", scbcrse.getLabHrLow());
        staticLogger.trace("  labHrHigh: {}", scbcrse.getLabHrHigh());
        // othHr
        staticLogger.trace("  othHrInd: {}", scbcrse.getOthHrInd());
        staticLogger.trace("  othHrLow: {}", scbcrse.getOthHrLow());
        staticLogger.trace("  othHrHigh: {}", scbcrse.getOthHrHigh());
        // billHr
        staticLogger.trace("  billHrInd: {}", scbcrse.getBillHrInd());
        staticLogger.trace("  billHrLow: {}", scbcrse.getBillHrLow());
        staticLogger.trace("  billHrHigh: {}", scbcrse.getBillHrHigh());
        // contHr
        staticLogger.trace("  contHrInd: {}", scbcrse.getContHrInd());
        staticLogger.trace("  contHrLow: {}", scbcrse.getContHrLow());
        staticLogger.trace("  contHrHigh: {}", scbcrse.getContHrHigh());
    };
}
