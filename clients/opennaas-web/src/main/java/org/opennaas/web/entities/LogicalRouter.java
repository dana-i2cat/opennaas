package org.opennaas.web.entities;

/**
 * @author Jordi
 */
import java.util.concurrent.atomic.AtomicLong;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LogicalRouter {

	private Long	id;

	@NotNull
	@Size(min = 1, max = 25)
	private String	name;

	@NotNull
	@Size(min = 1, max = 25)
	private String	router;

	@NotNull
	@Size(min = 1, max = 25)
	private String	iface;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the router
	 */
	public String getRouter() {
		return router;
	}

	/**
	 * @param router
	 *            the router to set
	 */
	public void setRouter(String router) {
		this.router = router;
	}

	/**
	 * @return the iface
	 */
	public String getIface() {
		return iface;
	}

	/**
	 * @param iface
	 *            the iface to set
	 */
	public void setIface(String iface) {
		this.iface = iface;
	}

	/**
	 * @return the idsequence
	 */
	public static AtomicLong getIdsequence() {
		return idSequence;
	}

	/**
	 * @return the assignId
	 */
	public Long assignId() {
		this.id = idSequence.incrementAndGet();
		return id;
	}

	/**
	 * 
	 */
	private static final AtomicLong	idSequence	= new AtomicLong();

}
