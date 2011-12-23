package org.opennaas.core.resources.shell;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.xml.sax.SAXException;

@Command(scope = "resource", name = "modify", description = "Modify an existing resource, changing its descriptor")
public class ModifyResourceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The name of the existing resource to modify ", required = true, multiValued = false)
	private String	resourceId;
	@Argument(index = 1, name = "path or url", description = "File path or url to new resource descriptor", required = true, multiValued = false)
	private String	filename;

	// @Option(name = "--profile", aliases = { "-p" }, description = "")
	// boolean optionProfile;
	// @Argument(name = "profileName", index = 2, required = false, description = "The profile ID")
	// String profileName = null;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("modify resource");

		IResourceManager manager = getResourceManager();

		ResourceDescriptor descriptor = getDescriptor(filename);

		if (descriptor != null) {

			String[] argsRouterName = new String[2];
			try {
				argsRouterName = splitResourceName(resourceId);
			} catch (Exception e) {
				printError(e.getMessage());
				printEndCommand();
				return -1;
			}

			IResourceIdentifier resourceIdentifier = null;
			try {
				resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
				if (resourceIdentifier != null) {
					try {
						manager.modifyResource(resourceIdentifier, descriptor);
						printInfo("Resource " + resourceId + " modified.");
					} catch (ResourceException f) {
						printError(f);
					}
				} else {
					printError("Resource " + resourceId + " not found on repository.");
				}
			} catch (ResourceException e) {
				printError(e);
				printError("Cannot modify resource " + resourceId);
			}
		}

		printEndCommand();
		return null;
	}

	// @Override
	// protected Object doExecute() throws Exception {
	//
	// initcommand("modify resource");
	//
	// IResourceManager manager = getResourceManager();
	// ResourceDescriptor descriptor = null;
	//
	// if (!splitResourceName(name))
	// return null;
	//
	// File file = new File(filename);
	// // check if the argument path is a directory
	// // if it is, load all the descriptor files of the directory
	// if (!file.isDirectory() && filename.endsWith(".descriptor")) {
	// // only accept the files with '.decriptor' extension
	// IResourceIdentifier resourceIdentifier = null;
	// try {
	// resourceIdentifier = manager.getIdentifierFromResourceName(args[0], args[1]);
	// if (resourceIdentifier != null) {
	// try {
	// descriptor = getResourceDescriptor(filename);
	//
	// manager.modifyResource(resourceIdentifier, descriptor);
	// printInfo("Resource " + args[1] + " modified.");
	// } catch (FileNotFoundException f) {
	// printError("File not found: " + filename);
	// // printError(f);
	// } catch (JAXBException f) {
	// printError("Error parsing descriptor ");
	// printError(f);
	// } catch (ResourceException f) {
	// printError(f);
	// }
	//
	// } else {
	// printError("The resource " + args[1] + " is not found on repository.");
	// }
	// } catch (ResourceException e) {
	// printError(e);
	// printError("No modified " + args[1]);
	// }
	// } else {
	// printError("The file cannot be a directory");
	// }
	// endcommand();
	// return null;
	// }

	public ResourceDescriptor getResourceDescriptor(String filename) throws JAXBException, IOException, ResourceException, SAXException {
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

	public ResourceDescriptor getDescriptor(InputStream stream) throws JAXBException, SAXException {

		ResourceDescriptor descriptor = null;
		try {
			JAXBContext context = JAXBContext.newInstance(ResourceDescriptor.class);

			Unmarshaller unmarshaller = context.createUnmarshaller();

			/* check wellformat xml with xsd */
			// TODO I CAN NOT UNDERSTAND WHY WE CAN GET THE LOADER FROM A COMMAND
			// SchemaFactory sf = SchemaFactory.newInstance(
			// javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			// ClassLoader loader = Thread.currentThread().getContextClassLoader();
			// Schema schema = sf.newSchema(new StreamSource(loader.getResourceAsStream(NAME_SCHEMA)));
			// unmarshaller.setSchema(schema);

			descriptor = (ResourceDescriptor) unmarshaller.unmarshal(stream);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// Ingore
			}
		}
		return descriptor;
	}

	private ResourceDescriptor getDescriptor(String path) {

		ResourceDescriptor descriptor = null;

		URL url = getDescriptorURL(path);

		if (url == null)
			return null;

		try {

			descriptor = getResourceDescriptor(url.toString());

		} catch (FileNotFoundException f) {
			printError("File not found: " + url.toString());
		} catch (NullPointerException f) {
			printError("Error parsing descriptor on " + url.toString());
		} catch (JAXBException f) {
			printError("Error parsing descriptor ");
			printError(f);
		} catch (ResourceException f) {
			printError("In file: " + url.toString());
			printError(f);
		} catch (IOException e) {
			printError("Error reading descriptor: " + url.toString(), e);
		} catch (SAXException f) {
			printError("Given file is not a valid descriptor. Check it complies with descriptor schema. Invalid file: " + url.toString());
			printError(f);
		}

		return descriptor;
	}

	private URL getDescriptorURL(String path) {

		URL url = null;
		try {
			url = fileNameToUrl(path);
		} catch (MalformedURLException e1) {
			printError("Could not read file. Malformed path: " + path);
		}
		return url;
	}

	private URL fileNameToUrl(String pathOrUrl) throws MalformedURLException {

		String url;

		if (!pathOrUrl.contains("://")) {
			url = "file:///" + pathOrUrl;
		} else {
			url = pathOrUrl;
		}

		return new URL(url);
	}

}
