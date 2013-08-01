package org.opennaas.extensions.router.opener.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.opennaas.extensions.router.opener.client.rpc.AddInterfaceRequest;
import org.opennaas.extensions.router.opener.client.rpc.DeleteInterfaceIPRequest;
import org.opennaas.extensions.router.opener.client.rpc.GetInterfaceResponse;
import org.opennaas.extensions.router.opener.client.rpc.GetInterfacesResponse;
import org.opennaas.extensions.router.opener.client.rpc.SetInterfaceIPRequest;
import org.opennaas.extensions.router.opener.client.rpc.SetInterfaceResponse;

@Path("/")
public interface OpenerQuaggaOpenAPI {
	
	/**
	 * The getInterface REST call retrieves detailed information about an interface. 
	 * It takes interface name as input and returns several parameters associated with the respective interface. 
	 * The XML response below shows the information returned by this call. 
	 * If the requested interface is not found on the queried system then it returns 404 error indicating interface not found.
	 * @param interfaceName
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("getInterface/{interfaceName}")
	@Consumes(MediaType.TEXT_XML + ";charset=UTF-8")
	@Produces(MediaType.TEXT_XML + ";charset=UTF-8")
	public GetInterfaceResponse getInterface(@PathParam("interfaceName") String interfaceName) throws Exception;
	
	/**
	 * The getInterfaces REST call returns the list of interfaces available on the queried system (names only).
	 * It does not take any parameter as input. 
	 * @return Names of interfaces available on the queried system.
	 * @throws Exception
	 */
	@GET
	@Path("getInterfaces")
	@Consumes(MediaType.TEXT_XML + ";charset=UTF-8")
	@Produces(MediaType.TEXT_XML + ";charset=UTF-8")
	public GetInterfacesResponse getInterfaces() throws Exception;
	
	/**
	 * The setInterface REST call set IP address for a given interface. 
	 * It takes interface name, IP address, prefix length as input in the request body of the query. 
	 * There is another additional input parameter required in the URI, called pip that is previous IP. It can have two values, either 0 or 1. 
	 * With pip set to 0, will remove all the previous IP address associated with the interface given in the request body 
	 * and with pip equal 1, it will keep the previous address and attach the new one to it.
	 * @param interfaceName
	 * @param ipAddress
	 * @param prefixLength
	 * @param keepCurrentAddresses
	 * @throws Exception
	 */
	@PUT
	@Path("setInterface")
	@Consumes(MediaType.TEXT_XML + ";charset=UTF-8")
	@Produces(MediaType.TEXT_XML + ";charset=UTF-8")
	public SetInterfaceResponse setInterfaceIPAddress(SetInterfaceIPRequest request, @QueryParam("pip") int keepCurrentAddresses) throws Exception;
	
	/**
	 * The deleteInterfaceIP REST call can be used to delete an IP address associated with a given interface. 
	 * This call takes interface name, IP address and prefix length as input parameters 
	 * and returns 200 message code if successful otherwise returns a specific error code.
	 * @param interfaceName
	 * @param ipAddress
	 * @param prefixLength
	 * @throws Exception
	 */
	@PUT
	@Path("deleteInterfaceIP")
	@Consumes(MediaType.TEXT_XML + ";charset=UTF-8")
	@Produces(MediaType.TEXT_XML + ";charset=UTF-8")
	public SetInterfaceResponse deleteInterfaceIPAddress(DeleteInterfaceIPRequest request) throws Exception;
	
	/**
	 * The addInterface REST call adds a new virtual interface. 
	 * It takes the name of the interface as parameter.
	 * It returns 200 message code if successful otherwise returns specific error code. 
	 * The interface created by this call only last till the current session with the respective backend module, 
	 * as the created virtual interface is not permanently created in the kernel of the queried system.
	 *
	 * @param interfaceName
	 * @throws Exception
	 */
	@POST
	@Path("addInterface")
	@Consumes(MediaType.TEXT_XML + ";charset=UTF-8")
	@Produces(MediaType.TEXT_XML + ";charset=UTF-8")
	public SetInterfaceResponse addInterface(AddInterfaceRequest request) throws Exception;
	
	/**
	 * The deleteInterface REST call deletes a virtual interface created by addInterface REST call. 
	 * It takes the name of the interface as parameter. 
	 * It returns 200 message code if successful otherwise returns specific error code.
	 * @param interfaceName
	 * @throws Exception
	 */
	@GET
	@Path("deleteInterface/{interfaceName}")
	@Consumes(MediaType.TEXT_XML + ";charset=UTF-8")
	@Produces(MediaType.TEXT_XML + ";charset=UTF-8")
	public SetInterfaceResponse deleteInterface(@PathParam("interfaceName") String interfaceName) throws Exception;
	
	// TODO METHODS REGARDING UP/DOWN INTERFACES MISSING
	// TODO METHODS REGARDING ROUTES MISSING 

}
