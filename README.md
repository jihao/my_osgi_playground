# An OSGi environment for practise 

The environment was setup upon Maven + Felix, you can simplely start the Felix OSGi enviromnet with one command **mvn clean install pax:provision**, you can create a new bundle with one command **mvn pax:create-bundle "-DbundleName=new-bundle" "-Dpackage=org.example.newbundle" "-Dinternals=false"**

The existing code should not be used as reference implementation, it's only dummy code, bad practise.


# Start Eclipse as debug client
 * Create a Remote Java Application in Debug Configurations
 * Set "Connection Type" as "Startd(Socket Attach)"
 * Set your Debugger Server information( configured in your design environment) in "Connection Properties" - 127.0.0.1,7000
 * Set your Source Code in "Source" tab.
