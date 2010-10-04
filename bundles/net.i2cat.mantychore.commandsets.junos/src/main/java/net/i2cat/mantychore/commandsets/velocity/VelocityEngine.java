package net.i2cat.mantychore.commandsets.velocity;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocityEngine {

	Logger							log				= LoggerFactory
															.getLogger(VelocityEngine.class);

	private static String			VELOCITY_PROPS	= "/velocity.properties";
	private VelocityContext			ctx;
	private String					template;
	private HashMap<String, String>	params			= new HashMap<String, String>();
	private ArrayList				list;

	public VelocityEngine(String template, HashMap<String, String> params,
			ArrayList list) {
		this.template = template;
		this.params = params;
		this.list = list;

	}

	private void init() throws Exception {
		Properties prop = new Properties();

		// load configure file
		log.info("Trying to open " + VELOCITY_PROPS);
		prop.load(getClass().getResourceAsStream(VELOCITY_PROPS));

		// Initialize velocity
		Velocity.init(prop);

		ctx = new VelocityContext();
	}

	public String mergeTemplate() throws ResourceNotFoundException,
			ParseErrorException, Exception {

		init();
		String currentPath = System.getProperty("user.dir");
		log.info("Trying to open " + currentPath + File.separator + template);
		Template tpl = Velocity.getTemplate(template);

		Iterator<String> iterKeys = params.keySet().iterator();
		while (iterKeys.hasNext()) {
			String key = iterKeys.next();
			String value = params.get(key);
			ctx.put(key, value);

		}

		ctx.put("list", list);

		Writer writer = new StringWriter();

		tpl.merge(ctx, writer);

		return writer.toString();
	}
}
