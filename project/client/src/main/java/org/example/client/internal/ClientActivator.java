package org.example.client.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Dictionary;
import java.util.Properties;

import org.example.simple.ExampleService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Extension of the default OSGi bundle activator
 */
public final class ClientActivator
    implements BundleActivator
{
    /**
     * Called whenever the OSGi framework starts our bundle
	 *
     * Implements BundleActivator.start(). Queries for
     * all available dictionary services. If none are found it
     * simply prints a message and returns, otherwise it reads
     * words from standard input and checks for their existence
     * from the first dictionary that it finds.
     * (NOTE: It is very bad practice to use the calling thread
     * to perform a lengthy process like this; this is only done
     * for the purpose of the tutorial.)
     * @param context the framework context for the bundle.
    **/

    public void start( BundleContext bc )
        throws Exception
    {
        System.out.println( "STARTING org.example.client" );
        // Query for all service references matching any language.
        ServiceReference[] refs = bc.getServiceReferences(
        		ExampleService.class.getName(), "(Language=*)");
        if (refs != null)
        {
            try
            {
                System.out.println("Enter a blank line to exit.");
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                String word = "";

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

                    // First, get a dictionary service and then check
                    // if the word is correct.
                    
                    for (ServiceReference serviceReference : refs) {
                    	ExampleService service =
                                (ExampleService) bc.getService(serviceReference);
                    	System.out.println(service.toString());
                    	System.out.println("input=["+word+"], output="+service.scramble(word));
                    	
                    	bc.ungetService(serviceReference);
					}
                }
            } catch (IOException ex) { }
        }
        else
        {
            System.out.println("Couldn't find any ExampleService service...");
        }
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
}

