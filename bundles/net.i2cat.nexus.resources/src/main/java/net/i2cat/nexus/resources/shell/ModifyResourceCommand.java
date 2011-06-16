package net.i2cat.nexus.resources.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

@Command(scope = "resource", name = "modify", description = "Modify one or more existing resources")
public class ModifyResourceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The name of the existing resource to modify ", required = true, multiValued = false)
	private String	name;
	@Argument(index = 1, name = "path or url", description = "THe file path or url to new resource descriptor", required = true, multiValued = false)
	private String	filename;

	// @Option(name = "--profile", aliases = { "-p" }, description = "")
	// boolean optionProfile;
	// @Argument(name = "profileName", index = 2, required = false, description = "The profile ID")
	// String profileName = null;

	@Override
	protected Object doExecute() throws Exception {

		initcommand("modify resource");

		Boolean created = false;
		IResourceManager manager = getResourceManager();
		ResourceDescriptor descriptor = null;

		if (!splitResourceName(name))
			return null;

		File file = new File(filename);
		// check if the argument path is a directory
		// if it is, load all the descriptor files of the directory
		if (!file.isDirectory() && filename.endsWith(".descriptor")) {
			// only accept the files with '.decriptor' extension
			IResourceIdentifier resourceIdentifier = null;
			try {
				resourceIdentifier = manager.getIdentifierFromResourceName(args[0], args[1]);
				if (resourceIdentifier != null) {
					try {
						descriptor = getResourceDescriptor(filename);

						manager.modifyResource(resourceIdentifier, descriptor);
						printInfo("Resource " + args[1] + " modified.");
					} catch (FileNotFoundException f) {
						printError("File not found: " + filename);
						// printError(f);
					} catch (JAXBException f) {
						printError("Error parsing descriptor ");
						printError(f);
					} catch (ResourceException f) {
						printError(f);
					}

				} else {
					printError("The resource " + args[1] + " is not found on repository.");
				}
			} catch (ResourceException e) {
				printError(e);
				printError("No modified " + args[1]);
			}
		} else {
			printError("The file cannot be a directory");
		}
		endcommand();
		return null;
	}

	public ResourceDescriptor getResourceDescriptor(String filename) throws JAXBException, IOException, ResourceException {
		InputStream stream = null;
		// First try a URL
		try {
			URL url = new URL(filename);
			printInfo("URL: " + url);
			stream = url.openStream();
		} catch (MalformedURLException ignore) {
			// Then try a file
			printInfo("file: " + filename);
			stream = new FileInputStream(filename);
		}
		ResourceDescriptor rd = getDescriptor(stream);

		if (rd.getInformation().getType() == null || rd.getInformation().getType() == "") {
			throw new ResourceException("ResourceDescriptor: Needed to indicate a resource type.");
		}
		if (rd.getInformation().getName().equals("") || rd.getInformation().getName() == null) {
			throw new ResourceException("ResourceDescriptor: The resourceName field cannot be null.");
		}
		printInfo("Descriptor loaded for resource " + rd.getInformation().getName() + " with type: " + rd.getInformation()
				.getType());
		return rd;
	}

	private ResourceDescriptor getDescriptor(InputStream stream) throws JAXBException {
		ResourceDescriptor descriptor = null;
		try {
			JAXBContext context = JAXBContext.newInstance(ResourceDescriptor.class);
			descriptor = (ResourceDescriptor) context.createUnmarshaller().unmarshal(stream);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// Ingore
			}
		}
		return descriptor;
	}

}
