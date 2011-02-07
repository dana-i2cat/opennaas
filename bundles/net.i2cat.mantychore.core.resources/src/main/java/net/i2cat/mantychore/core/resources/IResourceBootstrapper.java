package net.i2cat.mantychore.core.resources;

/**
 * This interface is used to implement the bootstrapping for a resource.
 * 
 * @author Scott Campbell (CRC)
 * 
 */
public interface IResourceBootstrapper {

	/**
	 * calls an operation on the first capability in the execution workflow
	 * to initialize the resource. This is usually a Query action.
	 * 
	 * @throws ResourceException
	 */
	public void bootstrap(IResource resource) throws ResourceException;
}
