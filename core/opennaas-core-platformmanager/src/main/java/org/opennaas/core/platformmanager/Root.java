package org.opennaas.core.platformmanager;

import javax.xml.bind.annotation.XmlAttribute;

public class Root {
	private String absolutePath = null;
	/** in bytes **/
	private String totalSpace = null;
	private String freeSpace = null;
	private String usableSpace = null;
	
	public Root(){
	}

	@XmlAttribute(name = "totalSpace")
	public String getTotalSpace() {
		return totalSpace;
	}

	public void setTotalSpace(String totalSpace) {
		this.totalSpace = totalSpace;
	}

	@XmlAttribute(name = "freeSpace")
	public String getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(String freeSpace) {
		this.freeSpace = freeSpace;
	}

	@XmlAttribute(name = "usableSpace")
	public String getUsableSpace() {
		return usableSpace;
	}

	public void setUsableSpace(String usableSpace) {
		this.usableSpace = usableSpace;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}
	
	@XmlAttribute(name = "absolutePath")
	public String getAbsolutePath() {
		return absolutePath;
	}
	
	public String toString(){
		String result = "      Root  \n";
		result = result + "         Absolute Path: " + getAbsolutePath() + "\n";
		result = result + "         Total Space (in MB): " + getTotalSpace() + "\n";
		result = result + "         Free Space (in MB): " + getFreeSpace() + "\n";
		result = result + "         Usable Space (in MB): " + getUsableSpace() + "\n";
		return result;
	}
}
