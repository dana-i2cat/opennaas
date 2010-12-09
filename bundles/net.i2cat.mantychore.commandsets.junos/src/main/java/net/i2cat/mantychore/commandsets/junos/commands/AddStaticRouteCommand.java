package net.i2cat.mantychore.commandsets.junos.commands;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import net.i2cat.mantychore.commandsets.junos.digester.RouterParser;
import net.i2cat.mantychore.commandsets.junos.digester.StaticRouteParser;
import net.i2cat.mantychore.models.router.RouterModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.model.IResourceModel;

public class AddStaticRouteCommand extends JunosCommand {

	public static final String		ADDSTATICROUTE	= "addStaticRoute";

	public static final String		TEMPLATE		= "/addStaticRoute.vm";

	private HashMap<String, String>	params			= new HashMap<String, String>();

	/** The logger **/
	Logger							log				= LoggerFactory
															.getLogger(AddStaticRouteCommand.class);

	public final String				message			= "<data><configuration><logical-systems><routing-options><rib><name>inet6.0</name><static><route><name>2001:770:ad::/48</name><discard/></route></static></rib><static><route><name>193.1.190.0/24</name><next-hop>193.1.190.1</next-hop></route></static><autonomous-system><as-number>65166</as-number></autonomous-system></routing-options></logical-systems></configuration></data>";

	public AddStaticRouteCommand(String logicalRouterName,
			ArrayList<String> listStaticRoutingProtocol) {
		super(ADDSTATICROUTE);
		log.debug("Preparing AddStaticRouteCommand...");
		this.setTemplate(TEMPLATE);

		params.put("logicalRouterName", logicalRouterName);

		// params.putAll(listStaticRoutingProtocol);

		// this.setParams(params);

	}

	@Override
	public void parseResponse(IResourceModel model) throws CommandException {
		log.debug("Debugging response from addStaticRoute...");
		// ProtocolResponseMessage message = (ProtocolResponseMessage) response;

		RouterParser.setRouterModel((RouterModel) model);

		/* fill first parameters */
		digester.addObjectCreate("data/configuration", RouterParser.class);

		digester
				.addObjectCreate(
						"data/configuration/logical-systems/routing-options/static/route",
						StaticRouteParser.class);
		digester
				.addCallMethod(
						"data/configuration/logical-systems/routing-options/static/route/name",
						"setIpAddressDestinationSubNetwork", 1);
		digester
				.addCallParam(
						"data/configuration/logical-systems/routing-options/static/route/name",
						0);
		digester
				.addCallMethod(
						"data/configuration/logical-systems/routing-options/static/route",
						"setIpAddressNextHop", 1);
		digester
				.addCallParam(
						"data/configuration/logical-systems/routing-options/static/route/next-hop",
						0);

		// digester
		// .addBeanPropertySetter(
		// "data/configuration/logical-systems/routing-options/static/route/name",
		// "IpAddressDestinationSubNetwork");
		//
		// digester
		// .addBeanPropertySetter(
		// "data/configuration/logical-systems/routing-options/static/route/next-hop",
		// "IpAddressNextHop");
		//
		// digester
		// .addSetNext(
		// "data/configuration/logical-systems/routing-options/static/route",
		// "setStaticRoute");

		digester
				.addSetNext(
						"data/configuration/logical-systems/routing-options/static/route",
						"addStaticRoute");

		/* Get interfaces information */

		// InputStream input = new
		// ByteArrayInputStream(message.getMessage().getBytes());

		try {
			InputStream input = new ByteArrayInputStream(message.trim()
					.getBytes());
			digester.parse(input);
		} catch (IOException e) {
			log.error("Error IO in command AddStaticRoute", e.getMessage(), e);
			throw new CommandException(e.getMessage());
		} catch (SAXException e) {
			log.error("Error SAX Exception in command AddStaticRoute", e
					.getMessage(), e);
			e.printStackTrace();
			throw new CommandException(e.getMessage());
		}

	}

	@Override
	public void initializeCommand(IResourceModel modelInfo) throws CommandException {
		// TODO Auto-generated method stub

	}
}
