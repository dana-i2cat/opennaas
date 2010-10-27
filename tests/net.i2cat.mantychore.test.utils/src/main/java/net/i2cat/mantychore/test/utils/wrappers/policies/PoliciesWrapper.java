package cat.i2cat.manticore.test.wrappers.policies;

import java.rmi.RemoteException;
import java.util.Iterator;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;
import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.log4j.Logger;
import org.globus.axis.gsi.GSIConstants;
import org.globus.gsi.jaas.JaasSubject;
import org.globus.wsrf.impl.security.authorization.HostAuthorization;
import org.globus.wsrf.impl.security.authorization.NoAuthorization;
import org.globus.wsrf.security.Constants;
import org.globus.wsrf.security.SecurityException;
import org.ietf.jgss.GSSCredential;
import org.oasis.wsrf.faults.BaseFaultType;

import cat.i2cat.manticore.stubs.policies.FindReq;
import cat.i2cat.manticore.stubs.policies.FindResp;
import cat.i2cat.manticore.stubs.policies.GetPoliciesReq;
import cat.i2cat.manticore.stubs.policies.GetPoliciesResp;
import cat.i2cat.manticore.stubs.policies.PoliciesFactoryPortType;
import cat.i2cat.manticore.stubs.policies.RemovePoliciesReq;
import cat.i2cat.manticore.stubs.policies.RemovePoliciesResp;
import cat.i2cat.manticore.stubs.policies.SetPoliciesReq;
import cat.i2cat.manticore.stubs.policies.SetPoliciesResp;
import cat.i2cat.manticore.stubs.policies.service.PoliciesFactoryServiceAddressingLocator;
import cat.i2cat.manticore.test.util.login.Session;

/**
 * This class implements all the methods to call the Policies web service
 * 
 * @author Fi2CAT
 *
 */
public class PoliciesWrapper {
//////////////////////////////////////////////////////////////////////////////////////////////////
	//									FACTORY SERVICE												//		
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	static {
		org.globus.axis.util.Util.registerTransport();
		 }
	
	private static Logger log = Logger.getLogger(PoliciesWrapper.class);
	/**
	 * Tells the policies factory located at factoryURI to create a user
	 * 
	 * @param request
	 * @param factoryURI
	 * @return The data of the user resource created.
	 * @throws ServiceException
	 * @throws RemoteException
	 */
	public static SetPoliciesResp setPolicies(SetPoliciesReq request, String factoryURI) throws ServiceException, RemoteException, BaseFaultType {
		SetPoliciesResp resp = null;
		
		try {
			PoliciesFactoryPortType policiesFactory = getFactoryPortType(factoryURI);
			log.debug("234");
			resp = policiesFactory.setPolicies(request);
			log.debug("234");
		} catch(MalformedURIException ex) {
			ex.printStackTrace();
		}

		return resp;
	}
	
	/**
	 * Tells the policies factory located at factoryURI to get the EPRs of user resources that
	 * match a given template
	 * 
	 * @param request
	 * @param factoryURI
	 * @return The list of the user instances available in the DB
	 * @throws ServiceException
	 * @throws RemoteException
	 */
	public static FindResp find(FindReq request, String factoryURI) throws ServiceException, RemoteException, BaseFaultType {
		FindResp result = null;
		
		try {
			PoliciesFactoryPortType userFactory = getFactoryPortType(factoryURI);  
			result = userFactory.find(request);
		} catch(MalformedURIException ex) {
			ex.printStackTrace();
		}

		return result;
	}
	
	/**
	 * Tells the policies factory located at factoryURI to get the EPRs of user resources that
	 * match a given template
	 * 
	 * @param request
	 * @param factoryURI
	 * @return The list of the user instances available in the DB
	 * @throws ServiceException
	 * @throws RemoteException
	 */
	public static GetPoliciesResp getPolicies(GetPoliciesReq request, String factoryURI) throws ServiceException, RemoteException, BaseFaultType {
		GetPoliciesResp result = null;
		
		try {
			PoliciesFactoryPortType userFactory = getFactoryPortType(factoryURI);  
			result = userFactory.getPolicies(request);
		} catch(MalformedURIException ex) {
			ex.printStackTrace();
		}

		return result;
	}
	/**
	 * Removes the policies associated to one router resource
	 * 
	 * @param request
	 * @param factoryURI
	 * @return The list of the user instances available in the DB
	 * @throws ServiceException
	 * @throws RemoteException
	 */
	public static RemovePoliciesResp removePolicies(RemovePoliciesReq request, String factoryURI) throws ServiceException, RemoteException, BaseFaultType {
		RemovePoliciesResp result = null;
		
		try {
			PoliciesFactoryPortType policiesFactory = getFactoryPortType(factoryURI);  
			result = policiesFactory.removePolicies(request);
		} catch(MalformedURIException ex) {
			ex.printStackTrace();
		}

		return result;
	}

	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//								INSTANCE, FACTORY, SECURITY										//		
	//////////////////////////////////////////////////////////////////////////////////////////////////

	
	private static PoliciesFactoryPortType getFactoryPortType(String factoryURI) throws MalformedURIException, ServiceException, SecurityException {
		PoliciesFactoryServiceAddressingLocator factory = new PoliciesFactoryServiceAddressingLocator();
		EndpointReferenceType endpoint = new EndpointReferenceType();
		endpoint.setAddress(new Address(factoryURI));
		PoliciesFactoryPortType userFactory = factory.getPoliciesFactoryPortTypePort(endpoint);
		setSecurity((Stub)userFactory);
		return userFactory;
	}
	
	private static void setSecurity(Stub stub) throws SecurityException {
		stub._setProperty(Constants.GSI_SEC_CONV, Constants.SIGNATURE);
		stub._setProperty(GSIConstants.GSI_CREDENTIALS, Session.getInstance().getCredentials());
		stub._setProperty(Constants.AUTHORIZATION, HostAuthorization.getInstance());
		stub._setProperty(GSIConstants.GSI_MODE,GSIConstants.GSI_MODE_FULL_DELEG);
	}
}
