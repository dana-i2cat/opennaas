package net.i2cat.mantychore.commandsets.junos.velocity;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class VelocityEngine {

	Logger							logger			= Logger
															.getLogger(VelocityEngine.class);

	private static String			VELOCITY_PROPS	= "resources/velocity.properties";
	private VelocityContext			_ctx;
	private String					template;
	private HashMap<String, String>	params			= new HashMap<String, String>();

	public VelocityEngine(String template, HashMap<String, String> params) {
		this.template = template;
		this.params = params;

	}

	private void init() throws Exception {
		Properties prop = new Properties();

		// load configure file
		prop.load(getClass().getResourceAsStream(VELOCITY_PROPS));

		// Initialize velocity
		Velocity.init(prop);

		_ctx = new VelocityContext();
	}

	public String mergeTemplate() throws ResourceNotFoundException,
			ParseErrorException, Exception {

		init();
		Template tpl = Velocity.getTemplate(template);

		Iterator<String> iterKeys = params.keySet().iterator();
		while (iterKeys.hasNext()) {
			String key = iterKeys.next();
			String value = params.get(key);
			_ctx.put(key, value);

		}

		Writer writer = new StringWriter();

		tpl.merge(_ctx, writer);

		return writer.toString();
	}
}
