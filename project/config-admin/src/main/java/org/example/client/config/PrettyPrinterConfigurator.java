package org.example.client.config;

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

public class PrettyPrinterConfigurator implements ManagedService {

	@Override
	public void updated(Dictionary props) throws ConfigurationException {
		if (props == null) {
            // no configuration from configuration admin
            // or old configuration has been deleted
			System.out.println("props == null");
        } else {
            // apply configuration from config admin
        	System.out.println("apply configuration from config admin");
        }

	}

}
