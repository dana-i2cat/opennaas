package net.i2cat.mantychore.protocols.internal;

import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import net.i2cat.mantychore.protocols.ExampleService;

/**
 * Extension of the default OSGi bundle activator
 */
public final class ExampleActivator
    implements BundleActivator
{
    /**
     * Called whenever the OSGi framework starts our bundle
     */
    public void start( BundleContext bc )
        throws Exception
    {
        System.out.println( "STARTING net.i2cat.mantychore.protocols" );

        Dictionary props = new Properties();
        // add specific service properties here...

        System.out.println( "REGISTER net.i2cat.mantychore.protocols.ExampleService" );

        // Register our example service implementation in the OSGi service registry
        bc.registerService( ExampleService.class.getName(), new ExampleServiceImpl(), props );
    }

    /**
     * Called whenever the OSGi framework stops our bundle
     */
    public void stop( BundleContext bc )
        throws Exception
    {
        System.out.println( "STOPPING net.i2cat.mantychore.protocols" );

        // no need to unregister our service - the OSGi framework handles it for us
    }
}

