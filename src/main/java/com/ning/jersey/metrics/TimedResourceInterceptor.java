package com.ning.jersey.metrics;

import java.util.concurrent.TimeUnit;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * A method interceptor which times the execution of the annotated resource method.
 */
public class TimedResourceInterceptor implements MethodInterceptor {
    private final TimedResourceMetric timer;

    public TimedResourceInterceptor(TimedResourceMetric timer) {
        this.timer = timer;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        final long startTime = System.nanoTime();
        // two ways to get the response code:
        // * response object is returned
        // * exception is thrown
        // * default response code (not sure how to get that ...)
        Integer status = null; 

        try {
            Object result = invocation.proceed();

            if (result instanceof Response) {
                status = ((Response)result).getStatus();
            }
            return result;
        }
        catch (WebApplicationException ex) {
            status = ex.getResponse().getStatus();
            throw ex;
        }
        finally {
            timer.update(status, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        }
    }
}
