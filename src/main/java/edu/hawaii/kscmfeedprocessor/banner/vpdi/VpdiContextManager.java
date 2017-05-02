package edu.hawaii.kscmfeedprocessor.banner.vpdi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.io.Closeable;

/**
 *
 */
public class VpdiContextManager implements AutoCloseable {

    private static final Logger staticLogger = LoggerFactory.getLogger("hello.VpdiContextManager static");

    /**
     *  Constructor
     */
    public VpdiContextManager(DataSource dataSource) {
        vpdiContextStack = new VpdiContextStack(dataSource);
    }


    public AutoCloseable push(MultiUseContext multiUseContext) {
        this.vpdiContextStack.push(multiUseContext);
        return this;
    }

    public AutoCloseable push(MultiUseContext multiUseContext, String instCode) {
        this.vpdiContextStack.push(multiUseContext, instCode);
        return this;
    }

    public VpdiContext pop() {
        return this.vpdiContextStack.pop();
    }

    public static VpdiContextManager get(DataSource dataSource) {
        staticLogger.trace("Getting VpdiContextManager instance for thread {}", Thread.currentThread().getName());
        VpdiContextManager vpdiContextManager;
        vpdiContextManager = (VpdiContextManager) TransactionSynchronizationManager.getResource(VpdiContextManager.class);
        if (vpdiContextManager != null) {
            staticLogger.trace("Returning existing.");
            staticLogger.trace("Stack size: {}", + vpdiContextManager.getStackSize() );
            return vpdiContextManager;
        }
        vpdiContextManager = new VpdiContextManager(dataSource);
        TransactionSynchronizationManager.bindResource(VpdiContextManager.class, vpdiContextManager);
        staticLogger.trace("Returning new");
        return vpdiContextManager;
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final VpdiContextStack vpdiContextStack;

    @Override
    public void close() {
        logger.trace("Close called");
        VpdiContext vpdiContext = this.pop();
        logger.trace("Closed by popping: " + vpdiContext);
    }

    public int getStackSize() {
        return vpdiContextStack.size();
    }

}
