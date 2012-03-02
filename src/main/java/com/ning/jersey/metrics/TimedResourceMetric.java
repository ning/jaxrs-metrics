package com.ning.jersey.metrics;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.core.Timer;

public class TimedResourceMetric 
{
    private final int defaultStatusCode;
    private final Map<Integer, Timer> metrics;

    @SuppressWarnings("deprecation")
    public TimedResourceMetric(final Future<MetricsRegistry> metricsRegistryFuture,
                               final Class<?> klass,
                               final String name,
                               final int defaultStatusCode,
                               final TimeUnit durationUnit,
                               final TimeUnit rateUnit)
    {
        this.defaultStatusCode = defaultStatusCode;
        this.metrics = new MapMaker().makeComputingMap(new Function<Integer, Timer>() {
            @Override
            public Timer apply(Integer input)
            {
                try
                {
                    return metricsRegistryFuture.get(1, TimeUnit.SECONDS).newTimer(klass, name + "-" + input, durationUnit, rateUnit);
                }
                catch (InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                    return null;
                }
                catch (TimeoutException ex)
                {
                    throw new IllegalStateException("Received requests during guice initialization", ex);
                }
                catch (ExecutionException ex)
                {
                    throw new IllegalStateException(ex);
                }
            }
        });
    }

    public void update(Integer statusCode, long duration, TimeUnit unit)
    {
        metrics.get(statusCode == null ? defaultStatusCode : statusCode.intValue()).update(duration, unit);
    }
}
