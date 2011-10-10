package org.opennaas.core.events.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;

import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Customizer;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.swissbox.tinybundles.core.TinyBundles;
import org.ops4j.pax.swissbox.tinybundles.dp.Constants;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(JUnit4TestRunner.class)
public class SendReceiveEventsTest extends AbstractIntegrationTest {

	Logger					log					= LoggerFactory.getLogger(SendReceiveEventsTest.class);

	@Inject
	private BundleContext	bundleContext;

	Event					h1receivedEvent		= null;
	Event					h2receivedEvent		= null;

	String					eventTopic			= "whatever/the/topic/is";
	String					filterPropertyName	= "aPropertyName";
	String					filterPropertyValue	= "aPropertyValue";

	public static Option[] configure() {
		return combine(
				IntegrationTestsHelper.getNexusTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId("net.i2cat.nexus.tests.helper")
		// ,
		// // Tell pax platform to include ORGi/Minimum-1.1 exec environment (required for org.eclipse.equinox.event)
		// vmOption("-Dorg.osgi.framework.executionenvironment=J2SE-1.2,J2SE-1.3,J2SE-1.4,J2SE-1.5,OSGi/Minimum-1.1,"
		// + "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5006")
		// )
		);
	}

	@Configuration
	public Option[] additionalConfiguration() throws Exception {
		return combine(configure(), new Customizer() {
			@Override
			public InputStream customizeTestProbe(InputStream testProbe) throws Exception {
				return TinyBundles.modifyBundle(testProbe).set(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional").build();
			}
		});
	}
	
	@Test
	public void registerHandlerAndPublishEventTest() {

		loadBundles();

		EventFilter filter1 = new EventFilter(
				new String[] { eventTopic });

		EventFilter filter2 = new EventFilter(
				new String[] { eventTopic }, "(" + filterPropertyName + "=" + filterPropertyValue + ")");

		EventHandler handler1 = createHandler1();
		EventHandler handler2 = createHandler2();

		IEventManager eventManager = getOsgiService(IEventManager.class, 20000);
		assertNotNull(eventManager);

		log.info("Registering Handlers...");
		int handler1Id = eventManager.registerEventHandler(handler1, filter1);
		int handler2Id = eventManager.registerEventHandler(handler2, filter2);
		log.info("Registering Handlers... DONE");

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(filterPropertyName, filterPropertyValue);

		Event event = new Event(eventTopic, properties);

		log.info("Publishing event...");
		eventManager.publishEvent(event);
		log.info("Publishing event... DONE");

		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		log.info("Checking reception...");
		assertNotNull(h1receivedEvent);
		assertNotNull(h2receivedEvent);
		assertNotNull(h2receivedEvent.getProperty(filterPropertyName));
		assertTrue(h2receivedEvent.getProperty(filterPropertyName)
				.equals(filterPropertyValue));
		log.info("Checking reception... DONE");

		log.info("Unregistering Handlers...");
		eventManager.unregisterHandler(handler1Id);
		eventManager.unregisterHandler(handler2Id);
		log.info("Unregistering Handlers... DONE");

	}

	private EventHandler createHandler1() {

		EventHandler handler = new EventHandler() {

			@Override
			public void handleEvent(Event event) {
				log.info("------------------------------------");
				log.info("------------------------------------");
				log.info("------------------------------------");
				log.info("Handler1 Received event");
				log.info("Topic: " + event.getTopic());
				log.info("Properties: ");
				for (String propName : event.getPropertyNames())
					log.info(propName + " : " + event.getProperty(propName));
				log.info("------------------------------------");
				log.info("------------------------------------");
				log.info("------------------------------------");

				h1receivedEvent = event;
			}
		};
		return handler;
	}

	private EventHandler createHandler2() {

		EventHandler handler = new EventHandler() {

			@Override
			public void handleEvent(Event event) {

				log.info("------------------------------------");
				log.info("------------------------------------");
				log.info("------------------------------------");
				log.info("Handler2 Received event");
				log.info("Topic: " + event.getTopic());
				log.info("Properties: ");
				for (String propName : event.getPropertyNames())
					log.info(propName + " : " + event.getProperty(propName));
				log.info("------------------------------------");
				log.info("------------------------------------");
				log.info("------------------------------------");

				h2receivedEvent = event;
			}
		};
		return handler;
	}

	public void loadBundles() {

		assertNotNull(bundleContext);

		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		EventAdmin eventAdmin = getOsgiService(EventAdmin.class, 20000);
		assertNotNull(eventAdmin);

		IEventManager eventManager = getOsgiService(IEventManager.class, 20000);
		assertNotNull(eventManager);

	}
}
