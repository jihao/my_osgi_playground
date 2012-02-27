# An OSGi environment for practise 

The environment was setup upon Maven + Felix, you can simplely start the Felix OSGi enviromnet with one command **mvn clean install pax:provision**, you can create a new bundle with one command **mvn pax:create-bundle "-DbundleName=new-bundle" "-Dpackage=org.example.newbundle" "-Dinternals=false"**

The existing code should not be used as reference implementation, it's only dummy code, bad practise.


