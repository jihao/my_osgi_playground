# Demonstrate of using MetaType Service

Metatype sevice schema defines the structure or format of the configuration of its specified pid. 

Metatype service and ConfigAdmin service was meant to be used together.

# Example

	<?xml version="1.0" encoding="UTF-8" ?>
	<MetaData xmlns="http://www.osgi.org/xmlns/metatype/v1.1.0">
	    <OCD name="person" id="2.5.6.6" description="Person Record">
	        <AD name="sex" id="2.5.4.12" type="Integer" default="1">
	            <Option label="male" value="1" />
	            <Option label="Female" value="0" />
	        </AD>
	        <AD name="sn" id="2.5.4.4" type="Integer" required="false" />
	        <AD name="cn" id="2.5.4.3" type="String" required="false"  />
	        <AD name="seeAlso" id="2.5.4.34" type="String" cardinality="8"
	            default="http://www.google.com,http://www.yahoo.com" />
	        <AD name="telNumber" id="2.5.4.20" type="String" required="false" />
	    </OCD>
	    <Designate pid="com.example.addressbook">
	        <Object ocdref="2.5.6.6" />
	    </Designate>
	</MetaData>

With this Metatype schema configuration, when we config things using ConfigAdmin service with pid com.example.addressbook.

1. invalid keys **can** be stored 

		Configuration config = ((ConfigurationAdmin) configAdminServiceTracker.getService()).getConfiguration("com.example.addressbook");
		// the following configuration can't be saved to the file since it doesn't compliance with schema
		Properties p = new Properties();
		p.put("nothing", "empty");
		config.update(p);

2. using name(like "sn") as key, it can be stored, it has no relation with Metatype service

3. using id(like "2.5.4.4") as key, it refresh the metatype configuration value.
You can check felix web console for the effect

		Properties pp = new Properties();
		pp.put("nothing", "empty");
		pp.put("sn", "1234");
		pp.put("cn", "3456");
		pp.put("2.5.4.4", "4567");
		config.update(pp);

result:

		$ cd project/runner/felix/cache/runner/bundle21
		$ cat ./data/config/com/example/addressbook.config
			service.bundleLocation="file:bundles/my.example.project.metatypeservice_1.0.0.SN
			APSHOT.jar"
			service.pid="com.example.addressbook"
			nothing="empty"
			2.5.4.4="4567"
			sn="1234"
			cn="3456"


