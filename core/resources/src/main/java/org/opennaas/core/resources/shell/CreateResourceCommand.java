package org.opennaas.core.resources.shell;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.network.NetworkTopology;
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
	@Option(name = "--name", aliases = { "-n" }, description = "Override resource name in given descriptor for given one", multiValued = true)
	private List<String>	names;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("create resource");

		IResourceManager manager = getResourceManager();

		int descriptorsCount = paths.size();

		List<ResourceDescriptor> loadedDescriptors = getDescriptors(paths);
		int createdResourcesCounter = 0;
		for (int i = 0; i < loadedDescriptors.size(); i++) {
			ResourceDescriptor descriptor = loadedDescriptors.get(i);

			try {
				// override name for the given one (if present)
				String name = null;
				if (names != null && i < names.size()) {
					name = names.get(i);
					descriptor.getInformation().setName(name);
				}
				int result = createResource(manager, descriptor);
				if (result >= 0)
					createdResourcesCounter++;

			} catch (NullPointerException f) {
				printError("Error creating Resource "
						+ descriptor.getInformation().getType() + ":"
						+ descriptor.getInformation().getName());
				printError(f);
			}
		}

		if (createdResourcesCounter != descriptorsCount) {
			printWarn("Created " + createdResourcesCounter + " resource/s of " + descriptorsCount);
		} else {
			if (createdResourcesCounter == 0) {
				printInfo("No resource has been created.");
			} else {
				printInfo("Created " + createdResourcesCounter + " resource/s of " + descriptorsCount);
			}
		}

		printEndCommand();
		return null;

	}

	public int createResource(IResourceManager manager,
			ResourceDescriptor descriptor) {

		// check if profile option is active
		if (profileName != null && profileName != "") {
			// Override profile in the descriptor
			descriptor.setProfileId(profileName);
		}
		try {
			// printInfo("Creating Resource ...... ");
			IResource resource = manager.createResource(descriptor);
			Information information = resource.getResourceDescriptor()
					.getInformation();
			printInfo("Created resource " + information.getType() + ":"
					+ information.getName());
			return 0;
		} catch (ResourceException e) {
			printError(e.getLocalizedMessage());
			if (manager.getResourceTypes().isEmpty()) {
				printError("There aren't any Resource Repositories registered.");
			}
			return -1;
		} catch (NullPointerException e) {
			printError(e);
			return -1;
		}
	}

	public ResourceDescriptor getResourceDescriptor(String filename)
			throws JAXBException, IOException, ResourceException, SAXException {
		InputStream stream = null;
		// First try a URL
		try {
			URL url = new URL(filename);
			log.info("URL: " + url);
			stream = url.openStream();
		} catch (MalformedURLException ignore) {
			// Then try a file
			log.info("file: " + filename);
			stream = new FileInputStream(filename);
		}

		ResourceDescriptor resourceDescriptor = getDescriptor(stream);

		if (resourceDescriptor.getInformation().getType() == null
				|| resourceDescriptor.getInformation().getType() == "") {
			throw new ResourceException(
					"Invalid ResourceDescriptor: Must specify a resource type.");
		}
		if (resourceDescriptor.getInformation().getName().equals("")
				|| resourceDescriptor.getInformation().getName() == null) {
			throw new ResourceException(
					"Invalid ResourceDescriptor: Must specify a resource name.");
		}

		/* try to load network topology */
		String networkTopologyFilePath = resourceDescriptor.getFileTopology();
		if (networkTopologyFilePath != null
				&& !networkTopologyFilePath.trim().equals("")) {
			/* checks */

			// TODO Improve to get descriptors from relative paths
			if (!networkTopologyFilePath.startsWith("/")) {
				log.info(networkTopologyFilePath + " doesnt start with /");
				throw new ResourceException(
						"The network file decriptor has to be absolute path");
			}

			try {
				printInfo("Loading network file descriptor: "
						+ networkTopologyFilePath);
				NetworkTopology networkTopology = getNetworkDescriptor(networkTopologyFilePath);
				resourceDescriptor.setNetworkTopology(networkTopology);
			} catch (FileNotFoundException e) {
				throw new FileNotFoundException("Could not found file "
						+ networkTopologyFilePath);
			}
		}

		printInfo("Loaded descriptor at " + filename);
		return resourceDescriptor;

	}

	public ResourceDescriptor getDescriptor(InputStream stream)
			throws JAXBException, SAXException {

		ResourceDescriptor descriptor = null;
		try {
			JAXBContext context = JAXBContext
					.newInstance(ResourceDescriptor.class);

			Unmarshaller unmarshaller = context.createUnmarshaller();

			/* check wellformat xml with xsd */
			// TODO I CAN NOT UNDERSTAND WHY WE CAN GET THE LOADER FROM A
			// COMMAND
			// SchemaFactory sf = SchemaFactory.newInstance(
			// javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			// ClassLoader loader =
			// Thread.currentThread().getContextClassLoader();
			// Schema schema = sf.newSchema(new
			// StreamSource(loader.getResourceAsStream(NAME_SCHEMA)));
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
				printError("File not found: " + f.getMessage());
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
				printError("Given file is not a valid descriptor. Check it complies with descriptor schema. Invalid file: "
						+ url.toString());
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
								printError("Could not read file. Malformed path: "
										+ fileInDirectory.toURI().toString());
							}
						}
					}
				} else {
					if (file.getName().endsWith(".descriptor")) {
						urls.add(url);
					} else {
						printError("The file type is not a valid for "
								+ file.getName());
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

	/* methods to read descriptors */

	/**
	 * Helper methods to test these functionality...
	 * 
	 * @param filename
	 * @return
	 * @throws JAXBException
	 * @throws IOException
	 * @throws ResourceException
	 * @throws SAXException
	 */
	private NetworkTopology getNetworkDescriptor(String filename)
			throws JAXBException, IOException, ResourceException, SAXException {
		InputStream stream = null;
		// First try a URL
		try {
			URL url = new URL(filename);
			log.info("URL: " + url);
			stream = url.openStream();
		} catch (MalformedURLException ignore) {
			// Then try a file
			// Added class loader to read files

			// TODO check to read topologies with relative paths
			// stream =
			// this.getClass().getClassLoader().getResourceAsStream(filename);
			log.error("file: " + filename);
			stream = new FileInputStream(filename);
		}

		NetworkTopology rd = loadNetworkDescriptor(stream);
		return rd;
	}

	private NetworkTopology loadNetworkDescriptor(InputStream stream)
			throws JAXBException, SAXException {

		NetworkTopology descriptor = null;
		try {
			JAXBContext context = JAXBContext
					.newInstance(NetworkTopology.class);

			Unmarshaller unmarshaller = context.createUnmarshaller();

			/* check wellformat xml with xsd */
			// TODO I CAN NOT UNDERSTAND WHY WE CAN GET THE LOADER FROM A
			// COMMAND
			// SchemaFactory sf = SchemaFactory.newInstance(
			// javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			// ClassLoader loader =
			// Thread.currentThread().getContextClassLoader();
			// Schema schema = sf.newSchema(new
			// StreamSource(loader.getResourceAsStream(NAME_SCHEMA)));
			// unmarshaller.setSchema(schema);

			descriptor = (NetworkTopology) unmarshaller.unmarshal(stream);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// Ignore
			}
		}
		return descriptor;

	}

}
