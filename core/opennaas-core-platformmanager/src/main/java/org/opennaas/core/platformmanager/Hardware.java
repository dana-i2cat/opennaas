package org.opennaas.core.platformmanager;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Hardware {
	
	private String cpus = null;
	private String cpuLoad = null;
	private HardDisk hardDisk = null;
	private Network network = null;
	
	public Hardware(){
	}

	@XmlAttribute(name = "cpus")
	public String getCpus() {
		return cpus;
	}

	public void setCpus(String cpus) {
		this.cpus = cpus;
	}
	
	@XmlAttribute(name = "cpuLoad")
	public String getCpuLoad() {
		return cpuLoad;
	}

	public void setCpuLoad(String cpuLoad) {
		this.cpuLoad = cpuLoad;
	}

	@XmlElement(name = "hardDisk")
	public HardDisk getHardDisk() {
		return hardDisk;
	}

	public void setHardDisk(HardDisk hardDisk) {
		this.hardDisk = hardDisk;
	}
	
	public void setNetwork(Network network) {
		this.network = network;
	}

	@XmlElement(name = "network")
	public Network getNetwork() {
		return network;
	}
	
	public String toString(){
		String result = Platform.BOLD + "Hardware \n" + Platform.NORMAL;
		result = result + "   Number of cpus: " + getCpus() + "\n";
		result = result + "   CPU load: " + getCpuLoad() + "\n";
		result = result + hardDisk.toString() + network.toString();
		return result;
	}
}
