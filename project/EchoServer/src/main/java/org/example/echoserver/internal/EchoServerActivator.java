package org.example.echoserver.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Extension of the default OSGi bundle activator
 */
public final class EchoServerActivator
    implements BundleActivator
{
	private static final Integer SERVER_PORT = 3333;
	private ServerThread serverThread = null;
    /**
     * Called whenever the OSGi framework starts our bundle
     */
    public void start( BundleContext bc )
        throws Exception
    {
        System.out.println( "STARTING org.example.echoserver" );
        System.out.println( "REGISTER org.example.echoserver.ExampleService" );

        serverThread = new ServerThread(SERVER_PORT);
        new Thread(serverThread).start();
    }

    /**
     * Called whenever the OSGi framework stops our bundle
     */
    public void stop( BundleContext bc )
        throws Exception
    {
        System.out.println( "STOPPING org.example.echoserver" );
        
        //stop serverSocket
        serverThread.shutdown();
        
        // no need to unregister our service - the OSGi framework handles it for us
    }
}

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