package org.opennaas.web.actions;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class Step2Action extends ActionSupport {

	/**
	 * Create resources<br>
	 * <br>
	 * resource:create /home/adrian/heanetM20.descriptor<br>
	 * protocols:context router:heanetM20 netconf ssh://user:password@hea.net:22/netconf<br>
	 * resource:start router:heanetM20<br>
	 * 
	 * ##Creating GSN resource resource:create /home/adrian/gsnMX10.descriptor<br>
	 * protocols:context router:gsnMX10 netconf ssh://user:password@gsn.hea.net:22/netconf<br>
	 * resource:start router:gsnMX10<br>
	 * 
	 * ##Creating UNI-C resource resource:create /home/adrian/unicM7i.descriptor<br>
	 * protocols:context router:unicM7i netconf ssh://user:password@unic.hea.net:22/netconf<br>
	 * resource:start router:unicM7i<br>
	 * 
	 * ##Create demo network resource (with empty topology) resource:create /home/adrian/network.descriptor<br>
	 * resource:start network:networkdemo<br>
	 */
	private static final long	serialVersionUID	= 1L;

	@Override
	public String execute() throws Exception {
		createResources();
		return SUCCESS;
	}

	/**
	 * 
	 */
	private void createResources() {
		// TODO Auto-generated method stub

	}
}
