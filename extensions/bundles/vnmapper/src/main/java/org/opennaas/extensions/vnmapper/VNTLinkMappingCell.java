package org.opennaas.extensions.vnmapper;

/// this class and VNTNodeMappingCell one are used to store information during the mapping process

public class VNTLinkMappingCell {

	private int		isConnected;
	private int		isMapped;
	private Path	resultPath;

	public VNTLinkMappingCell()
	{
		isConnected = 0;
		isMapped = 0;
		resultPath = new Path();
	}

	public int getIsConnected() {
		return isConnected;
	}

	public void setIsConnected(int isConnected) {
		this.isConnected = isConnected;
	}

	public int getIsMapped() {
		return isMapped;
	}

	public void setIsMapped(int isMapped) {
		this.isMapped = isMapped;
	}

	public Path getResultPath() {
		return resultPath;
	}

	public void setResultPath(Path resultPath) {
		this.resultPath = resultPath;
	}
}
