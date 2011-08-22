package com.ning.jersey.metrics;

import java.lang.reflect.Method;
import com.google.common.util.concurrent.ValueFuture;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.yammer.metrics.core.MetricsRegistry;

/**
 * A listener which adds method interceptors to timed resource methods.
 */
public class TimedResourceListener implements TypeListener {
    private ValueFuture<MetricsRegistry> metricsRegistryFuture = ValueFuture.create();

    @Inject
    public void setMetricsRegistry(MetricsRegistry metricsRegistry) {
        metricsRegistryFuture.set(metricsRegistry);
    }

    @Override
    public <T> void hear(TypeLiteral<T> literal,
                         TypeEncounter<T> encounter) {
        for (Method method : literal.getRawType().getMethods()) {
            final TimedResource annotation = method.getAnnotation(TimedResource.class);
            if (annotation != null) {
                final String name = annotation.name().isEmpty() ? method.getName() : annotation.name();
                final TimedResourceMetric timer = new TimedResourceMetric(metricsRegistryFuture,
                                                                          literal.getRawType(),
                                                                          name,
                                                                          annotation.defaultStatusCode(),
                                                                          annotation.durationUnit(),
                                                                          annotation.rateUnit());
                encounter.bindInterceptor(Matchers.only(method), new TimedResourceInterceptor(timer));
            }
        }
    }
}
