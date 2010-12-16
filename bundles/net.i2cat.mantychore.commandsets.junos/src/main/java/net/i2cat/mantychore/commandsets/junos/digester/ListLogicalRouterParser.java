package net.i2cat.mantychore.commandsets.junos.digester;

public class ListLogicalRouterParser extends DigesterEngine {

	private static int	id	= 0;

	@Override
	public void addRules() {
		addMyRule("*/logical-systems/name", "setName", 0);
	}

	/* Parser methods */

	public void setName(String logicalName) {
		id++;
		mapElements.put(String.valueOf(id), logicalName);

	}

	public String toPrint() {

		String str = "" + '\n';
		for (String key : mapElements.keySet()) {

			str += "- Logical names " + mapElements.get(key) + '\n';
		}
		return str;
	}
}
