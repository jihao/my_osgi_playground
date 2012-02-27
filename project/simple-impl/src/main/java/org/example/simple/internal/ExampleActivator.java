package org.example.simple.internal;

import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import org.example.simple.ExampleService;

/**
 * Extension of the default OSGi bundle activator
 */
public final class ExampleActivator
    implements BundleActivator, ServiceListener
{
    /**
     * Called whenever the OSGi framework starts our bundle
     */
    public void start( BundleContext bc )
        throws Exception
    {
        System.out.println( "STARTING org.example.simple" );
        
        System.out.println("Starting to listen for service events.");
        bc.addServiceListener(this);
        
        Dictionary props = new Properties();
        props.put("Language", "Simple");
        // add specific service properties here...

        System.out.println( "REGISTER org.example.simple.ExampleService" );

        // Register our example service implementation in the OSGi service registry
        bc.registerService( ExampleService.class.getName(), new SimpleServiceImpl(), props );
        
        
    }

    /**
     * Called whenever the OSGi framework stops our bundle
     */
    public void stop( BundleContext bc )
        throws Exception
    {
        System.out.println( "STOPPING org.example.simple" );
        System.out.println("Stopped listening for service events.");
        bc.removeServiceListener(this);
        // no need to unregister our service - the OSGi framework handles it for us
    }

	@Override
	public void serviceChanged(ServiceEvent event) {
		String[] objectClass = (String[])
	            event.getServiceReference().getProperty("objectClass");

	        if (event.getType() == ServiceEvent.REGISTERED)
	        {
	            System.out.println(
	                "Ex1: Service of type " + objectClass[0] + " registered.");
	        }
	        else if (event.getType() == ServiceEvent.UNREGISTERING)
	        {
	            System.out.println(
	                "Ex1: Service of type " + objectClass[0] + " unregistered.");
	        }
	        else if (event.getType() == ServiceEvent.MODIFIED)
	        {
	            System.out.println(
	                "Ex1: Service of type " + objectClass[0] + " modified.");
	        }
		
	}
}

