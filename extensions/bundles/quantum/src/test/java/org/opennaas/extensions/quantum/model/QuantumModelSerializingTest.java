package org.opennaas.extensions.quantum.model;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.junit.Test;
import org.opennaas.extensions.quantum.extensions.l3.model.Router;
import org.opennaas.extensions.quantum.extensions.l3.model.wrappers.IdTenantIdSubnetIdPortIdWrapper;
import org.opennaas.extensions.quantum.extensions.l3.model.wrappers.SubnetIdPortIdWrapper;
import org.opennaas.extensions.quantum.model.helper.QuantumModelHelper;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class QuantumModelSerializingTest {

	private static final Log	log												= LogFactory.getLog(QuantumModelSerializingTest.class);

	private final static String	networkJsonSample1								= "quantum-api/sample/json/network-post-req_new-3c0e668cae863734e746dbf72e3f2f3a272a9a0d.json";
	private final static String	networkJsonSample2								= "quantum-api/sample/json/network-post-resp_new-b07d8f156aa4354b80a7a8160522e5e179ca363b.json";
	private final static String	networkJsonSample3								= "quantum-api/sample/json/network-put-update.req_new-c5924ea7732c6bd48107fa2038fa4bcd84d435d9.json";
	private final static String	networkJsonSample4								= "quantum-api/sample/json/network-put-update-resp_new-fefa054df4c4a7735e42c555baa86d4f01969335.json";
	private final static String	networkJsonSample5								= "quantum-api/sample/json/networks-get-detail-resp_new-42e71c52ad4e79759c3c7a9f691b64cb472cfcb3.json";
	private final static String	networksJsonSample1								= "quantum-api/sample/json/networks-get-resp_new-8aa85d6952ee8a70e16252e7e7d69e5ed187627c.json";
	private final static String	portJsonSample1									= "quantum-api/sample/json/port-post-create-req_new-fbcbcc7c165acb0eca1f024f6a85065938df215f.json";
	private final static String	portsJsonSample1								= "quantum-api/sample/json/ports-get-resp_new-afb81c390ff648144013ef83eff6f489c00cc480.json";
	private final static String	subnetsJsonSample1								= "quantum-api/sample/json/subnets-get-resp_new-e5637401ce7357fa8c376065aaa0a6986db32b6a.json";

	private final static String	l3NetworkListRouterResponseSample				= "quantum-api/extensions/l3/json/example_5.10_List_Routers_JSON_Response.json";
	private final static String	l3NetworkShowRouterResponseSample				= "quantum-api/extensions/l3/json/example_5.12_Show_Router_JSON_Response.json";
	private final static String	l3NetworkCreateRouterRequestSample				= "quantum-api/extensions/l3/json/example_5.13_Create_Router_JSON_Request.json";
	private final static String	l3NetworkCreateRouterResponseSample				= "quantum-api/extensions/l3/json/example_5.14_Create_Router_JSON_Response.json";
	private final static String	l3NetworkUpdateRouterRequestSample				= "quantum-api/extensions/l3/json/example_5.15_Update_Router_JSON_Request.json";
	private final static String	l3NetworkUpdateRouterResponseSample				= "quantum-api/extensions/l3/json/example_5.16_Update_Router_JSON_Response.json";
	private final static String	l3NetworkAddRouterInterfaceRequestSample		= "quantum-api/extensions/l3/json/example_5.18_Add_Router_Interface_JSON_Request.json";
	private final static String	l3NetworkAddRouterInterfaceResponseSample		= "quantum-api/extensions/l3/json/example_5.19_Add_Router_Interface_JSON_Response.json";
	private final static String	l3NetworkRemoveRouterInterfaceRequestSample		= "quantum-api/extensions/l3/json/example_5.20_Remove_Router_Interface_JSON_Request.json";
	private final static String	l3NetworkRemoveRouterInterfaceResponseSample	= "quantum-api/extensions/l3/json/example_5.21_Remove_Router_Interface_JSON_Response.json";

	@Test
	public void serializeTest() throws JsonProcessingException {
		QuantumModel sampleQuantumModel = QuantumModelHelper.generateSampleQuantumModel();
		String serializedQuantumModel = serialize(sampleQuantumModel, true);
		log.debug("Serialized QuantumModel:\n" + serializedQuantumModel);
	}

	@Test
	public void deserializeTest() throws JSONException, JsonProcessingException, IOException {
		String originalSerializedNetwork1 = GetFileAsString(networkJsonSample1);
		Network deserializedNetwork1 = (Network) deserialize(Network.class, originalSerializedNetwork1, false);
		String serializedNetwork1 = serialize(deserializedNetwork1, false);
		log.debug("Original Serialized Network 1:\n" + originalSerializedNetwork1);
		log.debug("Serialized Network 1:\n" + serializedNetwork1);
		JSONAssert.assertEquals(originalSerializedNetwork1, serializedNetwork1, false);

		String originalSerializedNetwork2 = GetFileAsString(networkJsonSample2);
		Network deserializedNetwork2 = (Network) deserialize(Network.class, originalSerializedNetwork2, false);
		String serializedNetwork2 = serialize(deserializedNetwork2, false);
		log.debug("Original Serialized Network 2:\n" + originalSerializedNetwork2);
		log.debug("Serialized Network 2:\n" + serializedNetwork2);
		JSONAssert.assertEquals(originalSerializedNetwork2, serializedNetwork2, false);

		String originalSerializedNetwork3 = GetFileAsString(networkJsonSample3);
		Network deserializedNetwork3 = (Network) deserialize(Network.class, originalSerializedNetwork3, false);
		String serializedNetwork3 = serialize(deserializedNetwork3, false);
		log.debug("Original Serialized Network 3:\n" + originalSerializedNetwork3);
		log.debug("Serialized Network 3:\n" + serializedNetwork3);
		JSONAssert.assertEquals(originalSerializedNetwork3, serializedNetwork3, false);

		String originalSerializedNetwork4 = GetFileAsString(networkJsonSample4);
		Network deserializedNetwork4 = (Network) deserialize(Network.class, originalSerializedNetwork4, false);
		String serializedNetwork4 = serialize(deserializedNetwork4, false);
		log.debug("Original Serialized Network 4:\n" + originalSerializedNetwork4);
		log.debug("Serialized Network 4:\n" + serializedNetwork4);
		// FIXME lists of IDs, not elements with IDs
		// JSONAssert.assertEquals(originalSerializedNetwork4, serializedNetwork4, false);

		String originalSerializedNetwork5 = GetFileAsString(networkJsonSample5);
		Network deserializedNetwork5 = (Network) deserialize(Network.class, originalSerializedNetwork5, false);
		String serializedNetwork5 = serialize(deserializedNetwork5, false);
		log.debug("Original Serialized Network 5:\n" + originalSerializedNetwork5);
		log.debug("Serialized Network 5:\n" + serializedNetwork5);
		// FIXME lists of IDs, not elements with IDs
		// JSONAssert.assertEquals(originalSerializedNetwork5, serializedNetwork5, false);

		String originalSerializedNetworks1 = GetFileAsString(networksJsonSample1);
		QuantumModel deserializedNetworks1 = (QuantumModel) deserialize(QuantumModel.class, originalSerializedNetworks1, true);
		String serializedNetworks1 = serialize(deserializedNetworks1, true);
		log.debug("Original Serialized Networks 1:\n" + originalSerializedNetworks1);
		log.debug("Serialized Networks 1:\n" + serializedNetworks1);
		// FIXME lists of IDs, not elements with IDs
		// JSONAssert.assertEquals(originalSerializedNetworks1, serializedNetworks1, false);

		String originalSerializedPort1 = GetFileAsString(portJsonSample1);
		Port deserializedPort1 = (Port) deserialize(Port.class, originalSerializedPort1, false);
		String serializedPort1 = serialize(deserializedPort1, false);
		log.debug("Original Serialized Port 1:\n" + originalSerializedPort1);
		log.debug("Serialized Port 1:\n" + serializedPort1);
		JSONAssert.assertEquals(originalSerializedPort1, serializedPort1, false);

		String originalSerializedPorts1 = GetFileAsString(portsJsonSample1);
		QuantumModel deserializedPorts1 = (QuantumModel) deserialize(QuantumModel.class, originalSerializedPorts1, true);
		String serializedPorts1 = serialize(deserializedPorts1, true);
		log.debug("Original Serialized Ports 1:\n" + originalSerializedPorts1);
		log.debug("Serialized Ports 1:\n" + serializedPorts1);
		// FIXME ignore extension fields ("binding:XYZ")
		// JSONAssert.assertEquals(originalSerializedPorts1, serializedPorts1, false);

		String originalSerializedSubnets1 = GetFileAsString(subnetsJsonSample1);
		QuantumModel deserializedSubnets1 = (QuantumModel) deserialize(QuantumModel.class, originalSerializedSubnets1, true);
		String serializedSubnets1 = serialize(deserializedSubnets1, true);
		log.debug("Original Serialized Subnets 1:\n" + originalSerializedSubnets1);
		log.debug("Serialized Subnets 1:\n" + serializedSubnets1);
		JSONAssert.assertEquals(originalSerializedSubnets1, serializedSubnets1, false);
	}

	@Test
	public void l3extensionTest() throws JSONException, JsonProcessingException, IOException {
		String originalSerializedListRouterResponse = GetFileAsString(l3NetworkListRouterResponseSample);
		QuantumModel deserializedListRouterResponse = (QuantumModel) deserialize(QuantumModel.class, originalSerializedListRouterResponse, true);
		String serializedListRouterResponse = serialize(deserializedListRouterResponse, true);
		log.debug("Original List Router Response:\n" + originalSerializedListRouterResponse);
		log.debug("Serialized List Router Response:\n" + serializedListRouterResponse);
		// FIXME: ignore null fields
		// JSONAssert.assertEquals(originalSerializedListRouterResponse, serializedListRouterResponse, false);

		String originalSerializedShowRouterResponse = GetFileAsString(l3NetworkShowRouterResponseSample);
		QuantumModel deserializedShowRouterResponse = (QuantumModel) deserialize(QuantumModel.class, originalSerializedShowRouterResponse, true);
		String serializedShowRouterResponse = serialize(deserializedShowRouterResponse, true);
		log.debug("Original Show Router Response:\n" + originalSerializedShowRouterResponse);
		log.debug("Serialized Show Router Response:\n" + serializedShowRouterResponse);
		JSONAssert.assertEquals(originalSerializedShowRouterResponse, serializedShowRouterResponse, false);

		String originalSerializedCreateRouterRequest = GetFileAsString(l3NetworkCreateRouterRequestSample);
		Router deserializedCreateRouterRequest = (Router) deserialize(Router.class, originalSerializedCreateRouterRequest, false);
		String serializedCreateRouterRequest = serialize(deserializedCreateRouterRequest, false);
		log.debug("Original Create Router Request:\n" + originalSerializedCreateRouterRequest);
		log.debug("Serialized Create Router Request:\n" + serializedCreateRouterRequest);
		JSONAssert.assertEquals(originalSerializedCreateRouterRequest, serializedCreateRouterRequest, false);

		String originalSerializedCreateRouterResponse = GetFileAsString(l3NetworkCreateRouterResponseSample);
		Router deserializedCreateRouterResponse = (Router) deserialize(Router.class, originalSerializedCreateRouterResponse, false);
		String serializedCreateRouterResponse = serialize(deserializedCreateRouterResponse, false);
		log.debug("Original Create Router Response:\n" + originalSerializedCreateRouterResponse);
		log.debug("Serialized Create Router Response:\n" + serializedCreateRouterResponse);
		// FIXME: ignore null fields
		// JSONAssert.assertEquals(originalSerializedCreateRouterResponse, serializedCreateRouterResponse, false);

		String originalSerializedUpdateRouterRequest = GetFileAsString(l3NetworkUpdateRouterRequestSample);
		Router deserializedUpdateRouterRequest = (Router) deserialize(Router.class, originalSerializedUpdateRouterRequest, false);
		String serializedUpdateRouterRequest = serialize(deserializedUpdateRouterRequest, false);
		log.debug("Original Update Router Request:\n" + originalSerializedUpdateRouterRequest);
		log.debug("Serialized Update Router Request:\n" + serializedUpdateRouterRequest);
		JSONAssert.assertEquals(originalSerializedUpdateRouterRequest, serializedUpdateRouterRequest, false);

		String originalSerializedUpdateRouterResponse = GetFileAsString(l3NetworkUpdateRouterResponseSample);
		Router deserializedUpdateRouterResponse = (Router) deserialize(Router.class, originalSerializedUpdateRouterResponse, false);
		String serializedUpdateRouterResponse = serialize(deserializedUpdateRouterResponse, false);
		log.debug("Original Update Router Response:\n" + originalSerializedUpdateRouterResponse);
		log.debug("Serialized Update Router Response:\n" + serializedUpdateRouterResponse);
		JSONAssert.assertEquals(originalSerializedUpdateRouterResponse, serializedUpdateRouterResponse, false);

		String originalSerializedAddRouterInterfaceRequest = GetFileAsString(l3NetworkAddRouterInterfaceRequestSample);
		SubnetIdPortIdWrapper deserializedAddRouterInterfaceRequest = (SubnetIdPortIdWrapper) deserialize(SubnetIdPortIdWrapper.class,
				originalSerializedAddRouterInterfaceRequest, true);
		String serializedAddRouterInterfaceRequest = serialize(deserializedAddRouterInterfaceRequest, true);
		log.debug("Original Add Router Interface Request:\n" + originalSerializedAddRouterInterfaceRequest);
		log.debug("Serialized Add Router Interface Request:\n" + serializedAddRouterInterfaceRequest);
		JSONAssert.assertEquals(originalSerializedAddRouterInterfaceRequest, serializedAddRouterInterfaceRequest, false);

		String originalSerializedAddRouterInterfaceResponse = GetFileAsString(l3NetworkAddRouterInterfaceResponseSample);
		SubnetIdPortIdWrapper deserializedAddRouterInterfaceResponse = (SubnetIdPortIdWrapper) deserialize(SubnetIdPortIdWrapper.class,
				originalSerializedAddRouterInterfaceResponse, true);
		String serializedAddRouterInterfaceResponse = serialize(deserializedAddRouterInterfaceResponse, true);
		log.debug("Original Add Router Interface Response:\n" + originalSerializedAddRouterInterfaceResponse);
		log.debug("Serialized Add Router Interface Response:\n" + serializedAddRouterInterfaceResponse);
		JSONAssert.assertEquals(originalSerializedAddRouterInterfaceResponse, serializedAddRouterInterfaceResponse, false);

		String originalSerializedRemoveRouterInterfaceRequest = GetFileAsString(l3NetworkRemoveRouterInterfaceRequestSample);
		SubnetIdPortIdWrapper deserializedRemoveRouterInterfaceRequest = (SubnetIdPortIdWrapper) deserialize(SubnetIdPortIdWrapper.class,
				originalSerializedRemoveRouterInterfaceRequest, true);
		String serializedRemoveRouterInterfaceRequest = serialize(deserializedRemoveRouterInterfaceRequest, true);
		log.debug("Original Remove Router Interface Request:\n" + originalSerializedRemoveRouterInterfaceRequest);
		log.debug("Serialized Remove Router Interface Request:\n" + serializedRemoveRouterInterfaceRequest);
		JSONAssert.assertEquals(originalSerializedRemoveRouterInterfaceRequest, serializedRemoveRouterInterfaceRequest, false);

		String originalSerializedRemoveRouterInterfaceResponse = GetFileAsString(l3NetworkRemoveRouterInterfaceResponseSample);
		IdTenantIdSubnetIdPortIdWrapper deserializedRemoveRouterInterfaceResponse = (IdTenantIdSubnetIdPortIdWrapper) deserialize(
				IdTenantIdSubnetIdPortIdWrapper.class, originalSerializedRemoveRouterInterfaceResponse, true);
		String serializedRemoveRouterInterfaceResponse = serialize(deserializedRemoveRouterInterfaceResponse, true);
		log.debug("Original Remove Router Interface Response:\n" + originalSerializedRemoveRouterInterfaceResponse);
		log.debug("Serialized Remove Router Interface Response:\n" + serializedRemoveRouterInterfaceResponse);
		JSONAssert.assertEquals(originalSerializedRemoveRouterInterfaceResponse, serializedRemoveRouterInterfaceResponse, false);
	}

	private static String GetFileAsString(String filePath) throws IOException {
		log.debug("Reading file: " + filePath);
		return Resources.toString(Resources.getResource(filePath), Charsets.UTF_8);
	}

	private static String serialize(Object quantumModel, boolean rootObject) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()));
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, !rootObject);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		return mapper.writeValueAsString(quantumModel);
	}

	private static Object deserialize(Class<?> quantumObjectClass, String serializedObject, boolean rootObject) throws JsonParseException,
			JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()));
		mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, !rootObject);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.readValue(serializedObject, quantumObjectClass);
	}

}
