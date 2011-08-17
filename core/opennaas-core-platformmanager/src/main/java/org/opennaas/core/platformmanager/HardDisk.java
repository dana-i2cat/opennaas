package org.opennaas.core.platformmanager;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class HardDisk {
private List<Root> roots = null;
	
	public HardDisk(){
	}

	public void setRoots(List<Root> roots) {
		this.roots = roots;
	}

	@XmlElement(name = "roots")
	public List<Root> getRoots() {
		return roots;
	}
	
	public String toString(){
		String result = "   Hard Disk \n";
		if (roots != null){
			for(int i=0; i<roots.size(); i++){
				result = result + roots.get(i).toString();
			}
		}
		
		return result;
	}
}
