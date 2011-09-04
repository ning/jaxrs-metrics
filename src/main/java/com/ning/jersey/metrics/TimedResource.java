package com.ning.jersey.metrics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * An annotation for marking a JAX-RS resource method of a Guice-provided object as timed.
 * <p/>
 * Given a method like this:
 * <code><pre>
 *     &#64;TimedResource(name = "fancyName", rateUnit = TimeUnit.SECONDS, durationUnit = TimeUnit.MICROSECONDS)
 *     public String getStuff() &#123;
 *         return "Sir Captain " + name;
 *     &#125;
 * </pre></code>
 * <p/>
 * One timer for each response code for the defining class with the name {@code getStuff-[responseCode]}
 * will be created and each time the {@code #getStuff()} method is invoked, the
 * method's execution will be timed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TimedResource
{
    /**
     * The name of the timer.
     */
    String name() default "";

    /**
     * The default status code of the method.
     */
    int defaultStatusCode() default 200;

    /**
     * The time unit of the timer's rate.
     */
    TimeUnit rateUnit() default TimeUnit.SECONDS;

    /**
     * The time unit of the timer's duration.
     */
    TimeUnit durationUnit() default TimeUnit.MILLISECONDS;
}
