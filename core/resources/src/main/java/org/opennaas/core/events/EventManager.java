package org.opennaas.core.events;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.Activator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * This class is intended to manage events, using EventAdmin service as a helper. In order for a class to receive events, it must implement
 * EventHandler interface and register itself using a filter. <code>registerEventHandler</code> manages this registration.
 * 
 * 
 * @author isart
 * 
 */
public class EventManager implements IEventManager {

	// protected BundleContext bundleContext;

	Log												log					= LogFactory.getLog(EventManager.class);

	protected HashMap<Integer, ServiceRegistration>	registeredServices	= new HashMap<Integer, ServiceRegistration>();
	protected int									serviceID			= 0;

	public BundleContext getBundleContext() {
		return Activator.getBundleContext();
	}

	// public void setBundleContext(BundleContext bundleContext) {
	// this.bundleContext = bundleContext;
	// }

	/**
	 * Registers given Handler using given filter. Handler <code>handleEvent</code> operation will be called when events matching given filter are
	 * received.
	 * 
	 * @param filter
	 */
	public int registerEventHandler(EventHandler handler, EventFilter filter) {

		Dictionary<String, Object> handlerServiceProperties = getHandlerServiceProperties(
				filter.getTopics(), filter.getPropertiesFilter());

		int handlerServiceID = registerService(EventHandler.class.getName(),
				handler, handlerServiceProperties);

		return handlerServiceID;
	}

	public void unregisterHandler(int handlerServiceID) {
		unregisterService(handlerServiceID);
	}

	public void publishEvent(Event event) {
		log.debug("Publishing event");
		getEventAdmin().postEvent(event);
	}

	protected int registerService(String serviceName, Object service,
			Dictionary<String, Object> properties) {
		ServiceRegistration registration = getBundleContext().registerService(
				serviceName, service, properties);
		int thisServiceID = serviceID;
		registeredServices.put(thisServiceID, registration);
		serviceID++;
		return thisServiceID;
	}

	protected void unregisterService(int serviceID) {
		ServiceRegistration registration = registeredServices.get(serviceID);
		registration.unregister();
		registeredServices.remove(serviceID);
		if (registeredServices.size() == 0)
			serviceID = 0; // reset to allow re-use of IDs
	}

	protected EventAdmin getEventAdmin() {
		ServiceReference reference = getBundleContext()
				.getServiceReference(EventAdmin.class.getName());

		if (reference == null) {
			log.error("EventAdmin reference is null!!!! No services are registered which implement " + EventAdmin.class.getName());
			return null;
		}

		return (EventAdmin) getBundleContext().getService(reference);
	}

	/**
	 * 
	 * @param topics
	 *            Specify the topics to subscribe to - Wilcard * is applicable at the end of a String
	 * @param filter
	 * @return dictionary object that represents handler service properties.
	 */
	protected Dictionary<String, Object> getHandlerServiceProperties(
			String[] topics, String filter) {

		String[] topicsToUse = topics;
		if (topicsToUse == null || topicsToUse.length == 0)
			topicsToUse = new String[] { "*" };

		Dictionary<String, Object> result = new Hashtable<String, Object>();
		result.put(EventConstants.EVENT_TOPIC, topicsToUse);

		if (filter != null && filter.length() > 0)
			result.put(EventConstants.EVENT_FILTER, filter);

		return result;
	}

}
