package org.example.client.config.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.example.client.config.DataBaseConnection;
import org.example.client.config.DataBaseConnectionConfigurator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Extension of the default OSGi bundle activator
 */
public final class ConfigAdminActivator
    implements BundleActivator
{
	public static final String DATA_BASE_CONNECTION_CONFIGURATOR_PID = "org.example.client.config.DataBaseConnectionConfigurator";

	private static final Integer SERVER_PORT = 3334;
	
	// Bundle's context.
    private BundleContext m_context = null;
    
    private ServiceTracker configAdminServiceTracker = null;
    
    private ServerThread serverThread = null;
    
    public void start( BundleContext context )
        throws Exception
    {
        m_context = context;
        
        registerDataBaseConnectionConfigService();
        initConfigAdminServiceTracker();
        
        startServerSocket();
    }

	private void startServerSocket() {
		serverThread = new ServerThread(SERVER_PORT, configAdminServiceTracker);
        new Thread( serverThread ).start();
	}

	private void initConfigAdminServiceTracker() {
		configAdminServiceTracker = new ServiceTracker(m_context, ConfigurationAdmin.class.getName(), null);
        configAdminServiceTracker.open();
	}

	private void registerDataBaseConnectionConfigService() {
		Dictionary<String, String> props = new Hashtable<String, String>();
        props.put("service.pid", DATA_BASE_CONNECTION_CONFIGURATOR_PID);
        m_context.registerService(ManagedService.class.getName(), new DataBaseConnectionConfigurator(), props);
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
        serverThread.shutdown();
    }
}

class ServerThread implements Runnable {

	private int port;
	private ServerSocket server;
	private ServiceTracker configAdminServiceTracker;
	
	/**
	 * 
	 * @param port
	 * @param serviceTracker the configAdminServiceTracker
	 */
	public ServerThread(int port,ServiceTracker configAdminServiceTracker) {
		super();
		this.port = port;
		this.configAdminServiceTracker = configAdminServiceTracker;
	}
	
	/**
	 * shutdown the serverSocket
	 */
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
				ConfigurationAdmin configAdmin = (ConfigurationAdmin) configAdminServiceTracker.getService();
				//use another thread to handle client connection
				new Thread(new ConnectionHandlerThread(client,configAdmin)).start();
			}
			
		} catch (IOException e) {
			System.err.println("[Excepted exception] happened during listen for connections");
		}
	}
	
}
class ConnectionHandlerThread implements Runnable {
	private Socket socket;
	private ConfigurationAdmin configAdmin;
	
	public ConnectionHandlerThread(Socket socket, ConfigurationAdmin configAdmin) {
		super();
		this.socket = socket;
		this.configAdmin = configAdmin;
	}
	
	@Override
	public void run() {
		try {
			System.out.println("[Connection Established] client port:" + socket.getPort());
			
			if (configAdmin==null) {
				System.out.println("Can't load configAdmin");
			} else {
				process();
			}
			
			System.out.println("[Connection closing] client port:"+socket.getPort());
			socket.close();
		} catch (IOException e) {
			System.err.println("[Exception] happened during handle for connection");
		}
	}

	/**
	 * demonstrate using CLI to update config admin
	 * 
	 * @throws IOException
	 */
	private void process() throws IOException {
		Configuration config = configAdmin.getConfiguration(ConfigAdminActivator.DATA_BASE_CONNECTION_CONFIGURATOR_PID);
		Dictionary properties = config.getProperties();
		if (properties==null) {
			System.out.println("properties=null");
			properties = loadDefaultConfiguration(config);
		}
		DataBaseConnection dataBaseConnection = instantiationDataBaseConnection(properties);
		
		// the echo server implementation
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		readNewConfiguration(dataBaseConnection, reader, writer);
		
		updateConfigurationProperties(config, properties, dataBaseConnection);
		
		reader.readLine();
		reader.close();
		writer.close();
	}

	private Dictionary loadDefaultConfiguration(Configuration config) {
		Dictionary properties = new Properties();
		properties.put(DataBaseConnectionConfigurator.USERNAME, "deafult_username");
		properties.put(DataBaseConnectionConfigurator.PASSWORD, "deafult_password");
		properties.put(DataBaseConnectionConfigurator.CONNECTION_URL, "jdbc:mysql://localhost:3306/mysql");
		try {
			config.update(properties);
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return properties;
	}

	private DataBaseConnection instantiationDataBaseConnection(Dictionary properties) {
		String username = (String)properties.get(DataBaseConnectionConfigurator.USERNAME);
		String password = (String)properties.get(DataBaseConnectionConfigurator.PASSWORD);
		String connection_url = (String)properties.get(DataBaseConnectionConfigurator.CONNECTION_URL);
		return new DataBaseConnection(username, password, connection_url);
		
	}

	/**
	 * read new configuration from socket, update the dataBaseConnection object
	 * 
	 * @param dataBaseConnection
	 * @param reader
	 * @param writer
	 * @throws IOException
	 */
	private void readNewConfiguration(DataBaseConnection dataBaseConnection,
			BufferedReader reader, BufferedWriter writer) throws IOException {
		writer.write("You can modify the configureation\r\n");
		writer.write("------------Current-----------\r\n");
		writer.write("username=" + dataBaseConnection.username+"\r\n");
		writer.write("pasword=" + dataBaseConnection.password+"\r\n");
		writer.write("connection_url=" + dataBaseConnection.connection_url+"\r\n");
		writer.write("--------------END-------------\r\n");
		writer.write("New username=");
		writer.flush();
		dataBaseConnection.username = reader.readLine();
		
		writer.write("\r\nNew password=");
		writer.flush();
		dataBaseConnection.password = reader.readLine();
		
		writer.write("\r\nNew connection_url=");
		writer.flush();
		dataBaseConnection.connection_url = reader.readLine();
		
		writer.write(dataBaseConnection.toString()+"\r\n");
		writer.write("Press enter to exit\r\n");
		writer.flush();
	}

	/**
	 * 
	 * @param config - Configuration object
	 * @param properties - old properties
	 * @param dbConn - new DataBaseConnection object
	 */
	private void updateConfigurationProperties(Configuration config, Dictionary properties, DataBaseConnection dbConn) {
		properties.put(DataBaseConnectionConfigurator.USERNAME, dbConn.username);
		properties.put(DataBaseConnectionConfigurator.PASSWORD, dbConn.password);
		properties.put(DataBaseConnectionConfigurator.CONNECTION_URL, dbConn.connection_url);
		try {
			config.update(properties);
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
}

