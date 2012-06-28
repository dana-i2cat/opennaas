/**
 * 
 */
package org.opennaas.core.resources;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Jordi
 * 
 */
@XmlRootElement(name = "Test")
public class Test implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * 
	 */
	public Test() {
	}

	private String	A;
	private String	B;

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

	/**
	 * @return the b
	 */
	public String getB() {
		return B;
	}

	/**
	 * @param b
	 *            the b to set
	 */
	public void setB(String b) {
		B = b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((A == null) ? 0 : A.hashCode());
		result = prime * result + ((B == null) ? 0 : B.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Test other = (Test) obj;
		if (A == null) {
			if (other.A != null)
				return false;
		} else if (!A.equals(other.A))
			return false;
		if (B == null) {
			if (other.B != null)
				return false;
		} else if (!B.equals(other.B))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Test [A=" + A + ", B=" + B + "]";
	}

}
