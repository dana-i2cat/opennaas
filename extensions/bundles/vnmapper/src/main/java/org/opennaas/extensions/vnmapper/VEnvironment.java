package org.opennaas.extensions.vnmapper;

import java.io.Serializable;

public class VEnvironment implements Serializable {

	private static final long	serialVersionUID	= -7632392424323903202L;

	private int					vType;
	private int					vEnv;

	public int getvType() {
		return vType;
	}

	public void setvType(int vType) {
		this.vType = vType;
	}

	public int getvEnv() {
		return vEnv;
	}

	public void setvEnv(int vEnv) {
		this.vEnv = vEnv;
	}

}
