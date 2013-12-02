package org.opennaas.extensions.pdu.capability;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapabilityLifecycle;
import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public abstract class AbstractNotQueueingCapability implements ICapabilityLifecycle {

	Log								log				= LogFactory.getLog(AbstractNotQueueingCapability.class);

	private State					state			= null;
	protected CapabilityDescriptor	descriptor;
	protected String				capabilityId	= null;
	protected IResource				resource		= null;

	protected ServiceRegistration	registration;

	public AbstractNotQueueingCapability(CapabilityDescriptor descriptor) {
		this.descriptor = descriptor;
		this.capabilityId = descriptor.getCapabilityInformation().getType();
		setState(State.INSTANTIATED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.ICapability#getCapabilityDescriptor()
	 */
	@Override
	public CapabilityDescriptor getCapabilityDescriptor() {
		return descriptor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.ICapability#setCapabilityDescriptor(org.opennaas.core.resources.descriptor.CapabilityDescriptor)
	 */
	@Override
	public void setCapabilityDescriptor(CapabilityDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.ICapability#getCapabilityInformation()
	 */
	@Override
	public Information getCapabilityInformation() {
		return descriptor.getCapabilityInformation();
	}

	/**
	 * The resource where this capability belongs
	 * 
	 * @param resource
	 */
	@Override
	public void setResource(IResource resource) {
		this.resource = resource;
	}

	// ICapabilityLifecycle methods

	/**
	 * Returns the current capability state
	 * 
	 * @return state enum object
	 */
	@Override
	public State getState() {
		return state;
	}

	/**
	 * Sets the current capability state
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * Initializes this capability, the status will be INITIALIZED, then will be ACTIVE if enabled.
	 * 
	 * @throws ResourceException
	 */
	@Override
	public void initialize() throws CapabilityException {
		setState(State.INITIALIZED);
	}

	/**
	 * Activates this capability and change state to ACTIVE.
	 * 
	 * @throws ResourceException
	 */
	@Override
	public void activate() throws CapabilityException {
		setState(State.ACTIVE);
	}

	/**
	 * Deactivate this capability and change state to INACTIVE
	 * 
	 * @throws ResourceException
	 */
	@Override
	public void deactivate() throws CapabilityException {
		setState(State.INACTIVE);
	}

	/**
	 * Prepares capability for Garbage Collection state will be SHUTDOWN until it is collected.
	 * 
	 * @throws ResourceException
	 */
	@Override
	public void shutdown() throws CapabilityException {
		setState(State.SHUTDOWN);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (getCapabilityInformation() != null) {
			builder.append("\nCapability Type: " + getCapabilityInformation().getType());
			builder.append("\nCapability Description: " + getCapabilityInformation().getDescription());
			builder.append("\nCapability Version: " + getCapabilityInformation().getName());
		}
		return builder.toString();
	}

	/**
	 * Refreshes resource model
	 */
	public abstract void resyncModel() throws Exception;

	// Service registration methods

	/**
	 * Register the capability like a web service through DOSGi
	 * 
	 * @param name
	 * @param resourceId
	 * @return
	 * @throws CapabilityException
	 */
	protected ServiceRegistration registerService(BundleContext bundleContext, String capabilityName, String resourceType, String resourceName,
			String ifaceName) throws CapabilityException {
		Dictionary<String, String> props = new Hashtable<String, String>();
		return registration = registerService(bundleContext, capabilityName, resourceType, resourceName, ifaceName, props);
	}

	/**
	 * Register the capability like a web service through DOSGi
	 * 
	 * @param name
	 * @param resourceId
	 * @return
	 * @throws CapabilityException
	 */
	protected ServiceRegistration registerService(BundleContext bundleContext, String capabilityName, String resourceType, String resourceName,
			String ifaceName, Dictionary<String, String> props) throws CapabilityException {
		try {
			ConfigurationAdminUtil configurationAdmin = new ConfigurationAdminUtil(bundleContext);
			String url = configurationAdmin.getProperty("org.opennaas", "ws.rest.url");
			if (props != null) {
				// Rest
				props.put("service.exported.interfaces", "*");
				props.put("service.exported.configs", "org.apache.cxf.rs");
				props.put("service.exported.intents", "HTTP");
				props.put("org.apache.cxf.rs.httpservice.context", url + "/" + resourceType + "/" + resourceName + "/" + capabilityName);
				props.put("org.apache.cxf.rs.address", "/");
				props.put("org.apache.cxf.httpservice.requirefilter", "true");
			}
			log.info("Registering ws: \n " +
					"in url: " + props.get("org.apache.cxf.rs.address") + "\n" +
					"in context: " + props.get("org.apache.cxf.rs.httpservice.context"));
			registration = bundleContext.registerService(ifaceName, this, props);
		} catch (IOException e) {
			throw new CapabilityException(e);
		}
		return registration;
	}

	protected void unregisterService() {
		if (registration != null)
			registration.unregister();
	}

	/**
	 * @return the resource name through the resource descriptor
	 */
	protected String getResourceName() {
		String resourceName = "";
		if (resource.getResourceDescriptor() != null
				&& resource.getResourceDescriptor().getInformation() != null) {
			resourceName = resource.getResourceDescriptor().getInformation().getName();
		}
		return resourceName;
	}

	/**
	 * @return the resource type through the resource descriptor
	 */
	protected String getResourceType() {
		String resourceType = "";
		if (resource.getResourceDescriptor() != null
				&& resource.getResourceDescriptor().getInformation() != null) {
			resourceType = resource.getResourceDescriptor().getInformation().getType();
		}
		return resourceType;
	}
}
