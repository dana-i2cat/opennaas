package org.opennaas.extensions.abno.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * ABNO Client interface
 * 
 * @author Julio Carlos Barrera
 *
 */
public interface IABNOClient {

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUnicastLink(@QueryParam("source_region") String srcRegion, @QueryParam("destination_region") String dstRegion,
			@QueryParam("source_mac") String srcMACAddress, @QueryParam("dest_mac") String dstMACAddress,
			@QueryParam("source_interface") String srcInterface, @QueryParam("destination_interface") String dstInterface,
			@QueryParam("operation_type") String operationType, @QueryParam("id_operation") long operationId);

}
