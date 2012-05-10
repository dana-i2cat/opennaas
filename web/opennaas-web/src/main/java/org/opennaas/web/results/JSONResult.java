package org.opennaas.web.results;

import java.io.PrintWriter;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.ValueStack;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

/*
 * The JSONResult is a custom result to serlialize an object to the JSON format.  This
 * result type can be used to provide Ajax clients with JSON responses.
 * 
 * This result is basically just to show two things.  First, the fundamentals of making
 * a custom result.  And second, how Ajax applications can be easily integrated with the 
 * framework.  
 * 
 * This result takes a single parameter, classAlias, which will be the JSON object name
 * under which the domain model object will be serialized.  As far as what gets serialized,
 * the JSON result will look for a property on the ValueStack called jsonModel.  This 
 * value could arrive on the ValueStack via a variety of methods.     
 */

public class JSONResult implements Result {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public static final String	DEFAULT_PARAM		= "classAlias";

	String						classAlias;

	public String getClassAlias() {
		return classAlias;
	}

	public void setClassAlias(String classAlias) {
		this.classAlias = classAlias;
	}

	@Override
	public void execute(ActionInvocation invocation) throws Exception {

		ServletActionContext.getResponse().setContentType("text/plain");
		PrintWriter responseStream = ServletActionContext.getResponse()
				.getWriter();

		/* Retrieve Objects to Serialize to JSON from ValueStack */
		ValueStack valueStack = invocation.getStack();
		Object jsonModel = valueStack.findValue("jsonModel");

		XStream xstream = new XStream(new JettisonMappedXmlDriver());

		/*
		 * If there's no parameter passed in, just write the objects under a default name.
		 */
		if (classAlias == null) {
			classAlias = "object";
		}
		xstream.alias(classAlias, jsonModel.getClass());

		/* Write to the response stream */
		responseStream.println(xstream.toXML(jsonModel));

	}

}
