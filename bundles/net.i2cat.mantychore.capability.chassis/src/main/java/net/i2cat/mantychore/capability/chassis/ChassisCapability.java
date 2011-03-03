package net.i2cat.mantychore.capability.chassis;

import java.util.List;

import net.i2cat.mantychore.actionsets.junos.JunosActionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChassisCapability {

	Logger					log			= LoggerFactory
												.getLogger(ChassisCapability.class);

	private String			resourceId	= "";
	private List<String>	actionNames	= null;

	public ChassisCapability(String resourceId) {
		this.resourceId = resourceId;
	}

	public void handleMessage(String message) {

	}

	public void activeActionSets() {
		if (resourceId == "junos") {
			JunosActionFactory jaf = new JunosActionFactory();
			this.actionNames = jaf.getActionNames();

		} else {

		}
	}

}
