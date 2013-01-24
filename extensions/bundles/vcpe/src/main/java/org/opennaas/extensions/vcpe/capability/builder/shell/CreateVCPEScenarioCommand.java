package org.opennaas.extensions.vcpe.capability.builder.shell;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.vcpe.capability.builder.IVCPENetworkBuilderCapability;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

@Command(scope = "vcpenet", name = "create", description = "Create a vCPE network scenario from given model")
public class CreateVCPEScenarioCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "vCPENetwork resource friendly name.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "path_to_model_file", description = "Path to file containing the model in XML", required = true, multiValued = false)
	private String	modelPath;

	@Override
	protected Object doExecute() throws Exception {

		IResource resource = getResourceFromFriendlyName(resourceId);

		String xml = fileToString(modelPath);
		VCPENetworkModel desiredScenario = (VCPENetworkModel) ObjectSerializer.fromXml(xml, VCPENetworkModel.class);

		IVCPENetworkBuilderCapability capability = (IVCPENetworkBuilderCapability) resource.getCapabilityByInterface(IVCPENetworkBuilderCapability.class);
		capability.buildVCPENetwork(desiredScenario);

		return null;
	}

	/**
	 * Description of the Method
	 * 
	 * @param file
	 *            The file to be turned into a String
	 * @return The file as String encoded in the platform default encoding
	 * @throws IOException
	 */
	private static String fileToString(String filePath) throws IOException {
		String result = null;
		DataInputStream in = null;

		try {
			File f = new File(filePath);
			byte[] buffer = new byte[(int) f.length()];
			in = new DataInputStream(new FileInputStream(f));
			in.readFully(buffer);
			result = new String(buffer);
		} finally {
			try {
				in.close();
			} catch (IOException e) { /* ignore it */
			}
		}
		return result;
	}

}
