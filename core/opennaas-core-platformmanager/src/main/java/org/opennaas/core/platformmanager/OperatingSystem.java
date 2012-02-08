package org.opennaas.core.platformmanager;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Provides information about the operating system where the JVM is executing. It has JAXB annotations
 * to provide XML marshalling capabilities
 * @author eduardgrasa
 *
 */
public class OperatingSystem {

	private String name = null;
	private String version = null;
	private String architecture = null;

	public OperatingSystem(){
	}

	public OperatingSystem(String name, String version, String architecture){
		this.name = name;
		this.version = version;
		this.architecture = architecture;
	}

	@XmlAttribute(name = "name")
	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	@XmlAttribute(name = "version")
	public String getVersion(){
		return version;
	}

	public void setVersion(String version){
		this.version = version;
	}

	@XmlAttribute(name = "architecture")
	public String getArchitecture(){
		return architecture;
	}

	public void setArchitecture(String architecture){
		this.architecture = architecture;
	}

	public String toString(){
		String result = Platform.BOLD + "Operating System \n" + Platform.NORMAL;
		result = result + "   Name: " + getName() + "\n";
		result = result + "   Version: " + getVersion() + "\n";
		result = result + "   Architecture: " + getArchitecture() + "\n";
		return result;
	}
}
