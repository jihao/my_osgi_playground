package org.example.client.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Dictionary;
import java.util.Properties;

import org.example.simple.ExampleService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

/**
 * Extension of the default OSGi bundle activator
 */
public final class RobostClientActivator
    implements BundleActivator, ServiceListener
{
	// Bundle's context.
    private BundleContext m_context = null;
    // The service reference being used.
    private ServiceReference m_ref = null;
    // The service object being used.
    private ExampleService m_example = null;

    /**
     * Implements BundleActivator.start(). Adds itself
     * as a listener for service events, then queries for 
     * available dictionary services. If any dictionaries are
     * found it gets a reference to the first one available and
     * then starts its "word checking loop". If no dictionaries
     * are found, then it just goes directly into its "word checking
     * loop", but it will not be able to check any words until a
     * dictionary service arrives; any arriving dictionary service
     * will be automatically used by the client if a dictionary is
     * not already in use. Once it has dictionary, it reads words
     * from standard input and checks for their existence in the
     * dictionary that it is using.
     * (NOTE: It is very bad practice to use the calling thread
     * to perform a lengthy process like this; this is only done
     * for the purpose of the tutorial.)
     * @param context the framework context for the bundle.
    **/
  
    public void start( BundleContext context )
        throws Exception
    {
        System.out.println( "STARTING org.example.client" );

        m_context = context;

        // We synchronize while registering the service listener and
        // performing our initial dictionary service lookup since we
        // don't want to receive service events when looking up the
        // dictionary service, if one exists.
        synchronized (this)
        {
        	System.out.println("before addServiceListener...");
            // Listen for events pertaining to dictionary services.
            m_context.addServiceListener(this,
                "(&(objectClass=" + ExampleService.class.getName() + ")" +
                "(Language=*))");

            System.out.println("after addServiceListener...");
            
            // Query for any service references matching any language.
            ServiceReference[] refs = m_context.getServiceReferences(
                ExampleService.class.getName(), "(Language=*)");

            // If we found any dictionary services, then just get
            // a reference to the first one so we can use it.
            if (refs != null)
            {
                m_ref = refs[0];
                m_example = (ExampleService) m_context.getService(m_ref);
            }
            else 
            {
            	System.out.println("Couldn't find any ExampleService service...");
            }
        }

        try
        {
            System.out.println("Enter a blank line to exit.");
            String word = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            // Loop endlessly.
            while (true)
            {
                // Ask the user to enter a word.
                System.out.print("Enter word: ");
                word = in.readLine();

                // If the user entered a blank line, then
                // exit the loop.
                if (word.length() == 0)
                {
                    break;
                }
                // If there is no dictionary, then say so.
                else if (m_example == null)
                {
                    System.out.println("No ExampleService available.");
                }
                else
                {
                	System.out.println(m_example.toString());
                	System.out.println("input=["+word+"], output="+m_example.scramble(word));
                }
            }
        } catch (Exception ex) { }
        
    }

    /**
     * Called whenever the OSGi framework stops our bundle
     */
    public void stop( BundleContext bc )
        throws Exception
    {
        System.out.println( "STOPPING org.example.client" );

        // no need to unregister our service - the OSGi framework handles it for us
    }

    /**
     * Implements ServiceListener.serviceChanged(). Checks
     * to see if the service we are using is leaving or tries to get
     * a service if we need one.
     * @param event the fired service event.
    **/
	@Override
	public synchronized void serviceChanged(ServiceEvent event) {
		String[] objectClass = (String[]) event.getServiceReference().getProperty("objectClass");
		System.out.println("serviceChanged: Service of type " + objectClass[0] + " - " + event.getType()); 
		
		 
	        // If a dictionary service was registered, see if we
	        // need one. If so, get a reference to it.
	        if (event.getType() == ServiceEvent.REGISTERED)
	        {
	            if (m_ref == null)
	            {
	                // Get a reference to the service object.
	                m_ref = event.getServiceReference();
	                m_example = (ExampleService) m_context.getService(m_ref);
	            }
	            else 
	            {
	            	System.out.println("serviceChanged: my_ref!=null"); 
	            }
	        }
	        // If a dictionary service was unregistered, see if it
	        // was the one we were using. If so, unget the service
	        // and try to query to get another one.
	        else if (event.getType() == ServiceEvent.UNREGISTERING)
	        {
	            if (event.getServiceReference() == m_ref)
	            {
	            	System.out.println("Unget service object and null references."); 
	                // Unget service object and null references.
	                m_context.ungetService(m_ref);
	                m_ref = null;
	                m_example = null;

	                // Query to see if we can get another service.
	                ServiceReference[] refs = null;
	                try
	                {
	                    refs = m_context.getServiceReferences(
	                        ExampleService.class.getName(), "(Language=*)");
	                }
	                catch (InvalidSyntaxException ex)
	                {
	                    // This will never happen.
	                }
	                if (refs != null)
	                {
	                    // Get a reference to the first service object.
	                	System.out.println("Get a reference to the first service object."); 
	                    m_ref = refs[0];
	                    m_example = (ExampleService) m_context.getService(m_ref);
	                }
	                else
	                {
	                	System.out.println("Couldn't find another service...");
	                }
	            }
	        }
		
	}
}

