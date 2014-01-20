package org.opennaas.itests.ip.opener;

/*
 * #%L
 * OpenNaaS :: iTests :: IP :: Opener
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

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.opener.client.model.IPData;
import org.opennaas.extensions.router.opener.client.model.Interface;
import org.opennaas.extensions.router.opener.client.rpc.GetInterfaceResponse;
import org.opennaas.extensions.router.opener.client.rpc.GetInterfacesResponse;
import org.opennaas.extensions.router.opener.client.rpc.SetInterfaceIPRequest;
import org.opennaas.extensions.router.opener.client.rpc.SetInterfaceResponse;
import org.opennaas.extensions.router.opener.client.rpc.Utils;

public abstract class OpenerTestHelper {

	public static String sampleGetInterfacesResponse(List<String> ifaces) throws JAXBException {
		GetInterfacesResponse response = new GetInterfacesResponse();

		response.setInterfaces(ifaces);

		StringWriter writer = new StringWriter();
		JAXBContext jaxbCon = JAXBContext.newInstance(GetInterfacesResponse.class);
		Marshaller marshaller = jaxbCon.createMarshaller();

		marshaller.marshal(response, writer);

		return writer.toString();
	}

	public static String sampleGetInterfaceResponse(String ifaceName, String ipv4) throws JAXBException {

		GetInterfaceResponse response = new GetInterfaceResponse();

		Interface iface = new Interface();
		iface.setName(ifaceName);

		if (ipv4 != null) {
			IPData ipData = new IPData();
			ipData.setAddress(ipv4.split("/")[0]);
			ipData.setFamilyType(ProtocolIFType.IPV4.name());
			ipData.setPrefixLength(ipv4.split("/")[1]);
			iface.setIp(ipData);
		}

		response.setInterface(iface);

		StringWriter writer = new StringWriter();
		JAXBContext jaxbCon = JAXBContext.newInstance(GetInterfaceResponse.class);
		Marshaller marshaller = jaxbCon.createMarshaller();

		marshaller.marshal(response, writer);

		return writer.toString();
	}

	public static String sampleSetInterfaceRequest(String iface, String ipv4) throws JAXBException {

		String ipAdderss = ipv4.split("/")[0];
		String prefixLength = ipv4.split("/")[1];
		SetInterfaceIPRequest req = Utils.createSetInterfaceIPRequest(iface, ipAdderss, prefixLength);

		StringWriter writer = new StringWriter();
		JAXBContext jaxbCon = JAXBContext.newInstance(SetInterfaceIPRequest.class);
		Marshaller marshaller = jaxbCon.createMarshaller();

		marshaller.marshal(req, writer);

		return writer.toString();
	}

	public static String sampleSetInterfaceResponse(String responseMessage) throws JAXBException {

		SetInterfaceResponse response = new SetInterfaceResponse();
		response.setError(null);
		response.setResponse(responseMessage);

		StringWriter writer = new StringWriter();
		JAXBContext jaxbCon = JAXBContext.newInstance(SetInterfaceResponse.class);
		Marshaller marshaller = jaxbCon.createMarshaller();

		marshaller.marshal(response, writer);

		return writer.toString();
	}
}
