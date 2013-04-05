package org.opennaas.extensions.vnmapper;

import java.io.Serializable;

public class Location implements Serializable {

	private static final long	serialVersionUID	= 6331959112439501369L;

	private int					row;
	private int					cell;

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCell() {
		return cell;
	}

	public void setCell(int cell) {
		this.cell = cell;
	}

}
