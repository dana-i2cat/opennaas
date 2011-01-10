package net.i2cat.mantychore.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Association {

	static Logger	log	= LoggerFactory
								.getLogger(Association.class);

	private ManagedElement	from, to;

	public void setFrom(ManagedElement from) {
		this.from = from;
	}

	public ManagedElement getFrom() {
		return from;
	}

	public void setTo(ManagedElement to) {
		this.to = to;
	}

	public ManagedElement getTo() {
		return to;
	}

	// public static Association link(ManagedElement from, ManagedElement to) {
	// Association assoc = new Association();
	//
	// assoc.setFrom(from);
	// assoc.setTo(to);
	//
	// assoc.getFrom().addToAssociation(assoc);
	// assoc.getTo().addFromAssociation(assoc);
	//
	// return assoc;
	// };

	public static Association link(Class<? extends Association> clazz, ManagedElement from, ManagedElement to) {
		Association assoc = null;

		try {
			assoc = clazz.newInstance();

			assoc.setFrom(from);
			assoc.setTo(to);

			assoc.getFrom().addToAssociation(assoc);
			assoc.getTo().addFromAssociation(assoc);

			return assoc;

		} catch (Exception e) {
			log.error("This error must be impossible to send");
			log.error(e.getMessage());
			return null;
		}
	};

	/**
	 * Remove this association from both ManagedElements. Also, this association
	 * object becomes unusable and must be dropped. Use link() to reestablish
	 * the association.
	 */
	public boolean unlink() {
		boolean ok = true;
		assert this.to != null;
		assert this.from != null;

		ok = this.getTo().removeFromAssociation(this);
		ok = ok && this.getFrom().removeToAssociation(this);

		// Lets free references so we make this association unusable (and GC can
		// do its stuff).
		this.to = null;
		this.from = null;

		return ok;
	}
}
