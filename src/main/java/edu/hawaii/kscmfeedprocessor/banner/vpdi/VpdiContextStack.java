package edu.hawaii.kscmfeedprocessor.banner.vpdi;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Properties;

/**
 *
 */
public class VpdiContextStack {

    private static final Logger staticLogger = LoggerFactory.getLogger("hello.VpdiContextStack static");

    private static final String sqlForApplyingContext;

    /**
     * General utility method for loading the properties of a given class.
     * <p>
     * The properties file is assumed to be on classpath in same package as the class
     * and have same name as the class, but with .properties extension.
     */
    static Properties getPropertiesForClass(Class<?> clazz) {
        Properties props;
        final String resourcePath = String.format("%s.properties", clazz.getSimpleName());
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
        //
        final String resourcePath = String.format("%s.properties", VpdiContextStack.class.getSimpleName());
        Properties props = getPropertiesForClass(VpdiContextStack.class);
        sqlForApplyingContext = props.getProperty("sqlForApplyingContext");
        staticLogger.trace("{}: {}", "sqlForApplyingContext", sqlForApplyingContext);
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DataSource dataSource;

    private final JdbcTemplate jdbcTemplate;

    private final Deque<VpdiContext> vpdiContextDeque = new ArrayDeque<>();

    public VpdiContextStack(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void push(MultiUseContext multiUseContext) {
        if (multiUseContext == MultiUseContext.OVERRIDEALL) {
            push(MultiUseContext.OVERRIDEALL, "ALL");
        } else {
            String msg = String.format("MultiUseContext {} requires instCode arg", multiUseContext);
            throw new RuntimeException(msg);
        }
    }

    public void push(MultiUseContext multiUseContext, String instCode) {
        VpdiContext newVpdiContext = new VpdiContext(multiUseContext, instCode);
        logger.trace("Pushing: {}", newVpdiContext);
        try {
            if (newVpdiContext.equals(vpdiContextDeque.peek())) {
                logger.trace("Same as top of stack; no need to apply.");
            } else {
                apply(newVpdiContext);
                logger.trace("Applied: {}", newVpdiContext);
            }
        } finally {
            vpdiContextDeque.push(newVpdiContext);
            logger.trace("Stack size increased to {}", vpdiContextDeque.size());
        }
    }

    public VpdiContext pop() {
        VpdiContext poppedVpdiContext = vpdiContextDeque.pop();
        logger.trace("Popped: {}", poppedVpdiContext);
        if (vpdiContextDeque.size() > 0) {
            if (poppedVpdiContext.equals(vpdiContextDeque.peek())) {
                logger.trace("Same as top of stack; no need to apply.");
            } else {
                apply(vpdiContextDeque.peek());
                logger.trace("Applied top of stack: {}", vpdiContextDeque.peek());
            }
        } else {
            logger.trace("Stack is now empty. Nothing to apply.");
        }
        logger.trace("Stack size decreased to {}", vpdiContextDeque.size());
        return poppedVpdiContext;
    }

    public int size() {
        return vpdiContextDeque.size();
    }

    // Ended up not needing this. Was able to use `JdbcTemplate.update` after all. And JdbcTemplate takes care of getting the transaction's connection.
    /*
    private Connection getConnection() {
        // We need to jump through a hoop to acquire the connection.
        // This is because, within a transaction it is necessary to use the same Connection object throughout the transaction.
        // Spring accomplishes this with magic involving ThreadLocal and Proxy.
        // Within a Transactional context, your code shouldn't call `dataSource.getConnection`, instead it should call `DataSourceUtils.getConnection`.
        // Usually, you use JdbcTemplate or other Spring-aware data access library, which insulates you from such low level things like getting a
        // connection.
        // But in rare circumstances, you might need access to the connection.
        // Like to call functions in the `G$_VPDI_SECURITY` package, because '$' is not allowed in ANSI92 identifier names.

        // How to get hold of the transaction's Connection object?
        // ========================================================
        // http://stackoverflow.com/questions/11779469
        // http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/transaction/support/TransactionSynchronizationManager.html
        // http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/jdbc/datasource/ConnectionHolder.html
        // But question: Is JdbcTemplate aware of TransactionSynchronizationManager. Must be. How? Need to research.
        // So, if I was able to use JdbcTemplate to call g$_vpdi_security.*, I wouldn't have had this problem?!!!
        // (FYI, I couldn't use JdbcTemplate CallableStatement because of the '$' character!. It isn't allowed in ANSI92 names.)

        // This works:
        //Map resourceMap = TransactionSynchronizationManager.getResourceMap();
        //ConnectionHolder connectionHolder = (ConnectionHolder) resourceMap.get(dataSource);
        //Connection conn = connectionHolder.getConnection();
        //return conn;

        // Update: JdbcTemplate uses DataSourceUtils.getConnection(getDataSource())
        // DataSourceUtils makes it easier and less fragile.
        // It is aware of TransactionSynchronizationManager.
        // https://github.com/spring-projects/spring-framework/blob/master/spring-jdbc/src/main/java/org/springframework/jdbc/core/JdbcTemplate.java
        // https://github.com/spring-projects/spring-framework/blob/master/spring-jdbc/src/main/java/org/springframework/jdbc/datasource
        // /DataSourceUtils.java
        return DataSourceUtils.getConnection(dataSource);
    }
    */

    private void apply(VpdiContext vpdiContext) {
        // Ended up not needing this. Was able to use `JdbcTemplate.update` instead.
        /*
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            CallableStatement cs = conn.prepareCall(sqlForApplyingContext);
            cs.setString(1, vpdiContext.getInstCode());
            cs.setString(2, vpdiContext.getMultiUseContext().name());
            cs.execute();
        } catch (SQLException e) {
            String msg = String.format("SQLException occurred while applying vpdiContext {%s}", vpdiContext);
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
        */

        JdbcTemplate jdbcTemplate  = new JdbcTemplate(dataSource);
        jdbcTemplate.update(sqlForApplyingContext, vpdiContext.getInstCode(), vpdiContext.getMultiUseContext().name());
    }

}
