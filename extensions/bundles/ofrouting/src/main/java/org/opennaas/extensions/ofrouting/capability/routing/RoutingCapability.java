package org.opennaas.extensions.ofrouting.capability.routing;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.ofrouting.model.OfRoutingModel;
import org.opennaas.extensions.ofrouting.model.Route;
import org.opennaas.extensions.ofrouting.model.Switch;
import org.opennaas.extensions.ofrouting.model.Table;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public class RoutingCapability extends AbstractCapability implements IRoutingCapability {

    public static String CAPABILITY_TYPE = "routing";
    Log log = LogFactory.getLog(RoutingCapability.class);
    private String resourceId = "";

    public RoutingCapability(CapabilityDescriptor descriptor, String resourceId) {

        super(descriptor);
        this.resourceId = resourceId;
        log.debug("Built new Routing Capability");
    }

    @Override
    public void activate() throws CapabilityException {
        registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IRoutingCapability.class.getName());
        super.activate();
    }

    @Override
    public void deactivate() throws CapabilityException {
        unregisterService();
        super.deactivate();
    }

    @Override
    public String getCapabilityName() {
        return CAPABILITY_TYPE;
    }

    @Override
    public IActionSet getActionSet() throws CapabilityException {

        String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
        String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

        try {
            return Activator.getRoutingActionSetService(name, version);
        } catch (ActivatorException e) {
            throw new CapabilityException(e);
        }
    }

    @Override
    public void queueAction(IAction action) throws CapabilityException {
        getQueueManager(resourceId).queueAction(action);
    }

    /**
     * 
     * @return QueuemanagerService this capability is associated to.
     * @throws CapabilityException
     *             if desired queueManagerService could not be retrieved.
     */
    private IQueueManagerCapability getQueueManager(String resourceId) throws CapabilityException {
        try {
            return Activator.getQueueManagerService(resourceId);
        } catch (ActivatorException e) {
            throw new CapabilityException("Failed to get QueueManagerService for resource " + resourceId, e);
        }
    }

    /**
     * @param the ipSource / ipDest / SwitchInfo
     * @return the greeting message
     * 
     */
    @Override
    public String getPath(String ipSource, String ipDest, String switchip, String inputPort) throws CapabilityException {
        OfRoutingModel model = (OfRoutingModel) resource.getModel();
        if (model.getTable() == null) {
            model.setTable(new Table());
            return "null";
        }
        log.error("Path: "+ipSource+" "+ipDest+" "+switchip+" "+inputPort);
        Switch switchInfo = new Switch(inputPort, switchip);
        Route route = new Route(ipSource, ipDest, switchInfo);
        if(model.getTable().RouteExists(route)){
            return model.getTable().getOutputPort(route);
        }
        
        return "null";
    }

    /**
     * @param
     * @return the greeting message
     * 
     */
    @Override
    public String getRouteTable() throws CapabilityException {
        log.info("Get Route Table");
        OfRoutingModel model = (OfRoutingModel) resource.getModel();
        if (model.getTable() == null) {
            model.setTable(new Table());
        }

        String response = "No content";
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.writeValueAsString(model.getTable());
        } catch (IOException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    /**
     * @param the ipSource / ipDest / SwitchInfo
     * @return the greeting message
     * 
     */
    @Override
    public String putRoute(String ipSource, String ipDest, String switchip, String inputPort, String outputPort) throws CapabilityException {
        log.info("Put Route into table");
        OfRoutingModel model = (OfRoutingModel) resource.getModel();
        if (model.getTable() == null) {
            model.setTable(new Table());
        }

        Switch switchInfo = new Switch(inputPort, inputPort, outputPort, switchip);
        Route route = new Route(ipSource, ipDest, switchInfo);
        String response = model.getTable().addRoute(route);
        return response;
    }
}
