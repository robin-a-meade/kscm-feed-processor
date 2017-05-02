package edu.hawaii.kscmfeedprocessor.banner.vpdi;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 */
public class VpdiContext {

    final private MultiUseContext multiUseContext;

    final private String instCode;

    public VpdiContext(MultiUseContext multiUseContext, String instCode) {
        this.multiUseContext = multiUseContext;
        this.instCode = instCode;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                .append(multiUseContext)
                .append(instCode)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VpdiContext))
            return false;
        if (obj == this)
            return true;

        VpdiContext rhs = (VpdiContext) obj;
        return new EqualsBuilder()
                // if deriving: appendSuper(super.equals(obj)).
                .append(multiUseContext, rhs.multiUseContext)
                .append(instCode, rhs.instCode)
                .isEquals();
    }

    public MultiUseContext getMultiUseContext() {
        return multiUseContext;
    }

    public String getInstCode() {
        return instCode;
    }

    @Override
    public String toString() {
        switch (multiUseContext) {
            case RESTRICT:
                return String.format("RESTRICT %s", instCode);
            case OVERRIDE:
                return String.format("OVERRIDE %s", instCode);
            case OVERRIDEALL:
                return String.format("OVERRIDEALL");
            default:
                throw new IllegalStateException("Unknown enumeration value " + multiUseContext);
        }
    }

}
