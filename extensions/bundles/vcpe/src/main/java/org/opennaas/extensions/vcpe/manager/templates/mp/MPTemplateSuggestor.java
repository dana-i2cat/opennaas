package org.opennaas.extensions.vcpe.manager.templates.mp;

import java.io.IOException;

import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

public class MPTemplateSuggestor {

	private MPTemplateDefaultValuesLoader	defaultsLoader;

	public MPTemplateSuggestor() {
		defaultsLoader = new MPTemplateDefaultValuesLoader();
	}

	public void initialize() throws VCPENetworkManagerException {
		try {
			defaultsLoader.initialize();
		} catch (IOException e) {
			throw new VCPENetworkManagerException(e);
		}
	}

	/**
	 * 
	 * @param physicalModel
	 *            containing ALL physical elements in the template
	 * @return physicalModel populated with suggested values
	 * @throws VCPENetworkManagerException
	 *             if failed to suggest a valid vcpe physical model
	 */
	public VCPENetworkModel getSuggestionForPhysicalModel(VCPENetworkModel physicalModel) throws VCPENetworkManagerException {
		if (!defaultsLoader.isInitialized())
			throw new VCPENetworkManagerException("Suggestor is not initialized");

		// suggestion made from default values
		return defaultsLoader.loadDefaultPhysicalModel(physicalModel);
	}

	/**
	 * Populates given logicalModel with suggested values. Given physicalModel is used as read-only: It's used for suggesting, but it's not modified
	 * during this method.
	 * 
	 * @param physicalModel
	 *            containing ALL physical elements in the template, and nothing else
	 * @param logicalModel
	 *            containing ALL logical elements in the template, and nothing else
	 * @return given logicalModel with logical elements populated with suggested values
	 * @throws VCPENetworkManagerException
	 *             if failed to suggest a valid vcpe logical model
	 */
	public VCPENetworkModel getSuggestionForLogicalModel(VCPENetworkModel physicalModel, VCPENetworkModel logicalModel)
			throws VCPENetworkManagerException {
		if (!defaultsLoader.isInitialized())
			throw new VCPENetworkManagerException("Suggestor is not initialized");

		return defaultsLoader.loadDefaultLogicalModel(logicalModel);
	}

}
