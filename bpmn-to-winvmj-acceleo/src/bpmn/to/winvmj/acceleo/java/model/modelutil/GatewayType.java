package bpmn.to.winvmj.acceleo.java.model.modelutil;

import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.EventBasedGateway;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.ComplexGateway;

public enum GatewayType {

    EXCLUSIVE_GATEWAY("exclusiveGateway"),
    PARALLEL_GATEWAY("parallelGateway"),
    INCLUSIVE_GATEWAY("inclusiveGateway"),
    EVENT_BASED_GATEWAY("eventBasedGateway"),
    COMPLEX_GATEWAY("complexGateway"),
    UNKNOWN("unknown");

    private final String xmlName;

    GatewayType(String xmlName) {
        this.xmlName = xmlName;
    }

    public String getXmlName() {
        return xmlName;
    }

    public static GatewayType getGatewayType(BaseElement element) {
        if (element == null) {
            return UNKNOWN;
        }

        if (element instanceof ExclusiveGateway) {
            return EXCLUSIVE_GATEWAY;
        }
        if (element instanceof ParallelGateway) {
            return PARALLEL_GATEWAY;
        }
        if (element instanceof InclusiveGateway) {
            return INCLUSIVE_GATEWAY;
        }
        if (element instanceof EventBasedGateway) {
            return EVENT_BASED_GATEWAY;
        }
        if (element instanceof ComplexGateway) {
            return COMPLEX_GATEWAY;
        }

        return UNKNOWN;
    }
}