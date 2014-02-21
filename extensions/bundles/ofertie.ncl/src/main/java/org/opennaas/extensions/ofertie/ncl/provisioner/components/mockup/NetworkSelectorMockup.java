package org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.opennaas.extensions.ofertie.ncl.Activator;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.INetworkSelector;

public class NetworkSelectorMockup implements INetworkSelector {

	private static final Log	log	= LogFactory.getLog(NetworkSelectorMockup.class);

	private IResourceManager	resourceManager;
	private String				resourceType;
	private String				resourceName;

	public String getNetworkType() {
		return resourceType;
	}

	private static final String	RESOURCE_ID_FILE	= "org.ofertie.ncl.network";
	private static final String	RESOURCE_NAME_KEY	= "resource.name";
	private static final String	RESOURCE_TYPE_KEY	= "resource.type";

	public void init() throws IOException, ConfigurationException {

		log.debug("Reading configuration file " + RESOURCE_ID_FILE);

		ConfigurationAdminUtil configurationAdmin = new ConfigurationAdminUtil(Activator.getBundleContext());

		resourceName = configurationAdmin.getProperty(RESOURCE_ID_FILE, RESOURCE_NAME_KEY);
		resourceType = configurationAdmin.getProperty(RESOURCE_ID_FILE, RESOURCE_TYPE_KEY);

		if (StringUtils.isEmpty(resourceName) || StringUtils.isEmpty(resourceType))
			throw new ConfigurationException(RESOURCE_ID_FILE + " file should contain attributes " + RESOURCE_NAME_KEY + " and " + RESOURCE_TYPE_KEY);

		log.info("NetworkSelector initalized with configured resource " + resourceType + ":" + resourceName);

	}

	/**
	 * @return the resourceManager
	 */
	public IResourceManager getResourceManager() {
		return resourceManager;
	}

	/**
	 * @param resourceManager
	 *            the resourceManager to set
	 */
	public void setResourceManager(IResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	@Override
	public String findNetworkForRequest(QosPolicyRequest qosPolicyRequest)
			throws Exception {
		return getConfiguredNetwork();
	}

	@Override
	public String findNetworkForFlowId(String flowId) throws Exception {
		return getConfiguredNetwork();
	}

	/**
	 * Returns the id of the resource identified by the resourceType and resourceName set in the configuration file.
	 * 
	 * @return
	 * @throws ResourceException
	 */
	private String getConfiguredNetwork() throws ResourceException {
		return resourceManager.getIdentifierFromResourceTypeName(resourceType, resourceName);
	}

}
