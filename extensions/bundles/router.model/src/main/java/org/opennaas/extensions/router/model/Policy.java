package org.opennaas.extensions.router.model;

/**
 * An abstract class defining the common properties of the policy managed elements derived from CIM_Policy. The subclasses are used to create rules
 * and groups of rules that work together to form a coherent set of policies within an administrative domain or set of domains.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public abstract class Policy extends ManagedElement {

	/**
	 * Default constructor
	 */
	public Policy() {
	}

	/**
	 * A user-friendly name of this policy-related object.
	 */
	private String		commonName;

	/**
	 * An array of keywords for characterizing / categorizing policy objects. Keywords are of one of two types:<br>
	 * <br>
	 * - Keywords defined in this and other MOFs, or in DMTF white papers. These keywords provide a vendor- independent, installation-independent way
	 * of characterizing policy objects.<br>
	 * <br>
	 * - Installation-dependent keywords for characterizing policy objects. Examples include 'Engineering', 'Billing', and 'Review in December 2000'.<br>
	 * <br>
	 * This MOF defines the following keywords: 'UNKNOWN', 'CONFIGURATION', 'USAGE', 'SECURITY', 'SERVICE', 'MOTIVATIONAL', 'INSTALLATION', and
	 * 'EVENT'. These concepts are self-explanatory and are further discussed in the SLA/Policy White Paper. One additional keyword is defined:
	 * 'POLICY'. The role of this keyword is to identify policy-related instances that may not be otherwise identifiable, in some implementations. The
	 * keyword 'POLICY' is NOT mutually exclusive of the other keywords specified above.
	 */
	private String[]	policyKeywords;

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String[] getPolicyKeywords() {
		return policyKeywords;
	}

	public void setPolicyKeywords(String[] policyKeywords) {
		this.policyKeywords = policyKeywords;
	}

}
