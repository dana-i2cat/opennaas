package net.i2cat.mantychore.commandsets.junos.digester;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.digester.CallMethodRule;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSet;
import org.xml.sax.SAXException;

public class DigesterEngine extends Digester {

	protected HashMap<String, Object>	mapElements;

	protected RuleSet					ruleSet;

	public void init() {
		push(this);
		mapElements = new HashMap<String, Object>();
	}

	/**
	 * Adds a CallMethodRule for a method in the object at the bottom of the stack.
	 * 
	 * @param pathRule
	 *            Element matching pattern, telling when this rule will be fired
	 * @param nameMethod
	 *            name of the method (in the object at the bottom of the stack) to be called.
	 * @param numParam
	 *            number of parameters this method requires -1. 0 means only the content of pathRule is required and passed. -1 means method accepts
	 *            no parameters.
	 */
	public void addMyRule(String pathRule, String nameMethod, int numParam) {
		// -1 specifies the position in the stack, in our case, in the bottom.
		if (numParam >= 0) {
			addRule(pathRule, new CallMethodRule(-1, nameMethod, numParam + 1));
			addCallParam(pathRule, 0);
		} else {
			addRule(pathRule, new CallMethodRule(-1, nameMethod));
		}
	}

	public void configurableParse(InputStream input) throws IOException, SAXException {

		this.addRuleSet(ruleSet);
		super.parse(input);

	}

	public void configurableParse(String uri) throws IOException, SAXException {

		this.addRuleSet(ruleSet);
		super.parse(new File(uri));

	}

	public HashMap<String, Object> getMapElements() {
		return mapElements;
	}

	public void setMapElements(HashMap<String, Object> mapElements) {
		this.mapElements = mapElements;
	}

}
