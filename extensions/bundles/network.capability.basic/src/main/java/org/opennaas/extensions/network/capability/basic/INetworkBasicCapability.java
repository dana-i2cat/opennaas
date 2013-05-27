package org.opennaas.extensions.network.capability.basic;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.extensions.network.model.topology.NetworkConnection;

@Path("/")
public interface INetworkBasicCapability extends ICapability {

	/**
	 * Adds resource to network topology.
	 * 
	 * @param resourceId
	 *            the id of the resource
	 * 
	 * @return NetworkModel updated with added resource
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	@Path("/addResource")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void addResource(String resourceId) throws CapabilityException;

	/**
	 * Adds resource to network topology.
	 * 
	 * @param resourceToAdd
	 *            IResource to be added
	 * @return NetworkModel updated with added resource
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	public NetworkModel addResource(IResource resourceToAdd) throws CapabilityException;

	/**
	 * Removes a resource from network topology.
	 * 
	 * @param resourceId
	 *            the id of the resource
	 * @return NetworkModel updated with added resource
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	@Path("/removeResource")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void removeResource(String resourceId) throws CapabilityException;

	/**
	 * Removes a resource from network topology.
	 * 
	 * @param resourceId
	 *            the id of the resource
	 * @return NetworkModel updated without resourceToRemove
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	public NetworkModel removeResource(IResource resourceToRemove) throws CapabilityException;

	/**
	 * Creates a L2 connection between given interfaces in network topology.
	 * 
	 * This method does not interact with real interfaces, connection is only created in topology, despite having real connectivity or not.
	 * 
	 * @param link
	 * @return NetworkConnection linking given interfaces.
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	@Path("/l2attach")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void l2attach(Link link) throws CapabilityException;

	/**
	 * Creates a L2 connection between given interfaces in network topology.
	 * 
	 * This method does not interact with real interfaces, connection is only created in topology, despite having real connectivity or not.
	 * 
	 * @param interface1
	 * @param interface2
	 * @return NetworkConnection linking given interfaces.
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	public NetworkConnection l2attach(Interface interface1, Interface interface2) throws CapabilityException;

	/**
	 * Removes a L2 connection between given interfaces in network topology.
	 * 
	 * This method does not interact with real interfaces, connection is only removed from topology, despite having real connectivity or not.
	 * 
	 * @param link
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	@Path("/l2detach")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void l2detach(Link link) throws CapabilityException;

	/**
	 * Removes a L2 connection between given interfaces in network topology.
	 * 
	 * This method does not interact with real interfaces, connection is only removed from topology, despite having real connectivity or not.
	 * 
	 * @param interface1
	 * @param interface2
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	public void l2detach(Interface interface1, Interface interface2) throws CapabilityException;

}
