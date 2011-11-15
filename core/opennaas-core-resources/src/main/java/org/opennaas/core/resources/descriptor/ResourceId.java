package org.opennaas.core.resources.descriptor;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ResourceId {
	
	@Id
	@GeneratedValue
	private long						id;
	
	@Basic
	private String	type;
	/** The name or alias **/
	@Basic
	private String	name;

	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

}
