package bundle.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import bundle.MyService;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		Dictionary<String, String> restProps = new Hashtable<String, String>();

		restProps.put("service.exported.interfaces", "*");
		restProps.put("service.exported.configs", "org.apache.cxf.rs");
		restProps.put("service.exported.intents", "HTTP");
		restProps.put("org.apache.cxf.rs.address", "http://localhost:8888/");

		context.registerService(MyService.class.getName(), new MyServiceImpl(), restProps);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		//
	}
}