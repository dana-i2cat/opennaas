package net.i2cat.mantychore.commandsets.junos.digester;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.digester.CallMethodRule;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

public abstract class DigesterEngine extends Digester {
	protected HashMap<String, Object>	mapElements;

	public abstract void addRules();

	public void init() {
		push(this);
		mapElements = new HashMap<String, Object>();
	}

	public void addMyRule(String pathRule, String nameMethod, int numParam) {
		// -1 specifies the position in the stack, in our case, in the bottom.
		// nameMethod. What it is the name of the method to use of the bottom
		// stack.
		addRule(pathRule, new CallMethodRule(-1, nameMethod, numParam + 1));
		addCallParam(pathRule, 0);
	}

	public void configurableParse(InputStream input) throws IOException, SAXException {

		addRules();
		super.parse(input);

	}

	public void configurableParse(String uri) throws IOException, SAXException {

		addRules();
		super.parse(new File(uri));

	}

	public HashMap<String, Object> getMapElements() {
		return mapElements;
	}

	public void setMapElements(HashMap<String, Object> mapElements) {
		this.mapElements = mapElements;
	}

}
