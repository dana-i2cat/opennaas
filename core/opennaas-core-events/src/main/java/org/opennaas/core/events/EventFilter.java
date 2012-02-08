package org.opennaas.core.events;

import java.util.Enumeration;
import java.util.Properties;

/**
 *
 * This class is used for EventHandlers in order to specify events they are interested in.
 *
 * There are two handler properties that specify the type of events you are interested in: Event Topic and Event Properties filter.
 *
 * The value of the Topic Name pattern property must be of type String[]. This value contains the names of the topics that this handler is interested
 * in. A wildcard '*' can be used as the last token of a Topic Name, e.g., the value 'org/osgi/framework/*' matches topic name
 * 'org/osgi/framework/BundleEvent/REGISTERED', but the value 'org/osgi/framework/Bundle*' is considered invalid.
 *
 * The value of the Event Properties filter is of type String and contains an LDAP filter that will be matched against event properties, e.g., The
 * Event Properties Filter '(&(productCategory=books)(price>=80))' filters all events that has the 'books' category and price is more than or equal
 * 80.
 *
 * Event Topic property is mandatory! If the handler service doesn't provide this property it will receive no events. If you want to subscribe to all
 * of the events in the environment then you should specify the value '*' for it.
 *
 * @author isart
 *
 */
public class EventFilter {

	protected String[]	topics				= new String[] { "*" }; // match all topics by default
	protected String	propertiesFilter	= "";

	public EventFilter(String[] topics, String propertiesFilter) {
		this.topics = topics;
		this.propertiesFilter = propertiesFilter;
	}

	public EventFilter(String[] topics, Properties properties) {
		this(topics, buildPropertiesFilter(properties));
	}

	public EventFilter(String topic, String propertiesFilter) {
		this(new String[] { topic }, propertiesFilter);
	}

	public EventFilter(String[] topics) {
		this.topics = topics;
	}

	public EventFilter(String topic) {
		this(new String[] { topic });
	}

	public String getPropertiesFilter() {
		return propertiesFilter;
	}

	public String[] getTopics() {
		return topics;
	}

	public static String buildPropertiesFilter(Properties properties) {
		String filterString = "";
		Enumeration<Object> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String currentKey = (String) keys.nextElement();
			filterString += "(" + currentKey + "=" + properties.getProperty(currentKey) + ")";
		}
		filterString = "(&" + filterString + ")";
		return filterString;
	}

}
