package org.opennaas.extensions.contentprovisioning.mediaencoder.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.extensions.contentprovisioning.mediaencoder.api.messages.Reset;
import org.opennaas.extensions.contentprovisioning.mediaencoder.api.messages.Start;
import org.opennaas.extensions.contentprovisioning.mediaencoder.api.messages.Stop;

/**
 * Basic REST API interface for Elemental Live (product information <a href="https://www.elementaltechnologies.com/products/elemental-live">here</a>)
 * 
 * <p>
 * It allows starting, stopping and resetting predefined encoding jobs.
 * </p>
 * 
 * @author Julio Carlos Barrera
 * 
 */
@Path("/live_events")
public interface IMediaEncoder {

	/**
	 * Start encoding job
	 * 
	 * @param jobId
	 *            encoding job ID
	 * @return
	 */
	@POST
	@Path("/{jobId}/start")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String start(@PathParam("jobId") int jobId, Start start);

	/**
	 * Stop encoding job
	 * 
	 * @param jobId
	 *            encoding job ID
	 * @return
	 */
	@POST
	@Path("/{jobId}/stop")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String stop(@PathParam("jobId") int jobId, Stop stop);

	/**
	 * Reset encoding job
	 * 
	 * @param jobId
	 *            encoding job ID
	 * @return
	 */
	@POST
	@Path("/{jobId}/reset")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String reset(@PathParam("jobId") int jobId, Reset reset);

}
