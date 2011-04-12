package net.i2cat.nexus.platformmanager.shell;

import net.i2cat.nexus.platformmanager.Platform;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

/**
 * Get the platform details from the Karaf shell
 * @author Eduard Grasa (i2CAT)
 * 
 */
@Command(scope = "nexus", name = "platform", description = "Get platform details")
public class GetPlatformDetailsCommand extends OsgiCommandSupport {

	private Platform platform = null;
	
	@Override
	protected Object doExecute() throws Exception {
		log.debug("Executing get platform details shell command");
		if (platform == null){
			platform = new Platform();
		}
		platform.reloadInformation();
		System.out.println(platform.toString());
		return null;
	}
}
