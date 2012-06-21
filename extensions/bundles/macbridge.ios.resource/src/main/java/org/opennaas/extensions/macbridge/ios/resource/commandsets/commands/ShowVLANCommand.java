package org.opennaas.extensions.macbridge.ios.resource.commandsets.commands;

import org.opennaas.core.resources.command.CommandException;
import org.opennaas.extensions.capability.macbridge.model.MACBridge;
import org.opennaas.extensions.capability.macbridge.model.PortConfiguration;
import org.opennaas.extensions.capability.macbridge.model.StaticVLANRegistrationEntry;
import org.opennaas.extensions.capability.macbridge.model.VLANConfiguration;
import org.opennaas.extensions.protocols.cli.message.CLIResponseMessage;
import org.opennaas.extensions.router.model.EthernetPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ShowVLANCommand extends IOSCommand {
	
	/** CLI Session Log */
    static private Logger logger = LoggerFactory.getLogger(ShowVLANCommand.class);

	/**
	 * The widths of each column in the command output. Note the last column size is not specified
	 * because it will use the rest of the string
	 */
	private final int[] COLUMNWIDTHS = { 5, 33, 10};

	/** Constant value to specify Gigabit Ethernet Card */
	private final String GI = "Gi";

	/** Constant value to specify Fast Ethernet Card */
	private final String FA = "Fa";
	
	public ShowVLANCommand() {
	}

	@Override
	public void initialize() throws CommandException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCommand() {
		return "show vlan";
	}
	
	public static void main(String[] args){
		String test = "  Gi1/0/5, Gi1/0/7, Gi1/0/8";
		System.out.println(test.trim());
		String[] aux = test.trim().split(",");
		System.out.println(aux.length);
		for(int i=0; i<aux.length; i++){
			System.out.println(aux[i].trim());
		}
	}
	
	@Override
	public void updateModel(CLIResponseMessage responseMessage, MACBridge model) {
		model.getVLANDatabase().clear();
		model.getFilteringDatabase().getStaticVLANRegistrations().clear();
		VLANConfiguration vlanConfiguration = null;
		StaticVLANRegistrationEntry entry = null;
		PortConfiguration portConfiguration = null;
		EthernetPort ethernetPort = null;

		// parse the data to fill the model
		String[] lines = responseMessage.getRawMessage().split("\r\n|\r|\n");
		
		for (int i = 4; i < lines.length; i++) {
			if (lines[i+1].indexOf("VLAN") != -1 && 
				lines[i+1].indexOf("Type") != -1 &&
				lines[i+1].indexOf("SAID") != -1 && 
				lines[i+1].indexOf("MTU") != -1 &&
				lines[i+1].indexOf("Parent") != -1){
				break;
			}
			
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
			
			try{
				vlanConfiguration = new VLANConfiguration(); 
				vlanConfiguration.setVlanID(new Integer(blocks[0].trim()).intValue());
				vlanConfiguration.setName(blocks[1].trim());
				model.getVLANDatabase().put(new Integer(vlanConfiguration.getVlanID()), vlanConfiguration);
				
				if (blocks[3].indexOf(",") != -1 || blocks[3].indexOf("/")!=-1){
					entry = new StaticVLANRegistrationEntry();
					entry.setVlanID(vlanConfiguration.getVlanID());
					String[] aux = blocks[3].trim().split(",");
					for(int j=0; j<aux.length; j++){
						portConfiguration = new PortConfiguration();
						portConfiguration.setPortInterfaceId(aux[j].trim());
						ethernetPort = model.getPort(portConfiguration.getPortInterfaceId());
						if (ethernetPort != null){
							if (ethernetPort.getOtherPortType().equals("trunk")){
								portConfiguration.setTagged(true);
							}else{
								portConfiguration.setTagged(false);
							}
						}
						entry.getPortConfigurations().add(portConfiguration);
					}
					while(i+1<lines.length && lines[i+1].startsWith(" ")){
						i++;
						aux = lines[i].trim().split(",");
						for(int j=0; j<aux.length; j++){
							portConfiguration = new PortConfiguration();
							portConfiguration.setPortInterfaceId(aux[j].trim());
							entry.getPortConfigurations().add(portConfiguration);
						}
					}
					
					model.getFilteringDatabase().getStaticVLANRegistrations().put(new Integer(entry.getVlanID()), entry);
				}
			}catch(Exception ex){
				continue;
			}
		}
	}
}
