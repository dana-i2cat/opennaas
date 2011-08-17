package org.opennaas.core.resources.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceManager;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.xml.sax.SAXException;

/**
 * Create a new resource from the URL or file given on the karaf shell
 * 
 * @author Scott Campbell (CRC)
 * 
 */
@Command(scope = "resource", name = "create", description = "Create one or more resources from a given descriptor")
public class CreateResourceCommand extends GenericKarafCommand {
	private final String	NAME_SCHEMA	= "/descriptor.xsd";

	@Argument(index = 0, name = "paths or urls", description = "A space delimited list of file paths or urls to resource descriptors ", required = true, multiValued = true)
	private List<String>	paths;
	@Option(name = "--profile", aliases = { "-p" }, description = "Allows explicit declaration of profile to be used")
	String					profileName;

	@Override
	protected Object doExecute() throws Exception {

		initcommand("create resource");

		IResourceManager manager = getResourceManager();

		List<ResourceDescriptor> descriptors = getDescriptors(paths);

		for (ResourceDescriptor descriptor : descriptors) {
			try {
				totalFiles++;
				createResource(manager, descriptor);
				printSymbol(underLine);
			} catch (NullPointerException f) {
				printError("Error creating Resource " + descriptor.getInformation().getType() + ":" + descriptor.getInformation().getName());
				printError(f);
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

	// @Override
	// protected Object doExecute() throws Exception {
	//
	// initcommand("create resource");
	//
	// Boolean created = false;
	// IResourceManager manager = getResourceManager();
	// ResourceDescriptor descriptor = null;
	//
	// // For each argument path or URL
	// for (String filename : paths) {
	//
	// File file = new File(filename);
	// // check if the argument path is a directory
	// // if it is, load all the descriptor files of the directory
	// if (file.isDirectory()) {
	// for (File files : file.listFiles()) {
	// // only accept the files with '.descriptor' extension
	// if (files.getName().endsWith(".descriptor")) {
	// totalFiles++;
	// try {
	// descriptor = getResourceDescriptor(files.getPath());
	// try {
	// createResource(manager, descriptor);
	// } catch (NullPointerException f) {
	// printError("Error creating Resource.");
	// printError(f);
	// }
	// } catch (FileNotFoundException f) {
	// printError("File not found: " + files);
	//
	// } catch (NullPointerException f) {
	// printError("Error parsing descriptor on " + files.getName());
	//
	// } catch (JAXBException f) {
	// printError("Error parsing descriptor ");
	// printError(f);
	// } catch (ResourceException f) {
	// printError("In file: " + files.getName());
	// printError(f);
	// }
	//
	// }
	// printSymbol(underLine);
	// }
	// } else {
	// if (filename.endsWith(".descriptor")) {
	// totalFiles++;
	// try {
	// descriptor = getResourceDescriptor(filename);
	// try {
	// createResource(manager, descriptor);
	// } catch (NullPointerException f) {
	// printError("Error creating Resource. ");
	// printError(f);
	// }
	// } catch (JAXBException f) {
	// printError("Error parsing descriptor ");
	// printError(f);
	// } catch (FileNotFoundException f) {
	// printError("File not found: " + filename);
	//
	// } catch (NullPointerException f) {
	// printError("The descriptor is not loaded " + filename);
	//
	// } catch (ResourceException f) {
	// printError("File: " + filename);
	// printError(f);
	// }
	// } else {
	// printError("The file type is not a valid for " + filename);
	// }
	// printSymbol(underLine);
	// }
	//
	// }
	// if (counter == 0) {
	// printInfo("No resource has been created.");
	//
	// } else {
	// printInfo("Created " + counter + " resource/s from " + totalFiles);
	// }
	// endcommand();
	// return null;
	// }

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

	// public IResourceManager getResourceManager() throws Exception {
	// IResourceManager resourceManager = Activator.getResourceManagerService();
	//
	// return resourceManager;
	// }

	private List<ResourceDescriptor> getDescriptors(List<String> paths) {

		List<URL> urls = new ArrayList<URL>();
		List<ResourceDescriptor> descriptors = new ArrayList<ResourceDescriptor>();

		for (String path : paths) {
			urls.addAll(getDescriptorURLs(path));
		}

		for (URL url : urls) {
			try {

				descriptors.add(getResourceDescriptor(url.toString()));

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
		}

		return descriptors;
	}

	private List<URL> getDescriptorURLs(String path) {

		List<URL> urls = new ArrayList<URL>();

		try {
			URL url = fileNameToUrl(path);

			if (url.getProtocol().equals("file")) {
				File file = new File(url.toURI());

				if (file.isDirectory()) {

					for (File fileInDirectory : file.listFiles()) {

						if (fileInDirectory.getName().endsWith(".descriptor")) {
							try {
								urls.add(fileInDirectory.toURI().toURL());
							} catch (MalformedURLException e) {
								printError("Could not read file. Malformed path: " + fileInDirectory.toURI().toString());
							}
						}
					}
				} else {
					if (file.getName().endsWith(".descriptor")) {
						urls.add(url);
					} else {
						printError("The file type is not a valid for " + file.getName());
					}
				}
			} else {
				urls.add(url);
			}

		} catch (MalformedURLException e1) {
			printError("Could not read file. Malformed path: " + path);
		} catch (URISyntaxException e) {
			printError("Could not read file. Malformed path: " + path);
		}

		return urls;
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
