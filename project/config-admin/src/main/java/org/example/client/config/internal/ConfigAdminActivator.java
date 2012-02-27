package org.example.client.config.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.example.client.config.PrettyPrinterConfigurator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Extension of the default OSGi bundle activator
 */
public final class ConfigAdminActivator
    implements BundleActivator
{
	// Bundle's context.
    private BundleContext m_context = null;
    
    private ServiceRegistration ppcService;
    /**
     * Implements BundleActivator.start(). Crates a service
     * tracker to monitor dictionary services and starts its "word
     * checking loop". It will not be able to check any words until
     * the service tracker find a dictionary service; any discovered
     * dictionary service will be automatically used by the client.
     * It reads words from standard input and checks for their
     * existence in the discovered dictionary.
     * (NOTE: It is very bad practice to use the calling thread
     * to perform a lengthy process like this; this is only done
     * for the purpose of the tutorial.)
     * @param context the framework context for the bundle.
    **/
    public void start( BundleContext context )
        throws Exception
    {
        m_context = context;
        
        registerConfigService();
        
        
    }

	private void registerConfigService() {
		Dictionary<String, String> props = new Hashtable<String, String>();
        props.put("service.pid", "org.example.client.config.PrettyPrinterConfigurator");
        ppcService = m_context.registerService(ManagedService.class.getName(), new PrettyPrinterConfigurator(), props);
	}

	/*private void initConfigAdminServiceTracker() throws InvalidSyntaxException {
		// Create a service tracker to monitor dictionary services.
        m_service_tracker = new ServiceTracker( m_context, m_context.createFilter("(&(objectClass=" + String.class.getName() + "))"),null);
        m_service_tracker.open();
	}
*/
	
    /**
     * Called whenever the OSGi framework stops our bundle
     */
    public void stop( BundleContext bc )
        throws Exception
    {
        // no need to unregister our service - the OSGi framework handles it for us
        
        if (ppcService != null) {
            ppcService.unregister();
            ppcService = null;
        }
    }
}

