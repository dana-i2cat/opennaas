package org.opennaas.extensions.router.model.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.VLANEndpoint;

public class ModelToXmlTest {

	private JAXBContext	jc;

	@Before
	public void initJaxbContext() throws JAXBException {
		jc = JAXBContext.newInstance(
				org.opennaas.extensions.router.model.AdminDomain.class,
				org.opennaas.extensions.router.model.AreaOfConfiguration.class,
				org.opennaas.extensions.router.model.Association.class,
				org.opennaas.extensions.router.model.BindsTo.class,
				org.opennaas.extensions.router.model.Component.class,
				org.opennaas.extensions.router.model.ComputerSystem.class,
				org.opennaas.extensions.router.model.Dependency.class,
				org.opennaas.extensions.router.model.DeviceConnection.class,
				org.opennaas.extensions.router.model.DeviceSAPImplementation.class,
				org.opennaas.extensions.router.model.EnabledLogicalElement.class,
				org.opennaas.extensions.router.model.EndpointInArea.class,
				org.opennaas.extensions.router.model.EthernetPort.class,
				org.opennaas.extensions.router.model.FCPort.class,
				org.opennaas.extensions.router.model.FilterEntryBase.class,
				org.opennaas.extensions.router.model.GREService.class,
				org.opennaas.extensions.router.model.GRETunnelConfiguration.class,
				org.opennaas.extensions.router.model.GRETunnelEndpoint.class,
				org.opennaas.extensions.router.model.GRETunnelServiceConfiguration.class,
				org.opennaas.extensions.router.model.GRETunnelService.class,
				org.opennaas.extensions.router.model.HostedDependency.class,
				org.opennaas.extensions.router.model.HostedRoute.class,
				org.opennaas.extensions.router.model.HostedRoutingServices.class,
				org.opennaas.extensions.router.model.HostedService.class,
				org.opennaas.extensions.router.model.IPHeadersFilter.class,
				org.opennaas.extensions.router.model.IPProtocolEndpoint.class,
				org.opennaas.extensions.router.model.LogicalDevice.class,
				org.opennaas.extensions.router.model.LogicalElement.class,
				org.opennaas.extensions.router.model.LogicalModule.class,
				org.opennaas.extensions.router.model.LogicalPort.class,
				org.opennaas.extensions.router.model.LogicalTunnelPort.class,
				org.opennaas.extensions.router.model.ManagedElement.class,
				org.opennaas.extensions.router.model.ManagedSystemElement.class,
				org.opennaas.extensions.router.model.ModulePort.class,
				org.opennaas.extensions.router.model.NetworkPort.class,
				org.opennaas.extensions.router.model.NetworkService.class,
				org.opennaas.extensions.router.model.NextHopIPRoute.class,
				org.opennaas.extensions.router.model.NextHopRoute.class,
				org.opennaas.extensions.router.model.OSPFAreaConfiguration.class,
				org.opennaas.extensions.router.model.OSPFArea.class,
				org.opennaas.extensions.router.model.OSPFProtocolEndpointBase.class,
				org.opennaas.extensions.router.model.OSPFProtocolEndpoint.class,
				org.opennaas.extensions.router.model.OSPFServiceConfiguration.class,
				org.opennaas.extensions.router.model.OSPFService.class,
				org.opennaas.extensions.router.model.PortImplementsEndpoint.class,
				org.opennaas.extensions.router.model.PortOnDevice.class,
				org.opennaas.extensions.router.model.ProtocolEndpoint.class,
				org.opennaas.extensions.router.model.ProvidesEndpoint.class,
				org.opennaas.extensions.router.model.RouteCalculationService.class,
				org.opennaas.extensions.router.model.RouteUsesEndpoint.class,
				org.opennaas.extensions.router.model.RoutingProtocolDomain.class,
				org.opennaas.extensions.router.model.SAPSAPDependency.class,
				org.opennaas.extensions.router.model.ServiceAccessBySAP.class,
				org.opennaas.extensions.router.model.ServiceAccessPoint.class,
				org.opennaas.extensions.router.model.Service.class,
				org.opennaas.extensions.router.model.SystemComponent.class,
				org.opennaas.extensions.router.model.SystemDevice.class,
				org.opennaas.extensions.router.model.System.class,
				org.opennaas.extensions.router.model.VLANEndpoint.class);
	}

	@Test
	public void testSampleModel2Xml2Java() throws Exception {

		ComputerSystem original = createSampleModel();

		// transform to XML
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter writer = new StringWriter();
		marshaller.marshal(original, writer);

		// Load from XML
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		StringReader reader = new StringReader(writer.toString());
		ComputerSystem loaded =
				(ComputerSystem) unmarshaller.unmarshal(reader);

		check(original, loaded);
	}

	private void check(ComputerSystem original, ComputerSystem loaded) {
		assertEquals(original.getName(), loaded.getName());
		assertEquals(original.getElementName(), loaded.getElementName());

		assertEquals(original.getLogicalDevices().size(), loaded.getLogicalDevices().size());

		for (int i = 0; i < original.getLogicalDevices().size(); i++) {
			// check subtypes are maintained
			assertEquals(original.getLogicalDevices().get(i).getClass(), loaded.getLogicalDevices().get(i).getClass());
			assertEquals(original.getLogicalDevices().get(i).getName(), loaded.getLogicalDevices().get(i).getName());
			if (original.getLogicalDevices().get(i) instanceof NetworkPort) {
				assertEquals(((NetworkPort) original.getLogicalDevices().get(i)).getPortNumber(),
						((NetworkPort) loaded.getLogicalDevices().get(i)).getPortNumber());
			}

			assertEquals(((LogicalPort) original.getLogicalDevices().get(i)).getProtocolEndpoint().size(), ((LogicalPort) loaded.getLogicalDevices()
					.get(i)).getProtocolEndpoint().size());
			for (int j = 0; j < ((LogicalPort) original.getLogicalDevices().get(i)).getProtocolEndpoint().size(); j++) {
				// check subtypes are maintained
				assertEquals(((LogicalPort) original.getLogicalDevices().get(i)).getProtocolEndpoint().get(j).getClass(),
						((LogicalPort) loaded.getLogicalDevices().get(i)).getProtocolEndpoint().get(j).getClass());
				// check reverse association is loaded
				assertTrue("Reverse association should be loaded", ((LogicalPort) loaded.getLogicalDevices().get(i)).getProtocolEndpoint().get(j)
						.getLogicalPorts()
						.contains(loaded.getLogicalDevices().get(i)));
			}
		}
	}

	private ComputerSystem createSampleModel() {
		ComputerSystem model = new ComputerSystem();
		model.setName("testRouter");
		model.setElementName("testRouter");

		model.addLogicalDevice(createEthernetPortWithVlan("fe-0/1/2", 0, 0));
		model.addLogicalDevice(createEthernetPortWithVlan("fe-0/1/2", 1, 1));

		return model;
	}

	private LogicalDevice createEthernetPortWithVlan(String interfaceName, int portNumber, int vlanId) {
		EthernetPort eth = new EthernetPort();
		eth.setName(interfaceName);
		eth.setPortNumber(portNumber);

		VLANEndpoint vlanEp = new VLANEndpoint();
		vlanEp.setVlanID(vlanId);
		eth.addProtocolEndpoint(vlanEp);

		return eth;
	}

}
