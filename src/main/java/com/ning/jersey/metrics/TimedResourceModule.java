package com.ning.jersey.metrics;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class TimedResourceModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        TimedResourceListener listener = new TimedResourceListener();

        requestInjection(listener);
        bindListener(Matchers.any(), listener);
    }
}
