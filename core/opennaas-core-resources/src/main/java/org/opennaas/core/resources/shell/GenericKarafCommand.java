package org.opennaas.core.resources.shell;

import java.io.PrintStream;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.opennaas.core.resources.Activator;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.protocol.IProtocolManager;

public abstract class GenericKarafCommand extends OsgiCommandSupport {
	Log						log					= LogFactory.getLog(GenericKarafCommand.class);

	protected PrintStream	out					= System.out;
	protected PrintStream	err					= System.err;
	protected int			counter				= 0;
	protected int			totalFiles			= 0;
	protected String[]		argsRouterName		= null;
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

	public void initcommand(String commandName) {

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

	public void endcommand() {
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

	public boolean splitResourceName(String complexName) {
		argsRouterName = complexName.split(":");
		if (argsRouterName.length != 2) {
			printError("Invalid resourceId.");
			printError("ResourceId must have the format [resourceType:resourceName]");
			endcommand();
			return false;
		}
		if (argsRouterName[0].equalsIgnoreCase("")) {
			printError("Invalid resource type.");
			printError("[resourceType] can not be null");
			endcommand();
			return false;
		}
		if (argsRouterName[1].equalsIgnoreCase("")) {
			printError("Invalid resource name.");
			printError("[resourceName] can not be null");
			endcommand();
			return false;
		}

		return true;
	}

	public boolean splitInterfaces(String complexInterface) {
		try {
			argsInterface = complexInterface.split("\\.");
			if (argsInterface.length != 2) {
				printError("Invalid format in interface name.");
				endcommand();
				return false;
			}

		} catch (Exception e) {
			// throw new Exception("Bad interface name.", e);
			printError("Error reading the interface name.");
			endcommand();
			return false;
		}
		return true;
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
			endcommand();
			return null;
		}

		if (!splitResourceName(resourceId))
			return null;

		IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
		if (resourceIdentifier == null) {
			printError("Error in identifier.");
			endcommand();
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

	protected boolean validateResource(IResource resource) throws ResourceException {
		if (resource == null)
			throw new ResourceException("No resource found.");
		if (resource.getModel() == null)
			throw new ResourceException("The resource didn't have a model initialized. Start the resource first.");
		if (resource.getCapabilities() == null) {
			throw new ResourceException("The resource didn't have the capabilities initialized. Start the resource first.");
		}
		return true;
	}
}
