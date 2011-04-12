package net.i2cat.nexus.resources.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.RegistryUtil;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;

/**
 * Create a new resource from the URL or file given on the karaf shell
 * @author Scott Campbell (CRC)
 * 
 */
@Command(scope = "nexus", name = "create", description = "Create one or more resources")
public class CreateResourceCommand extends OsgiCommandSupport {

	@Argument(index = 0, name = "paths or urls", description = "A space delimited list of file paths or urls to the resource descriptors for the resources to create", required = true, multiValued = true)
	private List<String> paths;

	@Override
	protected Object doExecute() throws Exception {
		log.debug("Executing create resource shell command");

		try {
			IResourceManager manager = getResourceManager();

			ResourceDescriptor descriptor = null;
			for (String filename : paths) {
				descriptor = getResourceDescriptor(filename);
				System.out.println("Creating Resource " + descriptor.getInformation().getName());
				IResource resource = manager.createResource(descriptor);
				System.out.println("Resource created with ID: "
						+ resource.getResourceIdentifier().getId());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private ResourceDescriptor getResourceDescriptor(String filename) throws Exception {
		InputStream stream = null;
		// First try a URL
		try {
			URL url = new URL(filename);
			log.info("URL: " + url);
			stream = url.openStream();
		}
		catch (MalformedURLException ignore) {
			// They try a file
			File file = new File(filename);
			log.info("file: " + file);
			stream = new FileInputStream(file);
		}

		ResourceDescriptor descriptor = null;
		try {
			JAXBContext context = JAXBContext.newInstance(ResourceDescriptor.class);
			descriptor = (ResourceDescriptor) context.createUnmarshaller().unmarshal(stream);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				stream.close();
			}
			catch (IOException e) {
				// Ingore
			}
		}
		return descriptor;
	}

	private IResourceManager getResourceManager() throws Exception {
		IResourceManager resourceManager = (IResourceManager) RegistryUtil.getServiceFromRegistry(
				getBundleContext(), IResourceManager.class.getName());

		return resourceManager;
	}
}
