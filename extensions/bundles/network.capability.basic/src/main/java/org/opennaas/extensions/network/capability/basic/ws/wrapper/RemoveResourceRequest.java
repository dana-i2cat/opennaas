package org.opennaas.extensions.network.capability.basic.ws.wrapper;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RemoveResourceRequest {

	private String	resourceId;

	/**
	 * @return the resourceId
	 */
	public String getResourceId() {
		return resourceId;
	}

	/**
	 * @param resourceId
	 *            the resourceId to set
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

}
