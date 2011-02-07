package net.i2cat.mantychore.core.platformmanager;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * The manager of an IaaS Framework platform. Provides information about the platform where the IaaS Framework
 * is executing.
 * @author eduardgrasa
 *
 */
@WebService
public interface IPlatformManagerWS {

	/**
	 * Returns information about the platform where the IaaS Framework is executing.
	 * @return
	 */
	@WebMethod
	public Platform getPlatformData();
}
