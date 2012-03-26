package org.opennaas.core.platformmanager;

import javax.xml.bind.annotation.XmlAttribute;

public class Memory {

	/** in bytes **/
	private String committed = null;
	private String init = null;
	private String max = null;
	private String usage = null;

	public Memory(){
	}

	@XmlAttribute(name = "committed")
	public String getCommitted() {
		return committed;
	}

	public void setCommitted(String committed) {
		this.committed = committed;
	}

	@XmlAttribute(name = "init")
	public String getInit() {
		return init;
	}

	public void setInit(String init) {
		this.init = init;
	}

	@XmlAttribute(name = "max")
	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	@XmlAttribute(name = "usage")
	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String toString(){
		String result = "";
		result = result + "      Committed: " + getCommitted() + "\n";
		result = result + "      Initial: " + getInit() + "\n";
		result = result + "      Maximum: " + getMax() + "\n";
		result = result + "      Usage: " + getUsage() + "\n";
		return result;
	}

}
