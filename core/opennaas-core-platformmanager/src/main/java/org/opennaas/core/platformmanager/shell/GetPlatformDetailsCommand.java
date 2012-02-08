package org.opennaas.core.platformmanager.shell;


import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.opennaas.core.platformmanager.Platform;

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
