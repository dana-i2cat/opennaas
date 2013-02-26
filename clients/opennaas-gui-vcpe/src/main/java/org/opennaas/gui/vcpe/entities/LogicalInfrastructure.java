/**
 * 
 */
package org.opennaas.gui.vcpe.entities;

import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Jordi
 */
public class LogicalInfrastructure {

	private String	id;
	@NotBlank(message = "{message.error.field.mandatory}")
	private String	name;
	@NotBlank(message = "{message.error.field.mandatory}")
	private String	templateType;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the templateType
	 */
	public String getTemplateType() {
		return templateType;
	}

	/**
	 * @param templateType the templateType to set
	 */
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

}
