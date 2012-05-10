package org.opennaas.web.actions;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class Step4Action extends ActionSupport {

	/**
	 * shell:echo "CREATE LOGICAL ROUTERS"<br>
	 * ##Creating logical routers<br>
	 * chassis:createlogicalrouter router:heanetM20 logicalheanet1 fe-0/3/3.1 fe-0/3/0.13 ge-0/2/0.80 gr-1/2/0.1 <br>
	 * resource:list <br>
	 * queue:listactions router:heanetM20 <br>
	 * queue:execute router:heanetM20 <br>
	 * protocols:context router:logicalheanet1 netconf ssh://user:password@hea.net:22/netconf <br>
	 * resource:start router:logicalheanet1 <br>
	 * 
	 * chassis:createlogicalrouter router:unicM7i logicalunic1 ge-2/0/0.13 ge-2/0/0.12 ge-2/0/1.81 gr-1/1/0.2 <br>
	 * resource:list <br>
	 * queue:listactions router:unicM7i <br>
	 * queue:execute router:unicM7i <br>
	 * protocols:context router:logicalunic1 netconf ssh://user:password@unic.hea.net:22/netconf <br>
	 * resource:start router:logicalunic1 <br>
	 * 
	 * chassis:createlogicalrouter router:gsnMX10 logicalgsn1 ge-1/0/7.60 ge-1/0/7.59 <br>
	 * queue:listactions router:gsnMX10 <br>
	 * queue:execute router:gsnMX10 <br>
	 * protocols:context router:logicalgsn1 netconf ssh://user:password@gsn.hea.net:22/netconf <br>
	 * resource:start router:logicalgsn1 <br>
	 **/
	private static final long	serialVersionUID	= 1L;

	private static String		logicalLola			= "logicalheanet1";
	private static String		logicalMyre			= "logicalmyre1";
	private static String		logicalGSN			= "logicalGSN1";

	@Override
	public String execute() throws Exception {
		createLogicalRouters();
		return SUCCESS;
	}

	private void createLogicalRouters() {

	}
}
