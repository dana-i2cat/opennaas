package org.opennaas.extensions.vcpe.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlIDREF;

@XmlAccessorType(XmlAccessType.FIELD)
public class LogicalRouter extends Router {

	@XmlIDREF
	protected Router	physicalRouter;

	public Router getPhysicalRouter() {
		return physicalRouter;
	}

	public void setPhysicalRouter(Router physicalRouter) {
		this.physicalRouter = physicalRouter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((interfaces == null) ? 0 : interfaces.hashCode());
		result = prime * result + ((physicalRouter == null) ? 0 : physicalRouter.hashCode());

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogicalRouter other = (LogicalRouter) obj;
		if (interfaces == null) {
			if (other.interfaces != null)
				return false;
		} else if (!interfaces.equals(other.interfaces))
			return false;
		if (physicalRouter == null) {
			if (other.physicalRouter != null)
				return false;
		} else if (!physicalRouter.equals(other.physicalRouter))
			return false;

		return true;
	}

}
