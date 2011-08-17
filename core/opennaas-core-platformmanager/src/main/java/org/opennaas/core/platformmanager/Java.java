package org.opennaas.core.platformmanager;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Provides information about the java virtual machine is executing. It has JAXB annotations 
 * to provide XML marshalling capabilities
 * @author eduardgrasa
 *
 */
public class Java {
	private String jreVersion = null;
	private String jreVendor = null;
	private String jvmName = null;
	private String jvmVersion = null;
	private String jvmVendor = null;
	private Memory nonHeapMemory = null;
	private Memory heapMemory = null;
	
	public Java(){
	}

	@XmlAttribute(name = "jreVersion")
	public String getJreVersion() {
		return jreVersion;
	}

	public void setJreVersion(String jreVersion) {
		this.jreVersion = jreVersion;
	}

	@XmlAttribute(name = "jreVendor")
	public String getJreVendor() {
		return jreVendor;
	}

	public void setJreVendor(String jreVendor) {
		this.jreVendor = jreVendor;
	}

	@XmlAttribute(name = "jvmName")
	public String getJvmName() {
		return jvmName;
	}

	public void setJvmName(String jvmName) {
		this.jvmName = jvmName;
	}

	@XmlAttribute(name = "jvmVersion")
	public String getJvmVersion() {
		return jvmVersion;
	}

	public void setJvmVersion(String jvmVersion) {
		this.jvmVersion = jvmVersion;
	}

	@XmlAttribute(name = "jvmVendor")
	public String getJvmVendor() {
		return jvmVendor;
	}

	public void setJvmVendor(String jvmVendor) {
		this.jvmVendor = jvmVendor;
	}

	@XmlElement(name = "nonHeapMemory")
	public Memory getNonHeapMemory() {
		return nonHeapMemory;
	}

	public void setNonHeapMemory(Memory nonHeapMemory) {
		this.nonHeapMemory = nonHeapMemory;
	}

	@XmlElement(name = "heapMemory")
	public Memory getHeapMemory() {
		return heapMemory;
	}

	public void setHeapMemory(Memory heapMemory) {
		this.heapMemory = heapMemory;
	}
	
	public String toString(){
		String result = Platform.BOLD + "Java Virtual Machine Information \n" + Platform.NORMAL;
		result = result + "   JRE Version: " + getJreVersion() + "\n";
		result = result + "   JRE Vendor: " + getJreVendor() + "\n";
		result = result + "   JVM Name: " + getJvmName() + "\n";
		result = result + "   JVM Version: " + getJvmVersion() + "\n";
		result = result + "   JVM Vendor: " + getJvmVendor() + "\n";
		result = result + "   Non heap memory (in MB): \n" + nonHeapMemory.toString();
		result = result + "   Heap memory (in MB): \n" + heapMemory.toString();
		
		return result;
	}
}
