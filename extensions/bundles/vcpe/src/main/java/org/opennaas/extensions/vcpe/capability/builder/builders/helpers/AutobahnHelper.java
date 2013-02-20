/**
 * 
 */
package org.opennaas.extensions.vcpe.capability.builder.builders.helpers;

import org.joda.time.DateTime;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.bod.capability.l2bod.IL2BoDCapability;
import org.opennaas.extensions.bod.capability.l2bod.RequestConnectionParameters;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.vcpe.capability.VCPEToBoDModelTranslator;
import org.opennaas.extensions.vcpe.model.Domain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

/**
 * @author Jordi
 */
public class AutobahnHelper extends GenericHelper {

	/**
	 * Set link capacity to 100 MB/s
	 */
	private static final long	CAPACITY	= 100 * 1000000L;

	/**
	 * Create Autobahn Link
	 * 
	 * @param model
	 * @param src
	 * @param dst
	 * @param srcVlan
	 * @param dstVlan
	 * @throws ResourceException
	 */
	public static void createAutobahnLink(VCPENetworkModel model, Domain bod, Interface src, Interface dst, long srcVlan, long dstVlan)
			throws ResourceException {

		IResource autobahn = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("bod", bod.getName()));

		RequestConnectionParameters requestParams = createL2BoDCreateConnectionRequest(src, dst, srcVlan, dstVlan, autobahn);

		IL2BoDCapability capability = (IL2BoDCapability) autobahn.getCapabilityByInterface(IL2BoDCapability.class);
		capability.requestConnection(requestParams);
	}

	/**
	 * Destroy Autobahn Link
	 * 
	 * @param model
	 * @param src
	 * @param dst
	 * @param srcVlan
	 * @param dstVlan
	 * @throws ResourceException
	 */
	public static void destroyAutobahnLink(VCPENetworkModel model, Domain bod, Interface src, Interface dst, long srcVlan, long dstVlan)
			throws ResourceException {

		IResource autobahn = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("bod", bod.getName()));

		RequestConnectionParameters requestParams = createL2BoDCreateConnectionRequest(src, dst, srcVlan, dstVlan, autobahn);

		IL2BoDCapability capability = (IL2BoDCapability) autobahn.getCapabilityByInterface(IL2BoDCapability.class);
		capability.shutDownConnection(requestParams);
	}

	/**
	 * @param src
	 * @param dst
	 * @param srcVlan
	 * @param dstVlan
	 * @param autobahn
	 * @return
	 */
	private static RequestConnectionParameters createL2BoDCreateConnectionRequest(Interface src, Interface dst, long srcVlan, long dstVlan,
			IResource autobahn) {

		NetworkModel model = (NetworkModel) autobahn.getModel();

		DateTime startDate = DateTime.now();
		DateTime expirationDate = startDate.plusYears(1);

		RequestConnectionParameters parameters = new RequestConnectionParameters(
				VCPEToBoDModelTranslator.vCPEInterfaceToBoDInterface(src, model),
				VCPEToBoDModelTranslator.vCPEInterfaceToBoDInterface(dst, model),
				CAPACITY,
				Integer.parseInt(Long.toString(srcVlan)), Integer.parseInt(Long.toString(dstVlan)),
				startDate, expirationDate);

		return parameters;
	}

}
