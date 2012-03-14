package net.i2cat.nexus.events.tests;

import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import javax.inject.Inject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;

import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static net.i2cat.nexus.tests.OpennaasExamOptions.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(JUnit4TestRunner.class)
public class SendReceiveEventsTest
{
	private final static Log	log 	= LogFactory.getLog(SendReceiveEventsTest.class);

	private final BlockingQueue<Event>	h1Received = new ArrayBlockingQueue<Event>(1);
	private final BlockingQueue<Event>	h2Received = new ArrayBlockingQueue<Event>(1);

	private final String		eventTopic			= "whatever/the/topic/is";
	private final String		filterPropertyName	= "aPropertyName";
	private final String		filterPropertyValue	= "aPropertyValue";

	@Inject
	private BundleContext	bundleContext;

	@Inject
	private EventAdmin		eventAdmin;

	@Inject
	private IEventManager	eventManager;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
					   includeFeatures("opennaas-core"),
					   noConsole(),
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

		Event h1Event = h1Received.poll(10, SECONDS);
		Event h2Event = h2Received.poll(10, SECONDS);

		log.info("Checking reception...");
		assertNotNull(h1Event);
		assertNotNull(h2Event);
		assertNotNull(h2Event.getProperty(filterPropertyName));
		assertTrue(h2Event.getProperty(filterPropertyName)
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

				h1Received.add(event);
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

				h2Received.add(event);
			}
		};
		return handler;
	}
}
