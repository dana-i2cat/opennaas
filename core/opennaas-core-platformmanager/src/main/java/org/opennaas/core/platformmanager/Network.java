package org.opennaas.core.platformmanager;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Network {
	
	private List<NetInf> networkInterfaces = null;
	
	public Network(){
	}

	public void setNeworkInterfaces(List<NetInf> networkInterfaces) {
		this.networkInterfaces = networkInterfaces;
	}

	@XmlElement(name = "networkInterfaces")
	public List<NetInf> getNeworkInterfaces() {
		return networkInterfaces;
	}
	
	public String toString(){
		String result = "   Network Interfaces \n";
		if (networkInterfaces != null){
			for(int i=0; i<networkInterfaces.size(); i++){
				result = result + networkInterfaces.get(i).toString();
			}
		}
		
		return result;
	}
	
}
