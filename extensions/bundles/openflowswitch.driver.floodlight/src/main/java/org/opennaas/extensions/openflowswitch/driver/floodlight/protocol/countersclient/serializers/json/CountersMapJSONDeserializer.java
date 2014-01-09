package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.countersclient.serializers.json;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.countersclient.wrappers.CountersMap;

/**
 * {@link CountersMap} custom JSON deserializer
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class CountersMapJSONDeserializer extends JsonDeserializer<CountersMap> {
	@Override
	public CountersMap deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JsonProcessingException {
		// initialize object wrapper
		CountersMap wrapper = new CountersMap();

		// counters loop
		while ((jp.nextToken()) != JsonToken.END_OBJECT) {
			if ((jp.getCurrentToken()) != JsonToken.FIELD_NAME) {
				throw new IOException("Expected FIELD_NAME and it was " + jp.getCurrentToken());
			}

			// get counter ID
			String counterId = jp.getCurrentName();

			// get counter value
			if (jp.nextToken() != JsonToken.VALUE_NUMBER_INT) {
				throw new IOException("Expected VALUE_NUMBER_INT and it was " + jp.getCurrentToken());
			}
			long counterValue = jp.getLongValue();
			wrapper.addCounter(counterId, counterValue);
		}

		return wrapper;
	}
}
