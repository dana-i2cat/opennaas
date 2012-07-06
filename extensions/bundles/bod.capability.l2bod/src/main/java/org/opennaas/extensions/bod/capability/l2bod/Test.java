package org.opennaas.extensions.bod.capability.l2bod;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 */
/**
 * @author Jordi
 * 
 */
@XmlRootElement
public class Test {

	private String	A;

	/**
	 * 
	 */
	public Test() {
	}

	/**
	 * @return the a
	 */
	public String getA() {
		return A;
	}

	/**
	 * @param a
	 *            the a to set
	 */
	public void setA(String a) {
		A = a;
	}

}
