package org.opennaas.extensions.roadm.wonesys.commandsets.commands.optr;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.core.resources.command.CommandException;

public class GetOptrExc extends WonesysCommand {

	private static final String	DATA_LENGTH	= "0000";

	private String				chassis;
	private String				slot;

	private String				cardType;

	@Override
	public void parseResponse(Object arg0, Object arg1) throws CommandException {
		// TODO Auto-generated method stub

		// 0xXX 0x00
		// Mode* (Select8)
		// Mode Data**

		// cardType.equals("04");
		// * Mode:
		// 0: 4xLoopback, 1: 1xConv Lambda + 2xLoopback, 2: 2x Conv Lambda, 3: 1x 1+1 Reversible + 1xLoopback, 4: 1x 1+1 No Reversible +
		// 1xLoopback, 5: 1x 1+1 Forzado + 1xLoopback, 6: 1x 2+2
		// **Mode data 0: No data
		// 1: Port X (Select8), Port X' (Select8)
		// 2: Port X (Select8), Port X' (Select8), Port Y (Select8), Port Y' (Select8)
		// 3,4,5: Port Cliente (Select8), Port Linea Principal (Select8), Port Linea Backup (Select8)
		// 6: Port Cliente Principal (Select8), Port Cliente Backup (Select8), Port Linea Principal (Select8), Port Linea Backup (Select8)

		// cardType.equals("05");
		// * Mode: 0: 4xLoopback, 1: 1xConv Lambda + 2xLoopback, 2: 2x Conv Lambda, 3: 1x 1+1 Reversible + 1xLoopback, 4: 1x 1+1 No Reversible +
		// 1xLoopback, 5: 1x 1+1 Forzado + 1xLoopback, 6: 1x 2+2
		// **Mode data 0: Port X (Select8), Port X' (Select8)
		// 1,2,3: Port Cliente (Select8), Port Linea Principal (Select8), Port Linea Backup (Select8)

		// cardType.equals("08");
		// * Mode: 0: 3xLoopback, 1: 1xConv Lambda + 1xLoopback, 2: 1x 1+1 Reversible, 3: 1x 1+1 No Reversible, 4: 1x 1+1 Forzado
		// **Mode data 0: No data
		// 1: Port X (Select8), Port X' (Select8)
		// 2,3,4: Port Cliente (Select8), Port Linea Principal (Select8), Port Linea Backup (Select8)

	}

	@Override
	protected String getWonesysCommandDeviceId() {
		return chassis + slot;
	}

	@Override
	protected String getWonesysCommandId() {
		return cardType + "01";
	}

	@Override
	protected String getWonesysCommandRequiredDataLength() {
		return DATA_LENGTH;
	}

	@Override
	protected String getWonesysCommandData() {
		return "";
	}

}
