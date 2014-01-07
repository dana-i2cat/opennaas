package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.portstatisticsclient.serializers.json;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.openflowswitch.capability.monitoring.PortStatistics;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.countersclient.wrappers.CountersMap;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.portstatisticsclient.wrappers.SwitchStatisticsMap;

/**
 * {@link CountersMap} custom JSON deserializer
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class SwitchStatisticsMapJSONDeserializer extends JsonDeserializer<SwitchStatisticsMap> {

	private Log	log	= LogFactory.getLog(SwitchStatisticsMapJSONDeserializer.class);

	@Override
	public SwitchStatisticsMap deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JsonProcessingException {
		// initialize object wrapper
		SwitchStatisticsMap wrapper = new SwitchStatisticsMap();

		// switch IDs loop
		while ((jp.nextToken()) != JsonToken.END_OBJECT) {
			if ((jp.getCurrentToken()) != JsonToken.FIELD_NAME) {
				throw new IOException("Expected FIELD_NAME and it was " + jp.getCurrentToken());
			}

			// get switch ID
			String switchId = jp.getCurrentName();

			// expect start array
			if (jp.nextToken() != JsonToken.START_ARRAY) {
				throw new IOException("Expected START_ARRAY and it was " + jp.getCurrentToken());
			}

			// action array loop
			while (jp.nextToken() != JsonToken.END_ARRAY) {
				// expect action start object
				if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
					throw new IOException("Expected START_OBJECT and it was " + jp.getCurrentToken());
				}

				PortStatistics ps = parseStatistics(switchId, jp);
				wrapper.addPortStatisticsForSwitch(switchId, ps.getPort(), ps);
			}
		}

		return wrapper;
	}

	private PortStatistics parseStatistics(String switchId, JsonParser jp) throws JsonParseException, IOException {
		// initialize port statistics
		PortStatistics portStatistics = new PortStatistics();

		// port statistics fields loop
		while ((jp.nextToken()) != JsonToken.END_OBJECT) {
			if ((jp.getCurrentToken()) != JsonToken.FIELD_NAME) {
				throw new IOException("Expected FIELD_NAME and it was " + jp.getCurrentToken());
			}

			// get port statistic name and go to next token
			String statisticName = jp.getCurrentName();
			jp.nextToken();

			if (statisticName == "portNumber")
				portStatistics.setPort(jp.getIntValue());
			else if (statisticName == "collisions")
				portStatistics.setCollisions(jp.getLongValue());
			else if (statisticName == "receiveBytes")
				portStatistics.setReceiveBytes(jp.getLongValue());
			else if (statisticName == "receiveCRCErrors")
				portStatistics.setReceiveCRCErrors(jp.getLongValue());
			else if (statisticName == "receiveDropped")
				portStatistics.setReceiveDropped(jp.getLongValue());
			else if (statisticName == "receiveErrors")
				portStatistics.setReceiveErrors(jp.getLongValue());
			else if (statisticName == "receiveFrameErrors")
				portStatistics.setReceiveFrameErrors(jp.getLongValue());
			else if (statisticName == "receiveOverrunErrors")
				portStatistics.setReceiveOverrunErrors(jp.getLongValue());
			else if (statisticName == "receivePackets")
				portStatistics.setReceivePackets(jp.getLongValue());
			else if (statisticName == "transmitBytes")
				portStatistics.setTransmitBytes(jp.getLongValue());
			else if (statisticName == "transmitDropped")
				portStatistics.setTransmitDropped(jp.getLongValue());
			else if (statisticName == "transmitErrors")
				portStatistics.setTransmitErrors(jp.getLongValue());
			else if (statisticName == "transmitPackets")
				portStatistics.setTransmitPackets(jp.getLongValue());
			else
				log.warn("Unknown statistic with name = " + statisticName + " and value = " + jp.getText());
		}

		return portStatistics;

	}
}
