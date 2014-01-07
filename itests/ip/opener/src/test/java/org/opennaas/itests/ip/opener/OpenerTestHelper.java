package org.opennaas.itests.ip.opener;

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
