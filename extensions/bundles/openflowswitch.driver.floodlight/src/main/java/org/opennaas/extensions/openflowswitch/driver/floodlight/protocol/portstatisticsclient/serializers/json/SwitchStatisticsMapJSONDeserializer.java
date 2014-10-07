package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.portstatisticsclient.serializers.json;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Floodlight driver v0.90
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.PortStatistics;
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

			JsonToken switchStatisticsToken = jp.nextToken();
			if (switchStatisticsToken == JsonToken.VALUE_NULL) {
				// add empty switch statistics
				wrapper.addSwitchStatistics(switchId, new HashMap<Integer, PortStatistics>());
			} else {
				// expect start array
				if (switchStatisticsToken != JsonToken.START_ARRAY) {
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
