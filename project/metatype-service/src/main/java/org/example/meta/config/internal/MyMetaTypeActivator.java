package org.example.meta.config.internal;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Extension of the default OSGi bundle activator
 */
public final class MyMetaTypeActivator implements BundleActivator, ManagedService {
    public static final String METATYPE_BUNDLE_PID = "org.example.meta.config.pid";
    // Bundle's context.
    private BundleContext context = null;
    private ServiceTracker configAdminServiceTracker = null;
    private ServiceTracker metaTypeServiceTracker = null;
    // Method inherited from BundleActivator
    public void start(BundleContext bc) throws Exception {
        this.context = bc;
        // Retrieving the OSGi Metatype Service from the OSGi framework
        this.metaTypeServiceTracker = new ServiceTracker(context, MetaTypeService.class.getName(), null);
        this.metaTypeServiceTracker.open();
        
        configAdminServiceTracker = new ServiceTracker(context, ConfigurationAdmin.class.getName(), null);
        configAdminServiceTracker.open();
    
        Dictionary<String, String> props = new Hashtable<String, String>();
        props.put("service.pid", METATYPE_BUNDLE_PID);
        props.put("meta.key1", "meta.value1");
        props.put("meta.key2", "meta.value2");
        context.registerService(ManagedService.class.getName(), this, props);
    }

    // Method inherited from BundleActivator
    public void stop(BundleContext bc) throws Exception {
        metaTypeServiceTracker.close();
        configAdminServiceTracker.close();
    }

    @Override
    public void updated(@SuppressWarnings("rawtypes") Dictionary properties) throws ConfigurationException {
        System.out.println(this.getClass().getName()+"; properties=="+properties);
        if (properties == null) {
            System.out.println(this.getClass().getName()+"; >>>props == null<<<");
        } else {
            // apply configuration from config admin
            System.out.println(">>>apply configuration from config admin");
            @SuppressWarnings("rawtypes")
            Enumeration keys = properties.keys();
            while(keys.hasMoreElements()) {
                String key = (String)keys.nextElement();
                System.out.println(key+"="+properties.get(key));
            }
            System.out.println("<<<done!");
            
            try {
                Configuration config = ((ConfigurationAdmin)configAdminServiceTracker.getService()).getConfiguration(METATYPE_BUNDLE_PID);
                // OK, we can save the new configuration
                
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        
        //Try to read metatype service configurations
        
        MetaTypeService mts = (MetaTypeService) metaTypeServiceTracker.getService();

        Bundle[] bundles = context.getBundles();
        for (int i = 0; i < bundles.length; i++) {

            // Getting the org.osgi.service.metatype.MetaTypeInformation object
            // for all available bundles.
            MetaTypeInformation mti = mts.getMetaTypeInformation(bundles[i]);

            // Obtaining the available FPIDs and PIDs for each bundle
            String[] factorypids = mti.getFactoryPids();
            String[] pids = mti.getPids();

            // Retrieving the Object Class Definitions and Attribute Definitions for
            // bundles with PIDs
            if (pids != null) {
                for (int pid = 0; pid < pids.length; pid++) {
                    ObjectClassDefinition ocd = mti.getObjectClassDefinition(pids[pid], null);
                    AttributeDefinition[] ads = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);

                    // Printing the available Object Class Definitions and
                    // Attribute Definitions to the system output
                    for (int j = 0; j < ads.length; j++) {
                        System.out.println("AD= " + ads[j].getName() + " OCD= " + ocd.getName());
                    }
                }

                // Extracting ObjectClassDefinition and AttributeDefinition objects for bundles
                // with FPIDs
            }
            if (factorypids != null) {
                for (int fpid = 0; fpid < factorypids.length; fpid++) {
                    ObjectClassDefinition ocdfactory = mti.getObjectClassDefinition(factorypids[fpid], null);
                    AttributeDefinition[] adsfactory = ocdfactory.getAttributeDefinitions(ObjectClassDefinition.ALL);

                    // Printing all Object Class Definitions and Attribute Definitions
                    // for the available bundles to the system output
                    for (int k = 0; k < adsfactory.length; k++) {
                        System.out.println("AD= " + adsfactory[k].getName() + " OCD= " + ocdfactory.getName());
                    }
                }
            }
        }

    }
}
