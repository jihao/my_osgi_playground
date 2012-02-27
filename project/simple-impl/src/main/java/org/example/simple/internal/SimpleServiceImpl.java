package org.example.simple.internal;

import org.example.simple.ExampleService;

/**
 * Internal implementation of our example OSGi service
 */
public final class SimpleServiceImpl
    implements ExampleService
{
    // implementation methods go here...

    public String scramble( String text )
    {
        return new String( "Simple:"+text );
    }
}

