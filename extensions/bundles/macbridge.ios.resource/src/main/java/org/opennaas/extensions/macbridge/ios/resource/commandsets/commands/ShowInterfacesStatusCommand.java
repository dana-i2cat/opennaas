package org.opennaas.extensions.macbridge.ios.resource.commandsets.commands;

import org.opennaas.core.resources.command.CommandException;
import org.opennaas.extensions.capability.macbridge.model.MACBridge;
import org.opennaas.extensions.protocols.cli.message.CLIResponseMessage;
import org.opennaas.extensions.router.model.EthernetPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShowInterfacesStatusCommand extends IOSCommand {
	
	/** CLI Session Log */
    static private Logger logger = LoggerFactory.getLogger(ShowInterfacesStatusCommand.class);

	/**
	 * The widths of each column in the command output. Note the last column size is not specified
	 * because it will use the rest of the string
	 */
	private final int[] COLUMNWIDTHS = { 10, 19, 13, 11, 8, 6 };

	/** Constant value to specify Gigabit Ethernet Card */
	private final String GI = "Gi";

	/** Constant value to specify Fast Ethernet Card */
	private final String FA = "Fa";
	
	public ShowInterfacesStatusCommand() {
	}

	@Override
	public void initialize() throws CommandException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCommand() {
		return "show interfaces status";
	}
	
	@Override
	public void updateModel(CLIResponseMessage responseMessage, MACBridge model) {
		model.removeAllLogicalDeviceByType(EthernetPort.class);
		EthernetPort ethernetPort = null;

		// parse the data to fill the model
		String[] lines = responseMessage.getRawMessage().split("\r\n|\r|\n");
		
		for (int i = 0; i < lines.length; i++) {
			String[] blocks = new String[COLUMNWIDTHS.length + 1];
			try {
				// split each line by their column widths
				int start = 0;
				for (int j = 0; j < COLUMNWIDTHS.length; j++) {
					blocks[j] = lines[i].substring(start, start + COLUMNWIDTHS[j]);
					start += COLUMNWIDTHS[j];
					// logger.debug("Block " + j + " is: " + blocks[j]);
				}
				// use the rest of the line to get the final block
				blocks[blocks.length - 1] = lines[i].substring(start);
				// logger.debug("Block " + blocks.length + " is: " + blocks[blocks.length-1]);
			}catch (StringIndexOutOfBoundsException e) {
				// Ignore lines that don't have the correct number of columns
				//logger.debug("Skipped line: " + lines[i]);
				continue;
			}

			// Create the Model
			ethernetPort = new EthernetPort();
			// get Card type
			if (blocks[0].startsWith(GI)) {
				ethernetPort.setSpeed(1000);
				//TODO set type gigabitEthernet;
			}else if (blocks[0].startsWith(FA)) {
				ethernetPort.setSpeed(100);
				//TODO set type fastEthernet;
			}else {
				//logger.debug("Unrecognized card: " + lines[i]);
				continue;
			}

			try {
				ethernetPort.setName(blocks[0]);
				ethernetPort.setDescription(blocks[6].trim());
				
				// look for the vlan trunk flag
				if (blocks[3].trim().equals("trunk")) {
					if (blocks[3].indexOf("trunk") != -1){
						ethernetPort.setOtherPortType("trunk");
					}else{
						ethernetPort.setOtherPortType("access");
					}
				}
			}catch (Exception e) {
				// Skip the card if it can't be parsed
				logger.debug("Error parsing line: " + lines[i]);
				continue;
			}
			
			model.addLogicalDevice(ethernetPort);
		}
	}
}
