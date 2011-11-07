package org.opennaas.core.resources;

/**
 * This interface is used to implement the bootstrapping for a resource.
 * 
 * @author Scott Campbell (CRC)
 * 
 */
public interface IResourceBootstrapper {

	/**
	 * calls an operation on the first capability in the execution workflow to initialize the resource. This is usually a Query action.
	 * 
	 * @throws ResourceException
	 */
	public void bootstrap(IResource resource) throws ResourceException;

	/**
	 * Reverts bootstrap operation, leaving given resource in the same state as if bootstrap had never been called.
	 * 
	 * @param resource
	 * @throws ResourceException
	 */
	public void revertBootstrap(IResource resource) throws ResourceException;
	
	public void resetModel (IResource resource) throws ResourceException;

}
