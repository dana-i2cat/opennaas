package net.i2cat.nexus.events.tests;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.ops4j.pax.exam.Customizer;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.junit.ProbeBuilder;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

@RunWith(JUnit4TestRunner.class)
public class SendReceiveEventsTest
{
	private final static Log	log 	= LogFactory.getLog(SendReceiveEventsTest.class);

	private Event				h1receivedEvent;
	private Event				h2receivedEvent;

	private final String		eventTopic			= "whatever/the/topic/is";
	private final String		filterPropertyName	= "aPropertyName";
	private final String		filterPropertyValue	= "aPropertyValue";

	@Inject
	private BundleContext	bundleContext;

	@Inject
	private EventAdmin		eventAdmin;

	@Inject
	private IEventManager	eventManager;

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional");
        return probe;
    }

	@Configuration
	public static Option[] configuration() {
		return options(karafDistributionConfiguration()
					   .frameworkUrl(maven()
									 .groupId("net.i2cat.mantychore")
									 .artifactId("assembly")
									 .type("zip")
									 .classifier("bin")
									 .versionAsInProject())
					   .karafVersion("2.2.2")
					   .name("mantychore")
					   .unpackDirectory(new File("target/paxexam")),
					   keepRuntimeFolder());
	}

	@Test
	public void registerHandlerAndPublishEventTest() throws InterruptedException
	{
		EventFilter filter1 = new EventFilter(
				new String[] { eventTopic });

		EventFilter filter2 = new EventFilter(
				new String[] { eventTopic }, "(" + filterPropertyName + "=" + filterPropertyValue + ")");

		EventHandler handler1 = createHandler1();
		EventHandler handler2 = createHandler2();

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

		Thread.sleep(10 * 1000);

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
}
