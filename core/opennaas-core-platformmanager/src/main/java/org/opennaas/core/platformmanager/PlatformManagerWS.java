package org.opennaas.core.platformmanager;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class PlatformManagerWS implements IPlatformManagerWS{
	
	private Platform platform = null;
	
	public PlatformManagerWS(){
		platform = new Platform();
	}

	@WebMethod
	public Platform getPlatformData() {
		platform.reloadInformation();
		return platform;
	}

}
