package cat.i2cat.manticore.test.wrappers.ipnetwork;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;
import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.globus.axis.gsi.GSIConstants;
import org.globus.axis.util.Util;
import org.globus.wsrf.impl.security.authorization.HostAuthorization;
import org.globus.wsrf.security.Constants;
import org.oasis.wsrf.faults.BaseFaultType;
import org.oasis.wsrf.lifetime.Destroy;

import cat.i2cat.manticore.stubs.ipnetwork.AddDirectedLinkReq;
import cat.i2cat.manticore.stubs.ipnetwork.AddDirectedLinkResp;
import cat.i2cat.manticore.stubs.ipnetwork.AddRouterReq;
import cat.i2cat.manticore.stubs.ipnetwork.AddRouterResp;
import cat.i2cat.manticore.stubs.ipnetwork.CommitQueueReq;
import cat.i2cat.manticore.stubs.ipnetwork.CommitQueueResp;
import cat.i2cat.manticore.stubs.ipnetwork.CreateReq;
import cat.i2cat.manticore.stubs.ipnetwork.CreateResp;
import cat.i2cat.manticore.stubs.ipnetwork.DelDirectedLinksReq;
import cat.i2cat.manticore.stubs.ipnetwork.DelDirectedLinksResp;
import cat.i2cat.manticore.stubs.ipnetwork.DelRouterReq;
import cat.i2cat.manticore.stubs.ipnetwork.DelRouterResp;
import cat.i2cat.manticore.stubs.ipnetwork.DeleteActionsFromQueueReq;
import cat.i2cat.manticore.stubs.ipnetwork.DeleteActionsFromQueueResp;
import cat.i2cat.manticore.stubs.ipnetwork.EmptyQueueReq;
import cat.i2cat.manticore.stubs.ipnetwork.FindReq;
import cat.i2cat.manticore.stubs.ipnetwork.FindResp;
import cat.i2cat.manticore.stubs.ipnetwork.GetActionQueueReq;
import cat.i2cat.manticore.stubs.ipnetwork.GetActionQueueResp;
import cat.i2cat.manticore.stubs.ipnetwork.GetDefaultNetworksReq;
import cat.i2cat.manticore.stubs.ipnetwork.GetDefaultNetworksResp;
import cat.i2cat.manticore.stubs.ipnetwork.GetIPNetworksReq;
import cat.i2cat.manticore.stubs.ipnetwork.GetIPNetworksResp;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkFactoryPortType;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkPortType;
import cat.i2cat.manticore.stubs.ipnetwork.InvokeReq;
import cat.i2cat.manticore.stubs.ipnetwork.InvokeResp;
import cat.i2cat.manticore.stubs.ipnetwork.ModifyReq;
import cat.i2cat.manticore.stubs.ipnetwork.ModifyResp;
import cat.i2cat.manticore.stubs.ipnetwork.QueueActionReq;
import cat.i2cat.manticore.stubs.ipnetwork.QueueActionResp;
import cat.i2cat.manticore.stubs.ipnetwork.SetQueueReq;
import cat.i2cat.manticore.stubs.ipnetwork.SetQueueResp;
import cat.i2cat.manticore.stubs.ipnetwork.service.IPNetworkFactoryServiceAddressingLocator;
import cat.i2cat.manticore.stubs.ipnetwork.service.IPNetworkServiceAddressingLocator;
import cat.i2cat.manticore.test.util.login.Session;


public class IPNetworkWrapper {

	
	static {
		  Util.registerTransport();
	}
	/**
	 * Create new IPNetwork resource
	 * @param request
	 * @param factoryURI
	 * @return
	 * @throws ServiceException
	 * @throws RemoteException
	 * @throws BaseFaultType
	 */
	public static CreateResp create (CreateReq request, String factoryURI)throws ServiceException, RemoteException, BaseFaultType{
		CreateResp response = new CreateResp();
		try {
			IPNetworkFactoryPortType factoryPortType = getFactoryPortType(factoryURI + "/wsrf/services/manticore/IPNetworkFactoryService");
			response = factoryPortType.create(request);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		} 
		
		return response;
	}
	/**
	 * Find IPNetwork instances that matches the template
	 * @param request
	 * @param factoryURI
	 * @return
	 * @throws ServiceException
	 * @throws BaseFaultType
	 * @throws RemoteException
	 */
	public static GetIPNetworksResp getIPNetworks(GetIPNetworksReq request, String factoryURI) throws ServiceException, BaseFaultType, RemoteException{
		if(request.getTemplate().getId()==null){
			request.getTemplate().setId(new String[]{""});
		}
		GetIPNetworksResp response = new GetIPNetworksResp();
		try {
			IPNetworkFactoryPortType factoryPortType = getFactoryPortType(factoryURI + "/wsrf/services/manticore/IPNetworkFactoryService");
			response = factoryPortType.getIPNetworks(request);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		} 
		return response;
	}
	/**
	 * Find IPnetworks instances that matches the template
	 * @param request
	 * @param factoryURI
	 * @return
	 * @throws ServiceException
	 * @throws BaseFaultType
	 * @throws RemoteException
	 */
	public static FindResp findIPNetworks(FindReq request, String factoryURI) throws ServiceException, BaseFaultType, RemoteException{
		FindResp response = new FindResp();
		try {
			IPNetworkFactoryPortType factoryPortType = getFactoryPortType(factoryURI + "/wsrf/services/manticore/IPNetworkFactoryService");
			response = factoryPortType.find(request);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		} 
		return response;
	}
	/**
	 * Modify the parameters of IPNetwork
	 * @param request
	 * @param ipnetworkKey
	 * @param factoryURI
	 * @return
	 * @throws Exception
	 */
	public static ModifyResp modifyIPNetwork(ModifyReq request, EndpointReferenceType eprIPnetwork) throws Exception{
		ModifyResp response;
		IPNetworkPortType portType = getInstancePortType(eprIPnetwork);		
		response = portType.modify(request);
		
		return response;
		
	}
	/**
	 * Add routers to IPNetwork
	 * @param request
	 * @param ipnetworkKey
	 * @param factoryURI
	 * @return
	 * @throws Exception
	 */
	public static AddRouterResp addRouterToIPNetwork(AddRouterReq request, EndpointReferenceType eprIPnetwork) throws Exception{
		IPNetworkPortType portType = getInstancePortType(eprIPnetwork);
		return portType.addRouters(request);
	}
	
	/**
	 * Delete Routers From IP network
	 * @param request
	 * @param ipnetworkKey
	 * @param factoryURI
	 * @return
	 * @throws Exception
	 */
	public static DelRouterResp deleteRouterFromIPNetwork(DelRouterReq request, EndpointReferenceType eprIPnetwork) throws Exception {
		IPNetworkPortType portType = getInstancePortType(eprIPnetwork);
		return portType.deleteRouters(request);
	}
	/**
	 * Add one new directed link to the network graph
	 * @param request
	 * @param eprIPnetwork
	 * @return
	 * @throws Exception
	 */
	public static AddDirectedLinkResp addDirectedLink(AddDirectedLinkReq request, EndpointReferenceType eprIPnetwork)throws Exception{
		IPNetworkPortType portType = getInstancePortType(eprIPnetwork);
		return portType.addDirectedLink(request);
	}
	/**
	 * Delete a set of directed links from the network graph
	 * @param request
	 * @param eprIPnetwork
	 * @return
	 * @throws Exception
	 */
	public static DelDirectedLinksResp delDirectedLinks(DelDirectedLinksReq request, EndpointReferenceType eprIPnetwork) throws Exception{
		IPNetworkPortType portType = getInstancePortType(eprIPnetwork);
		return portType.deleteDirectedLinks(request);
	}
	/**
	 * Invoke a set of actions over the IPNetwork
	 * @param request
	 * @param eprIPnetwork
	 * @return
	 * @throws Exception
	 */
	public static InvokeResp invoke(InvokeReq request, EndpointReferenceType eprIPnetwork) throws Exception{
		IPNetworkPortType portType = getInstancePortType(eprIPnetwork);
		return portType.invoke(request);
	}
	/**
	 * Get the Action queue from service
	 * @param request
	 * @param eprIPnetwork
	 * @return
	 * @throws Exception
	 */
	public static GetActionQueueResp getActionQueue(GetActionQueueReq request, EndpointReferenceType eprIPnetwork) throws Exception{
		IPNetworkPortType portType = getInstancePortType(eprIPnetwork);
		return portType.getActionQueue(request);
	}
	/**
	 * Send and execute the action queue
	 * @param request
	 * @param eprIPnetwork
	 * @return
	 * @throws Exception
	 */
	public static CommitQueueResp commitQueue(CommitQueueReq request, EndpointReferenceType eprIPnetwork) throws Exception{
		IPNetworkPortType portType = getInstancePortType(eprIPnetwork);
		return portType.commitQueue(request);
	}
	/**
	 * Add a new action to queue
	 * @param request
	 * @param eprIPnetwork
	 * @return
	 * @throws Exception
	 */
	public static QueueActionResp queueAction(QueueActionReq request, EndpointReferenceType eprIPnetwork) throws Exception{
		IPNetworkPortType portType = getInstancePortType(eprIPnetwork);
		return portType.queueAction(request);
	}
	/**
	 * EmptyQueue
	 * @param request
	 * @param ipnetworkEPR
	 * @throws RemoteException 
	 * @throws BaseFaultType 
	 */
	public static void emptyQueue(EmptyQueueReq request, EndpointReferenceType ipnetworkEPR) throws Exception {
		IPNetworkPortType portType = getInstancePortType(ipnetworkEPR);
		portType.emptyQueue(request);
	}
	/**
	 * Delete one or more actions from queue
	 * @param request
	 * @param ipnetworkEPR
	 * @return
	 * @throws Excpetion
	 */
	public static DeleteActionsFromQueueResp deleteActionsFromQueue(DeleteActionsFromQueueReq request, EndpointReferenceType ipnetworkEPR) throws Exception{
		IPNetworkPortType portType = getInstancePortType(ipnetworkEPR);
		return  portType.deleteActionsFromQueue(request);
	}
	/**
	 * Set the actions in Queue
	 * @param request
	 * @param ipnetworkEPR
	 * @return
	 */
	public static SetQueueResp setQueue(SetQueueReq request,EndpointReferenceType ipnetworkEPR)  throws Exception{
		IPNetworkPortType portType = getInstancePortType(ipnetworkEPR);
		return  portType.setQueue(request);
	}
	/**
	 * Get the default networks for RIP protocol
	 * @param request
	 * @return
	 */
	public static GetDefaultNetworksResp getDefaultNetworks(GetDefaultNetworksReq request, EndpointReferenceType ipnetworkEPR) throws Exception{
		IPNetworkPortType portType = getInstancePortType(ipnetworkEPR);
		return  portType.getDefaultNetworks(request);
	}
	
	/**
	 * Remove the IPNetwork resource
	 * @param ipnetworkKey
	 * @param factoryURI
	 * @throws Exception
	 */
	public static void removeIPNetwork(EndpointReferenceType eprIPnetwork) throws Exception{
		IPNetworkPortType portType = getInstancePortType(eprIPnetwork);
		portType.destroy(new Destroy());
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//								INSTANCE, FACTORY, SECURITY										//		
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static IPNetworkPortType getInstancePortType(EndpointReferenceType eprIPnetwork) throws Exception  {
		IPNetworkServiceAddressingLocator instance = new IPNetworkServiceAddressingLocator();
		IPNetworkPortType ipnetwork = instance.getIPNetworkPortTypePort(eprIPnetwork);
		setSecurity((Stub) ipnetwork);
		
		return ipnetwork;
	}
	
	private static IPNetworkFactoryPortType getFactoryPortType(String factoryURI) throws MalformedURIException, ServiceException {
		IPNetworkFactoryServiceAddressingLocator factory = new IPNetworkFactoryServiceAddressingLocator();
		EndpointReferenceType endpoint = new EndpointReferenceType();
		endpoint.setAddress(new Address(factoryURI));
		IPNetworkFactoryPortType ipnetworkFactory = factory.getIPNetworkFactoryPortTypePort(endpoint);
		setSecurity((Stub)ipnetworkFactory);
		
		return ipnetworkFactory;
	}
	
	private static void setSecurity(Stub stub) {
		stub._setProperty(Constants.GSI_SEC_CONV, Constants.SIGNATURE);
		stub._setProperty(GSIConstants.GSI_CREDENTIALS, Session.getInstance().getCredentials());
		stub._setProperty(Constants.AUTHORIZATION, HostAuthorization.getInstance());
		stub._setProperty(GSIConstants.GSI_MODE,GSIConstants.GSI_MODE_FULL_DELEG);
	}
	
}
