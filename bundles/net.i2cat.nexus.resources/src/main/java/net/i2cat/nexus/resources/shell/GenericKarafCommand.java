package net.i2cat.nexus.resources.shell;

import java.io.PrintStream;
import java.util.List;

import net.i2cat.nexus.resources.Activator;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.profile.IProfileManager;
import net.i2cat.nexus.resources.protocol.IProtocolManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.shell.console.OsgiCommandSupport;

public abstract class GenericKarafCommand extends OsgiCommandSupport {
	Log						log					= LogFactory.getLog(GenericKarafCommand.class);

	protected PrintStream	out					= System.out;
	protected PrintStream	err					= System.err;
	protected int			counter				= 0;
	protected int			totalFiles			= 0;
	protected String[]		argsInterface		= null;
	// Messages
	protected String		error				= "[ERROR] ";
	protected String		info				= "[INFO] ";
	// Symbols
	protected String		simpleTab			= " ";
	protected String		doubleTab			= "     ";
	protected String		indexArrowRigth		= ">>>>";
	protected String		indexArrowLeft		= "<<<<";
	protected String		bullet				= "o";

	// Separators
	protected String		doubleLine			= "===========================================================================";
	protected String		underLine			= "___________________________________________________________________";
	protected String		horizontalSeparator	= "|------------------------------------------------------------------|";
	protected String		titleFrame			= "=";

	public void printInitCommand(String commandName) {

		String title = " Executing " + commandName + " command ";

		int length = title.length();
		float chFrame = (doubleLine.length() - length) / 2;
		String symbol = "";
		for (int i = 0; i < chFrame; i++) {
			symbol = symbol.concat("=");
		}
		if (((2 * chFrame) + length) >= doubleLine.length()) {
			printSymbol(symbol + title + symbol);
		} else if (chFrame % 2 == 0 && (((2 * chFrame) + length) < doubleLine.length())) {
			printSymbol(symbol + title + symbol + titleFrame);
		} else if (chFrame % 2 == 0) {
			printSymbol(symbol + title + symbol);
		} else {
			printSymbol(symbol + title + symbol + titleFrame);
		}

		printSymbol(doubleLine);
	}

	public void newLine() {
		printSymbol(underLine);
	}

	public void newSeparator() {
		printSymbol(horizontalSeparator);
	}

	public void printEndCommand() {
		printSymbol(doubleLine);
	}

	public void printError(String message) {
		err.println(error + message);
		log.error(error + message);
	}

	public void printError(Throwable e) {
		err.println(error + e.getMessage());
		log.error(e.getMessage(), e);
	}

	public void printError(String message, Throwable e) {
		err.println(error + message);
		log.error(error + message, e);
	}

	public void printInfo(String message) {
		out.println(info + message);
		log.info(info + message);
	}

	public void printSymbol(String symbol) {
		out.println(symbol);
	}

	public void printSymbolWithoutDoubleLine(String symbol) {
		out.print(symbol);
	}

	public void printfSymbol(String format, String symbol) {
		out.printf(format, symbol);
	}

	public String[] splitResourceName(String complexName) throws Exception

	{
		String[] argsRouterName = new String[2];
		argsRouterName = complexName.split(":");
		if (argsRouterName.length != 2) {
			Exception excep = new Exception("Invalid resourceId" + '\n' + "ResourceId must have the format [resourceType:resourceName]");
			throw excep;
		}
		if (argsRouterName[0].equalsIgnoreCase("")) {
			Exception excep = new Exception("Invalid resource type." + '\n' + "[resourceType] can not be null");
			throw excep;
		}
		if (argsRouterName[1].equalsIgnoreCase("")) {
			Exception excep = new Exception("Invalid resource name." + '\n' + "[resourceType] can not be null");
			throw excep;
		}
		return argsRouterName;

	}

	public String[] splitInterfaces(String complexInterface) throws Exception {
		String[] argsInterface = new String[2];
		try {

			argsInterface = complexInterface.split("\\.");
			if (argsInterface.length != 2) {
				Exception excep = new Exception("Invalid format in interface name.");
				throw excep;
			}

		} catch (Exception e) {
			// throw new Exception("Bad interface name.", e);
			printError("Error reading the interface name.");
			Exception excep = new Exception("Error reading the interface name.");
			throw excep;
		}
		return argsInterface;
	}

	public void printTable(String[] titles, String[][] values, int sizeWords) {
		boolean wordSizeVariable = false;
		wordSizeVariable = (sizeWords == -1);

		int max = 0;
		if (wordSizeVariable) {
			for (String title : titles) {
				int sizeCaracts = title.length();
				if (max < sizeCaracts)
					max = sizeCaracts;
			}
		}

		/* Print titles */
		for (String title : titles)
			printfSymbol("%" + max + "s ", title);
		printSymbolWithoutDoubleLine("\n");

		for (int row = 0; row < values.length; row++) {
			for (int col = 0; col < values[row].length; col++) {
				printfSymbol("%" + max + "s ", values[row][col]);
			}
			printSymbolWithoutDoubleLine("\n");
		}
	}

	protected IResourceManager getResourceManager() throws Exception {
		IResourceManager resourceManager = Activator.getResourceManagerService();
		return resourceManager;
	}

	protected IProfileManager getProfileManager() throws Exception {
		IProfileManager profileManager = Activator.getProfileManagerService();
		return profileManager;
	}

	protected IProtocolManager getProtocolManager() throws Exception {
		return (IProtocolManager) getAllServices(IProtocolManager.class, null).get(0);
	}

	/**
	 * TODO A method don't have to return null!!! REFACTOR
	 * 
	 * @param resourceId
	 * @return
	 * @throws ResourceException
	 */
	protected IResource getResourceFromFriendlyName(String resourceId) throws ResourceException {

		IResourceManager manager = null;
		try {
			manager = getResourceManager();
		} catch (Exception e) {
			printError("Error getting resource manager.");
			printError(e);
			return null;
		}
		if (manager == null) {
			printError("Error in manager.");
			printEndCommand();
			return null;
		}

		String[] argsRouterName = new String[2];
		try {
			argsRouterName = splitResourceName(resourceId);
		} catch (Exception e) {
			return null;
		}

		IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
		if (resourceIdentifier == null) {
			printError("Error in identifier.");
			printEndCommand();
			return null;
		}

		IResource resource = manager.getResource(resourceIdentifier);
		validateResource(resource);

		return resource;
	}

	protected ICapability getCapability(List<ICapability> capabilities, String type) throws Exception {
		for (ICapability capability : capabilities) {
			if (capability.getCapabilityInformation().getType().equals(type)) {
				return capability;
			}
		}
		throw new Exception("Error getting the capability");
	}

	protected boolean containsCapability(List<ICapability> capabilities, String type) throws Exception {
		for (ICapability capability : capabilities) {
			if (capability.getCapabilityInformation().getType().equals(type)) {
				return true;
			}
		}
		return false;
	}

	public static final String	NORESOURCEFOUND				= "No resource found.";
	public static final String	NOTMODELINITIALIZED			= "The resource didn't have a model initialized. Start the resource first.";
	public static final String	NOTCAPABILITIESINITIALIZED	= "The resource didn't have the capabilities initialized. Start the resource first.";

	protected boolean validateResource(IResource resource) throws ResourceException {
		if (resource == null)
			throw new ResourceException(NORESOURCEFOUND);
		if (resource.getModel() == null)
			throw new ResourceException(NOTMODELINITIALIZED);
		if (resource.getCapabilities() == null) {
			throw new ResourceException(NOTCAPABILITIESINITIALIZED);
		}
		return true;
	}

}
