package net.i2cat.nexus.resources.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

import net.i2cat.nexus.resources.Activator;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.IResourceRepository;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.ResourceManager;
import net.i2cat.nexus.resources.command.GenericKarafCommand;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;

/**
 * Create a new resource from the URL or file given on the karaf shell
 * 
 * @author Scott Campbell (CRC)
 * 
 */
@Command(scope = "resource", name = "create", description = "Create one or more resources from a given descriptor")
public class CreateResourceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "paths or urls", description = "A space delimited list of file paths or urls to resource descriptors ", required = true, multiValued = true)
	private List<String>	paths;
	@Option(name = "--profile", aliases = { "-p" }, description = "Allows explicit declaration of profile to be used")
	String					profileName;

	@Override
	protected Object doExecute() throws Exception {

		initcommand("create resource");

		Boolean created = false;
		IResourceManager manager = getResourceManager();
		ResourceDescriptor descriptor = null;

		// For each argument path or URL
		for (String filename : paths) {

			File file = new File(filename);
			// check if the argument path is a directory
			// if it is, load all the descriptor files of the directory
			if (file.isDirectory()) {
				for (File files : file.listFiles()) {
					// only accept the files with '.descriptor' extension
					if (files.getName().endsWith(".descriptor")) {
						totalFiles++;
						try {
							descriptor = getResourceDescriptor(files.getPath());
							try {
								createResource(manager, descriptor);
							} catch (NullPointerException f) {
								printError("Error creating Resource.");
								printError(f);
							}
						} catch (FileNotFoundException f) {
							printError("File not found: " + files);

						} catch (NullPointerException f) {
							printError("Error parsing descriptor on " + files.getName());

						} catch (JAXBException f) {
							printError("Error parsing descriptor ");
							printError(f);
						} catch (ResourceException f) {
							printError("In file: " + files.getName());
							printError(f);
						}

					}
					printSymbol(underLine);
				}
			} else {
				if (filename.endsWith(".descriptor")) {
					totalFiles++;
					try {
						descriptor = getResourceDescriptor(filename);
						try {
							createResource(manager, descriptor);
						} catch (NullPointerException f) {
							printError("Error creating Resource. ");
							printError(f);
						}
					} catch (JAXBException f) {
						printError("Error parsing descriptor ");
						printError(f);
					} catch (FileNotFoundException f) {
						printError("File not found: " + filename);

					} catch (NullPointerException f) {
						printError("The descriptor is not loaded " + filename);

					} catch (ResourceException f) {
						printError("File: " + filename);
						printError(f);
					}
				} else {
					printError("The file type is not a valid for " + filename);
				}
				printSymbol(underLine);
			}

		}
		if (counter == 0) {
			printInfo("No resource has been created.");

		} else {
			printInfo("Created " + counter + " resource/s from " + totalFiles);
		}
		endcommand();
		return null;
	}

	public int createResource(IResourceManager manager, ResourceDescriptor descriptor) {

		// check if profile option is active
		if (profileName != null && profileName != "") {
			// Override profile in the descriptor
			descriptor.setProfileId(profileName);
		}
		IResource resource = null;
		try {

			printInfo("Creating Resource ...... ");
			resource = manager.createResource(descriptor);
		} catch (ResourceException e) {

			printError(e.getLocalizedMessage());
			ResourceManager rm = (ResourceManager) manager;
			Hashtable<String, IResourceRepository> rr = (Hashtable<String, IResourceRepository>) rm.getResourceRepositories();
			if (rr.isEmpty()) {
				printError("There aren't any Resource Repositories registered.");
				return -1;
			}
			return -1;
		} catch (NullPointerException e) {
			printError(e);
			return -1;
		}
		printInfo("Resource of type " + resource.getResourceDescriptor().getInformation().getType() + " created with name: "
				+ resource.getResourceDescriptor().getInformation().getName());
		counter++;
		return 0;
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

	public IResourceManager getResourceManager() throws Exception {
		IResourceManager resourceManager = Activator.getResourceManagerService();

		return resourceManager;
	}
}
