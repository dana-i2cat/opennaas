package org.opennaas.extensions.genericnetwork.test.capability.nclmonitoring;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimedPortStatistics;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimedStatistics;
import org.xml.sax.SAXException;

public class PortStatisticsSerializationTest {
	
	@Test
	public void timedPortStatisticsSerializationDeserializationTest() throws SerializationException, IOException, SAXException,
			TransformerException,
			ParserConfigurationException {

		TimedPortStatistics toSerialize = generateSampleTimedPortStatistics();

		String serialized = ObjectSerializer.toXml(toSerialize);
		System.out.println(serialized);
		
		TimedPortStatistics deserialized = (TimedPortStatistics) ObjectSerializer.fromXml(serialized, TimedPortStatistics.class);

		Assert.assertEquals(toSerialize, deserialized);
	}

	private TimedPortStatistics generateSampleTimedPortStatistics() {
		
		TimedPortStatistics result = new TimedPortStatistics();
		result.setStatistics(new ArrayList<TimedStatistics>());
		
		result.getStatistics().add(new TimedStatistics(Long.valueOf(1L), "switch1", "p1", "10", "0"));
		result.getStatistics().add(new TimedStatistics(Long.valueOf(1L), "switch1", "p2", "10", "0"));
		result.getStatistics().add(new TimedStatistics(Long.valueOf(2L), "switch1", "p1", "10", "0"));
		result.getStatistics().add(new TimedStatistics(Long.valueOf(2L), "switch1", "p2", "100", "5"));
		result.getStatistics().add(new TimedStatistics(Long.valueOf(3L), "switch1", "p1", "10", "0"));
		result.getStatistics().add(new TimedStatistics(Long.valueOf(3L), "switch1", "p2", "10", "0"));
				
		return result;
	}
}
