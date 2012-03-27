package org.opennaas.core.persistence;
/**
 *
 * @author Valery Abu-Eid (Dynamic Java.org)
 */
public class PersistenceException extends RuntimeException {
	private String persistenceUnit;

	public PersistenceException() {
		super();
	}

	public PersistenceException(String message, Throwable cause) {
		super(message, cause);
	}

	public PersistenceException(String message) {
		super(message);
	}

	public PersistenceException(Throwable cause) {
		super(cause);
	}

	public String getPersistenceUnit() {
		return persistenceUnit;
	}

	public void setPersistenceUnit(String persistenceUnit) {
		this.persistenceUnit = persistenceUnit;
	}
	private static final long serialVersionUID = ("urn:" + PersistenceException.class.getName()).hashCode();

}
