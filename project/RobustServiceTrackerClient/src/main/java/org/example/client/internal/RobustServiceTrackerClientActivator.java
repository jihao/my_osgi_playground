package org.example.client.internal;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.example.simple.ExampleService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Extension of the default OSGi bundle activator
 */
public final class RobustServiceTrackerClientActivator
    implements BundleActivator
{
	// Bundle's context.
    private BundleContext m_context = null;
    // The service tacker object.
    private ServiceTracker m_example_service_tracker = null;
    
    // The log service tracker object.
    private ServiceTracker m_log_tracker = null;
    
    // The log service object
    private LogService m_log_service = null;

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
        
        initExampleServiceTracker();
        
        initLogService();
        
        // bad practice here 
        if (m_log_service != null ) {
        	
	        m_log_service.log(LogService.LOG_DEBUG, "STARTING " + this.getClass().getName());
	        
	        try
	        {
	        	m_log_service.log(LogService.LOG_INFO, "Enter a blank line to exit.");
	            String word = "";
	            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	            
	           
	            m_log_service.log(LogService.LOG_DEBUG, "\nmy_tracker servcies:");
	            for (Object service :  m_example_service_tracker.getServices()) {
	            	m_log_service.log(LogService.LOG_DEBUG, "[DEBUG] "+service.toString());
				}
	            m_log_service.log(LogService.LOG_DEBUG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	            
	            // Get the selected dictionary service, if available.
	            ExampleService exampleService = (ExampleService) m_example_service_tracker.getService();
	            
	            // Loop endlessly.
	            while (true)
	            {
	                // Ask the user to enter a word.
	            	m_log_service.log(LogService.LOG_INFO, "Enter word: ");
	                word = in.readLine();
	
	                // If the user entered a blank line, then
	                // exit the loop.
	                if (word.length() == 0)
	                {
	                    break;
	                }
	                // If there is no dictionary, then say so.
	                else if (exampleService == null)
	                {
	                    m_log_service.log(LogService.LOG_DEBUG, "No ExampleService available.");
	                }
	                else
	                {
	                	m_log_service.log(LogService.LOG_DEBUG, exampleService.toString());
	                	m_log_service.log(LogService.LOG_DEBUG, "input=["+word+"], output="+exampleService.scramble(word));
	                }
	            }
	        } catch (Exception ex) { }
        }
        
    }

	private void initExampleServiceTracker() throws InvalidSyntaxException {
		// Create a service tracker to monitor dictionary services.
        m_example_service_tracker = new ServiceTracker(
            m_context,
            m_context.createFilter(
                "(&(objectClass=" + ExampleService.class.getName() + ")" +
                "(Language=*))"),
            null);
        m_example_service_tracker.open();
	}

	private void initLogService() {
		m_log_tracker = new ServiceTracker(m_context, LogService.class.getName(), null);
        m_log_tracker.open();
        m_log_service = (LogService)m_log_tracker.getService();
        
        ServiceTracker log_reader_service_tracker = new ServiceTracker(m_context, LogReaderService.class.getName(), null);
        log_reader_service_tracker.open();
        LogReaderService reader = (LogReaderService) log_reader_service_tracker.getService();
        // bad practice here 
        if (reader!=null) {
        	reader.addLogListener(new LogWriter());
        }
	}

    /**
     * Called whenever the OSGi framework stops our bundle
     */
    public void stop( BundleContext bc )
        throws Exception
    {
    	// bad practice here 
        if (m_log_service != null ) {
        	m_log_service.log(LogService.LOG_DEBUG,  "STOPPING " + this.getClass().getName() );
        }
        // no need to unregister our service - the OSGi framework handles it for us
    }
}

