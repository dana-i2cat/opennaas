package org.opennaas.extensions.vnmapper;

public class Global {

	private static Global	instance;

	private int				rowNum;
	private int				cellNum;
	private int				servNumMin;
	private int				servNumMax;
	private int				vEnvMax;
	private int				vTypeMax;
	private int				minDay;
	private int				maxDay;
	private int				pNodeChoice;
	private int				pathChoice;		// // SPF: pathChoice=1 / LB: pathChoice=2
	private int				maxPathLinksNum;
	private int				staticNet;
	private int				staticVNT;

	// /
	private int				stepsNum;
	private int				stepsMax;

	private Global() {
		stepsNum = 0;
		stepsMax = 500;
		staticVNT = 0;
		staticNet = 0;
	}

	public static Global getInstance() {
		if (instance == null)
			instance = new Global();

		return instance;
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public int getCellNum() {
		return cellNum;
	}

	public void setCellNum(int cellNum) {
		this.cellNum = cellNum;
	}

	public int getServNumMin() {
		return servNumMin;
	}

	public void setServNumMin(int servNumMin) {
		this.servNumMin = servNumMin;
	}

	public int getServNumMax() {
		return servNumMax;
	}

	public void setServNumMax(int servNumMax) {
		this.servNumMax = servNumMax;
	}

	public int getvEnvMax() {
		return vEnvMax;
	}

	public void setvEnvMax(int vEnvMax) {
		this.vEnvMax = vEnvMax;
	}

	public int getvTypeMax() {
		return vTypeMax;
	}

	public void setvTypeMax(int vTypeMax) {
		this.vTypeMax = vTypeMax;
	}

	public int getMinDay() {
		return minDay;
	}

	public void setMinDay(int minDay) {
		this.minDay = minDay;
	}

	public int getMaxDay() {
		return maxDay;
	}

	public void setMaxDay(int maxDay) {
		this.maxDay = maxDay;
	}

	public int getpNodeChoice() {
		return pNodeChoice;
	}

	public void setpNodeChoice(int pNodeChoice) {
		this.pNodeChoice = pNodeChoice;
	}

	public int getPathChoice() {
		return pathChoice;
	}

	public void setPathChoice(int pathChoice) {
		this.pathChoice = pathChoice;
	}

	public int getMaxPathLinksNum() {
		return maxPathLinksNum;
	}

	public void setMaxPathLinksNum(int maxPathLinksNum) {
		this.maxPathLinksNum = maxPathLinksNum;
	}

	public int getStaticNet() {
		return staticNet;
	}

	public void setStaticNet(int staticNet) {
		this.staticNet = staticNet;
	}

	public int getStaticVNT() {
		return staticVNT;
	}

	public void setStaticVNT(int staticVNT) {
		this.staticVNT = staticVNT;
	}

	public int getStepsNum() {
		return stepsNum;
	}

	public void setStepsNum(int stepsNum) {
		this.stepsNum = stepsNum;
	}

	public int getStepsMax() {
		return stepsMax;
	}

	public void setStepsMax(int stepsMax) {
		this.stepsMax = stepsMax;
	}

	public void increaseStepsNum(int value) {
		this.stepsNum += value;
	}

}
