package org.example.client.config.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.example.client.config.DataBaseConnectionConfigurator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;

/**
 * Extension of the default OSGi bundle activator
 */
public final class ConfigAdminActivator
    implements BundleActivator
{
	// Bundle's context.
    private BundleContext m_context = null;
    
    private ServiceRegistration dbConfigService;
    private List configurationList = new ArrayList(); 
    
    public void start( BundleContext context )
        throws Exception
    {
        m_context = context;
        
        registerConfigService();
        
        ServiceReference configurationAdminReference = context.getServiceReference(ConfigurationAdmin.class.getName());  
        if (configurationAdminReference != null) 
        {  
            ConfigurationAdmin confAdmin = (ConfigurationAdmin) context.getService(configurationAdminReference);  
              
            Configuration configuration = confAdmin.createFactoryConfiguration("org.example.client.config.DataBaseConnectionConfigurator", null);  
            Dictionary properties = createServiceProperties();
            configuration.update(properties);  
              
            //Store in the configurationList the configuration object, the dictionary object
            //or configuration.getPid()  for future use  
            configurationList.add(configuration);  
        }  
    }

	private Dictionary createServiceProperties() {
		Properties p = new Properties();
		p.put("key1", "value1");
		p.put("key2", "value2");
		return p;
	}

	private void registerConfigService() {
		Dictionary<String, String> props = new Hashtable<String, String>();
        props.put("service.pid", "org.example.client.config.DataBaseConnectionConfigurator");
        dbConfigService = m_context.registerService(ManagedService.class.getName(), new DataBaseConnectionConfigurator(), props);
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
        
        if (dbConfigService != null) {
            dbConfigService.unregister();
            dbConfigService = null;
        }
    }
}

//TODO: make CLI change config working 
class ServerThread implements Runnable {

	private int port;
	private ServerSocket server;
	
	public ServerThread(int port) {
		super();
		this.port = port;
	}
	
	public void shutdown() {
		try {
			server.close();
		} catch (IOException e) {
			System.err.println("[Exception] happened during shutdown ServerSocket");
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
        try {
        	// Start our socket server listen on port 3333
			server = new ServerSocket(port);
			Socket client = null;
			//accepts client connection
			while( (client = server.accept()) != null ){
				System.out.println("[Connection Established] client port:"+client.getPort());
				//use another thread to handle client connection
				new Thread(new ConnectionHandlerThread(client)).start();
			}
			
		} catch (IOException e) {
			System.err.println("[Exception] happened during listen for connections");
			e.printStackTrace();
		}
	}
	
}
class ConnectionHandlerThread implements Runnable {
	private Socket socket;
	public ConnectionHandlerThread(Socket socket) {
		super();
		this.socket = socket;
	}
	@Override
	public void run() {
		try {
			System.out.println("Wait for client message....");
			// the echo server implementation
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			String msg = reader.readLine();
			while(msg!=null) {
				System.out.println("Received:"+msg);
				writer.write("[Echo]:"+msg+"\n");
				writer.flush();
				// exit when received message "bye"
				if(msg.equalsIgnoreCase("bye")) {
					break;
				}
				System.out.println("Wait for client message....");
				msg = reader.readLine();
			}
			reader.close();
			writer.close();
			
			System.out.println("[Connection closing] client port:"+socket.getPort());
			//close connection when received "bye"
			socket.close();
			
		} catch (IOException e) {
			System.err.println("[Exception] happened during handle for connection");
			e.printStackTrace();
		}
		
		
		
	}
}

