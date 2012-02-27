package org.example.complex.internal;

import org.example.simple.ExampleService;

/**
 * Internal implementation of our example OSGi service
 */
public final class ComplexServiceImpl
    implements ExampleService
{
    // implementation methods go here...

    public String scramble( String text )
    {
        return new String( "Complex," + text );
    }
}

