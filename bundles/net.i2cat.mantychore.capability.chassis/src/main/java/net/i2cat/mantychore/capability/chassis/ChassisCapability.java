package net.i2cat.mantychore.capability.chassis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChassisCapability {

	Logger			log			= LoggerFactory
										.getLogger(ChassisCapability.class);

	private String	resourceId	= "";

	public ChassisCapability(String resourceId) {
		this.resourceId = resourceId;
	}

	public void handleMessage(String message) {
		

	}


}
