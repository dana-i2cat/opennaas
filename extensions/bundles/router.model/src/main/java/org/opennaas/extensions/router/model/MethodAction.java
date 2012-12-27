package org.opennaas.extensions.router.model;

/**
 * MethodAction is a PolicyAction that MAY invoke methods as defined by a query. If there are no results returned from the query, then no methods are
 * called, otherwise each query result row defines the method to call and its parameters. The called method MAY be either an intrinsic method of a CIM
 * Namespace or an extrinsic method of a CIM_ManagedElement.<br>
 * <br>
 * <br>
 * <br>
 * In order to correlate between methods called by this MethodAction and any other invoked Methods, InstMethodCall indications MAY be created for the
 * method calls that result from this MethodAction. These indications are named by the value in the InstMethodCallName property. These indications
 * MUST be produced if that InstMethodCallName value is included in the FROM clause of the query of some other MethodAction instance within the same
 * PolicyRule. (The details of how this is accomplished are implementation dependent.)<br>
 * <br>
 * <br>
 * <br>
 * The input parameters to the method are defined by the query and MAY be fixed values defined by literals or MAY be defined by reference to one or
 * more properties of classes named in the FROM clause of the query. The referenced objects MAY be those produced by QueryConditions or MethodActions
 * instances associated to the same PolicyRule instance.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class MethodAction extends PolicyAction {

	/**
	 * Default constructor
	 */
	public MethodAction() {
	}

	/**
	 * In the context of the associated PolicyRule, InstMethodCallName defines a unique name for the InstMethodCall instances that result from the
	 * methods invoked as a result of evaluating the Query string. This name MAY be used in subsequent MethodActions of the same PolicyRule to
	 * identify the set of InstMethodCall instances that result from evaluation of this MethodAction in the context of its PolicyRule. This string is
	 * treated as a class name, in a query statement.
	 */
	private String	instMethodCallName;

	/**
	 * A query expression that defines the method to invoke and its input parameters. These are defined by the first and subsequent select-list
	 * entries in the Query string's select-criteria. The FROM clause MAY reference any object, including those named by the QueryResultName and
	 * MethodCallName produced by QueryConditions or MethodActions of the same PolicyRule.<br>
	 * <br>
	 * Note that both intrinsic and extrinsic methods MAY be called. The first select-list entry MUST be an object Path to a method. For consistency
	 * it SHOULD be called MethodName. However, if there is a conflict with existing parameter names, it MAY be called something else. The remaining
	 * select list entries are not positional and MUST use the name of the corresponding method parameter.<br>
	 * <br>
	 * The object path to a method is defined here as a WBEM URI, (see DSP0207) dot concatenated with a method name. It must have the form:
	 * [&lt;wbemURI&gt;, ".",] &lt;MethodName&gt;.<br>
	 * <br>
	 * The named method may be intrinsic or extrinsic. Extrinsics may be at class level (i.e. static) or not.<br>
	 * <br>
	 * The particular instance of this class that is invoking the specified method defines the default namespace, class, and key values. If any of
	 * these are missing from the WBEM URI, these defaults are assumed.<br>
	 * <br>
	 * For intrinsic methods (as defined in the "Specification for CIM Operations over HTTP", see DSP0200), any class name or key values specified in
	 * the WBEM URI are ignored. Similarly, key values are ignored for static methods.<br>
	 * <br>
	 * Intrinsic methods that take an &lt;instancename&gt;, an &lt;objectname&gt;, an &lt;instance&gt;, or a &lt;namedinstance&gt; (as defined in
	 * DSP0200) as an input parameter are preprocessed by the implementation of the MethodAction instance. For each of &lt;instancename&gt; or
	 * &lt;objectname&gt;, the corresponding input parameter name is set to a WBEM URI of the instance or class. /n For each of &lt;instance&gt; or
	 * &lt;namedinstance&gt;, the corresponding input parameter must be set to a WBEM URI to the instance or class. Properties of that instance are
	 * passed as additional select list entries with the name of the corresponding parameter dot appended with the name of the named instance
	 * property.<br>
	 * <br>
	 * For example: if the call is to ModifyInstance, then parameter ModifiedInstance is set to the= &lt;wbemURI&gt; of the instance to modify and for
	 * each relevant property to modify, a parameter is supplied with the name ModifiedInstance.&lt;propertyName&gt; and is set to the new value for
	 * the named property.
	 */
	private String	query;

	/**
	 * The language in which the Query string is expressed./n CQL - refers to the 'DMTF:CQL' language defined by DSP0200.<br>
	 * <br>
	 * CQL: indicates a CIM Query Language string.<br>
	 * <br>
	 * CQLT: indicates a CIM Query Language Template string. When used, the identifiers recognized in the $identifier$ tokens are "SELF" and the
	 * property names of this class, or one of its subclasses. When used in the Query string, $SELF$ will be replaced by a string corresponding to a
	 * WBEM URI referencing the instance of this class that contains the Query template string. Tokens of the form $&lt;propertyname&gt;$ will be
	 * replaced by a string representing the corresponding property value of the instance of this class that contains the Query string.
	 */
	public enum queryLanguage_enum {

		CQL(2),
		CQLT(3);
		/* values .. are "DMTF_Reserved" */
		/* values 0x8000.. are "Vendor_Reserved" */

		private final int	localValue;

		queryLanguage_enum(int enumValue) {
			this.localValue = enumValue;
		}
	}

	private queryLanguage_enum	queryLanguage	= queryLanguage_enum.CQL;

	public String getInstMethodCallName() {
		return instMethodCallName;
	}

	public void setInstMethodCallName(String instMethodCallName) {
		this.instMethodCallName = instMethodCallName;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public queryLanguage_enum getQueryLanguage() {
		return queryLanguage;
	}

	public void setQueryLanguage(queryLanguage_enum queryLanguage) {
		this.queryLanguage = queryLanguage;
	}
}
