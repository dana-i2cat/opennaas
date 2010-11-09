package net.i2cat.mantychore.commandsets.junos.digester;

import net.i2cat.mantychore.models.router.PhysicalInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterfaceParser {
	/** logger **/
	Logger			log			= LoggerFactory
													.getLogger(RouterParser.class);

	private String	name		= "";
	private String	unitName	= "";

	public void setName(String name) {
		this.name = name;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public PhysicalInterface getPhysicalInterface() {
		PhysicalInterface physicalInterface = new PhysicalInterface();

		// formating information
		physicalInterface.setLocation(name + "." + unitName);

		return physicalInterface;

	}

}
