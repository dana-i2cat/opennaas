package net.i2cat.mantychore.commandsets.junos.velocity;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocityEngine {

	public static final String	PARAMS			= "params";

	Logger						log				= LoggerFactory
															.getLogger(VelocityEngine.class);

	private static String		VELOCITY_PROPS	= "/velocity.properties";
	private VelocityContext		ctx;
	private String				template;
	private Object				params;

	public VelocityEngine(String template, Object params) {
		this.template = template;
		this.params = params;

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
		Template tpl = Velocity.getTemplate(template);

		ctx.put(PARAMS, params);

		Writer writer = new StringWriter();

		tpl.merge(ctx, writer);

		return writer.toString();
	}
}
