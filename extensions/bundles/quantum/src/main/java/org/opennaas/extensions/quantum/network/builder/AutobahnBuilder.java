package org.opennaas.extensions.quantum.network.builder;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.bod.autobahn.model.AutobahnLink;
import org.opennaas.extensions.bod.capability.l2bod.BoDLink;
import org.opennaas.extensions.bod.capability.l2bod.IL2BoDCapability;
import org.opennaas.extensions.bod.capability.l2bod.RequestConnectionParameters;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.NetworkElement;
import org.opennaas.extensions.quantum.Activator;
import org.opennaas.extensions.quantum.QuantumException;
import org.opennaas.extensions.quantum.model.AutobahnElement;
import org.opennaas.extensions.quantum.model.Network;
import org.opennaas.extensions.quantum.model.NetworkModel;
import org.opennaas.extensions.quantum.model.QuantumModel;
import org.opennaas.extensions.quantum.model.Resource;
import org.opennaas.extensions.quantum.model.ResourceElement;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public class AutobahnBuilder implements NetworkBuilder {

	private Log					log				= LogFactory.getLog(AutobahnBuilder.class);

	private static final String	PROPERTIES_PATH	= "org.opennaas.extensions.quantum.autobahn";

	/**
	 * Set link capacity to 100 MB/s
	 */
	private static final long	CAPACITY		= 100 * 1000000L;

	@Override
	public NetworkModel buildNetwork(Network network) throws QuantumException {

		log.info("Building Autobahn network");

		RequestConnectionParameters requestParams;

		try {

			Properties props = loadAutobahnProperties();

			IResource autobahnResource = NetworkBuilderHelper.getResourceByTypeAndName(props.getProperty(AutobahnPropertiesConstants.RESOURCE_NAME),
					"bod");

			requestParams = createL2BoDCreateConnectionRequest(props);

			IL2BoDCapability capability = (IL2BoDCapability) autobahnResource.getCapabilityByInterface(IL2BoDCapability.class);
			capability.requestConnection(requestParams);

			NetworkBuilderHelper.executeResourceQueue(autobahnResource);

			org.opennaas.extensions.network.model.NetworkModel model = (org.opennaas.extensions.network.model.NetworkModel) autobahnResource
					.getModel();
			BoDLink link = getBoDLinkByRequestConnection(model, requestParams);

			if (link == null)
				throw new QuantumException("There's no link in Autobahn model with the requested parameters.");

			NetworkModel builtModel = createBuiltModel(network.getId(), autobahnResource.getResourceIdentifier().getId(), link);

			log.info("Autobahn network built");

			return builtModel;

		} catch (Exception e) {
			log.error("Error building Autobahn Network : " + e.getMessage());
			throw new QuantumException(e);
		}

	}

	@Override
	public void destroyNetwork(IResource quantumResource, Network network) throws QuantumException {

		log.info("Destroying Autobahn network");

		IResource autobahnResource;

		try {

			String netId = network.getId();
			QuantumModel model = (QuantumModel) quantumResource.getModel();

			NetworkModel netModel = NetworkBuilderHelper.getNetworkModelFromQuantumNetworkId(model.getNetworksModel(), netId);
			if (netModel == null)
				throw new QuantumException("There's no resource referenced by Quantum network " + network.getName());

			for (Resource resource : netModel.getResources()) {
				String resourceId = resource.getResourceId();
				autobahnResource = NetworkBuilderHelper.getResourceById(resourceId);
				for (ResourceElement element : resource.getResourceElement())
					if (element instanceof AutobahnElement)
						removeAutobahnLinks(autobahnResource, (AutobahnElement) element);
			}
		} catch (Exception e) {
			log.error("Error destroying Autobahn Network : " + e.getMessage());
			throw new QuantumException(e);
		}
	}

	private void removeAutobahnLinks(IResource autobahnResource, AutobahnElement autobahnElem) throws ResourceException, ProtocolException {

		for (BoDLink link : autobahnElem.getLinks()) {

			IL2BoDCapability capability = (IL2BoDCapability) autobahnResource.getCapabilityByInterface(IL2BoDCapability.class);
			capability.shutDownConnection(link);

		}

		NetworkBuilderHelper.executeResourceQueue(autobahnResource);

	}

	private BoDLink getBoDLinkByRequestConnection(org.opennaas.extensions.network.model.NetworkModel model, RequestConnectionParameters requestParams) {

		List<NetworkElement> links = NetworkModelHelper.getNetworkElementsByClassName(AutobahnLink.class, model.getNetworkElements());

		for (NetworkElement link : links) {
			AutobahnLink bodLink = (AutobahnLink) link;
			if (compareRequestParameters(bodLink.getRequestParameters(), requestParams))
				return bodLink;
		}

		return null;

	}

	/**
	 * Loads Autobahn properties from configuration file, and asserts all expected attributes are set.
	 * 
	 * @return
	 * @throws IOException
	 * @throws QuantumException
	 */
	private Properties loadAutobahnProperties() throws IOException, QuantumException {

		log.debug("Loading autobahn properties from config file " + PROPERTIES_PATH);

		Properties props = loadProperties();

		if (props.get(AutobahnPropertiesConstants.RESOURCE_NAME) == null)
			throw new QuantumException("Autobahn configuration file does not contain any property with name \"autobahn.resource.name\"");
		if (props.get(AutobahnPropertiesConstants.PORT_SRC) == null)
			throw new QuantumException("Autobahn configuration file does not contain any property with name \"autobahn.port.src\"");
		if (props.get(AutobahnPropertiesConstants.PORT_DST) == null)
			throw new QuantumException("Autobahn configuration file does not contain any property with name \"autobahn.port.dst\"");
		if (props.get(AutobahnPropertiesConstants.VLAN_SRC) == null)
			throw new QuantumException("Autobahn configuration file does not contain any property with name \"autobahn.vlan.src\"");
		if (props.get(AutobahnPropertiesConstants.VLAN_DST) == null)
			throw new QuantumException("Autobahn configuration file does not contain any property with name \"autobahn.vlan.dst\"");

		log.debug("Autobahn properties loaded.");

		return props;
	}

	private RequestConnectionParameters createL2BoDCreateConnectionRequest(Properties props) throws ActivatorException, ResourceException {

		log.debug("Building Autobahn request to create a new connection");

		IResource autobahnResource = NetworkBuilderHelper.getResourceByTypeAndName(props.getProperty(AutobahnPropertiesConstants.RESOURCE_NAME),
				"bod");
		org.opennaas.extensions.network.model.NetworkModel autobahNModel = (org.opennaas.extensions.network.model.NetworkModel) autobahnResource
				.getModel();

		Interface srcInt = NetworkBuilderHelper.getInterfaceByName(autobahNModel.getNetworkElements(),
				props.getProperty(AutobahnPropertiesConstants.PORT_SRC));
		Interface srcDst = NetworkBuilderHelper.getInterfaceByName(autobahNModel.getNetworkElements(),
				props.getProperty(AutobahnPropertiesConstants.PORT_DST));

		DateTime startDate = DateTime.now();
		DateTime expirationDate = startDate.plusYears(1);

		int srcVlan = Integer.parseInt(props.getProperty(AutobahnPropertiesConstants.VLAN_SRC));
		int dstVlan = Integer.parseInt(props.getProperty(AutobahnPropertiesConstants.VLAN_DST));

		RequestConnectionParameters parameters = new RequestConnectionParameters(srcInt, srcDst, CAPACITY, srcVlan, dstVlan,
				startDate, expirationDate);

		log.debug("Autobahn request built.");

		return parameters;

	}

	private NetworkModel createBuiltModel(String networkId, String resourceId, BoDLink link) {
		AutobahnElement autobahnElement = new AutobahnElement();
		autobahnElement.addLink(link);

		Resource resource = new Resource();
		resource.setResourceId(resourceId);
		resource.addResourceElement(autobahnElement);

		NetworkModel builtModel = new NetworkModel();
		builtModel.setQuantumNetworkId(networkId);
		builtModel.addResource(resource);

		return builtModel;
	}

	private Properties loadProperties() throws IOException {
		Properties props = ConfigurationAdminUtil.getProperties(Activator.getContext(), PROPERTIES_PATH);
		if (props == null)
			throw new IOException("Failed to load physicalInfrastructure configuration file " + PROPERTIES_PATH);

		return props;
	}

	private boolean compareRequestParameters(RequestConnectionParameters requestParams1, RequestConnectionParameters requestParams2) {

		if (requestParams1.capacity != requestParams2.capacity)
			return false;

		if (requestParams1.interface1 == null) {
			if (requestParams2.interface1 != null)
				return false;
		}
		if (requestParams1.interface2 == null) {
			if (requestParams2.interface2 != null)
				return false;
		}
		if (requestParams1.vlanid1 != requestParams2.vlanid1)
			return false;
		if (requestParams1.vlanid2 != requestParams2.vlanid2)
			return false;

		return true;

	}
}
