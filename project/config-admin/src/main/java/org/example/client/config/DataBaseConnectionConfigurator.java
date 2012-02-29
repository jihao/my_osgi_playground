package org.example.client.config;

import java.util.Dictionary;
import java.util.Enumeration;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

public class DataBaseConnectionConfigurator implements ManagedService {

	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String CONNECTION_URL = "connection_url";
	@Override
	public void updated(Dictionary props) throws ConfigurationException {
		if (props == null) {
            // no configuration from configuration admin
            // or old configuration has been deleted
			System.out.println(">>>props == null<<<");
        } else {
            // apply configuration from config admin
        	System.out.println(">>>apply configuration from config admin");
        	Enumeration keys = props.keys();
        	while(keys.hasMoreElements()) {
        		String key = (String)keys.nextElement();
        		System.out.println(key+"="+props.get(key));
        	}
        	System.out.println("<<<done!");
        }
	}
}
