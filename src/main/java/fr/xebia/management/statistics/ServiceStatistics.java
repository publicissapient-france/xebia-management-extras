/*
 * Copyright 2008-2010 Xebia and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.xebia.management.statistics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedMetric;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.naming.SelfNaming;
import org.springframework.jmx.support.MetricType;

@ManagedResource
public class ServiceStatistics implements SelfNaming {

    public static boolean containsThrowableOfType(Throwable throwable, Class<?>... throwableTypes) {
        List<Throwable> alreadyProcessedThrowables = new ArrayList<Throwable>();
        while (true) {
            if (throwable == null) {
                // end of the list of causes
                return false;
            } else if (alreadyProcessedThrowables.contains(throwable)) {
                // infinite loop in causes
                return false;
            } else {
                for (Class<?> throwableType : throwableTypes) {
                    if (throwableType.isAssignableFrom(throwable.getClass())) {
                        return true;
                    }
                }
                alreadyProcessedThrowables.add(throwable);
                throwable = throwable.getCause();
            }
        }
    }

    private final AtomicInteger businessExceptionCounter = new AtomicInteger();

    private final Class<?>[] businessExceptionsTypes;

    private final AtomicInteger communicationExceptionCounter = new AtomicInteger();

    private final Class<?>[] communicationExceptionsTypes;

    private final AtomicInteger currentActiveCounter = new AtomicInteger();

    private final AtomicInteger invocationCounter = new AtomicInteger();

    private final String name;

    private final AtomicInteger otherExceptionCounter = new AtomicInteger();

    private final AtomicInteger slowInvocationCounter = new AtomicInteger();

    private long slowInvocationThresholdInNanos;

    private final AtomicLong totalDurationInNanosCounter = new AtomicLong();

    private final AtomicInteger verySlowInvocationCounter = new AtomicInteger();

    private long verySlowInvocationThresholdInNanos;

    public ServiceStatistics(String name) {
        this(name, new Class<?>[] {}, new Class<?>[] { IOException.class });
    }

    public ServiceStatistics(String name, Class<?>[] businessExceptionsTypes, Class<?>[] communicationExceptionsTypes) {
        super();
        this.name = name;
        this.businessExceptionsTypes = businessExceptionsTypes;
        this.communicationExceptionsTypes = communicationExceptionsTypes;
    }

    public void decrementCurrentActiveCount() {
        currentActiveCounter.decrementAndGet();
    }

    @ManagedMetric(description = "Number of business exceptions", metricType = MetricType.COUNTER, category = "throughput")
    public int getBusinessExceptionCount() {
        return businessExceptionCounter.get();
    }

    public AtomicInteger getBusinessExceptionCounter() {
        return businessExceptionCounter;
    }

    @ManagedMetric(description = "Number of communication exceptions (timeout, connection refused, etc)", metricType = MetricType.COUNTER, category = "throughput")
    public int getCommunicationExceptionCount() {
        return communicationExceptionCounter.get();
    }

    public AtomicInteger getCommunicationExceptionCounter() {
        return communicationExceptionCounter;
    }

    @ManagedMetric(description = "Number of currently active invocations", metricType = MetricType.GAUGE, category = "utilization")
    public int getCurrentActive() {
        return currentActiveCounter.get();
    }

    public AtomicInteger getCurrentActiveCounter() {
        return currentActiveCounter;
    }

    @ManagedMetric(description = "Number of invocations", metricType = MetricType.COUNTER, category = "throughput")
    public int getInvocationCount() {
        return invocationCounter.get();
    }

    public AtomicInteger getInvocationCounter() {
        return invocationCounter;
    }

    public String getName() {
        return name;
    }

    public ObjectName getObjectName() throws MalformedObjectNameException {
        return new ObjectName("fr.xebia:type=RemoteServiceStatistics,name=" + ObjectName.quote(this.name));
    }

    @ManagedMetric(description = "Number of non business exceptions excluding communication exceptions", metricType = MetricType.COUNTER, category = "throughput")
    public int getOtherExceptionCount() {
        return otherExceptionCounter.get();
    }

    public AtomicInteger getotherExceptionCounter() {
        return otherExceptionCounter;
    }

    public long getSlowInvocationThresholdInNanos() {
        return slowInvocationThresholdInNanos;
    }

    @ManagedAttribute(description = "Total durations in millis of the invocations")
    public long getTotalDurationInMillis() {
        return TimeUnit.MILLISECONDS.convert(getTotalDurationInNanos(), TimeUnit.NANOSECONDS);
    }

    @ManagedMetric(description = "Total durations in nanos of the invocations", metricType = MetricType.COUNTER, unit = "ns", category = "throughput")
    public long getTotalDurationInNanos() {
        return totalDurationInNanosCounter.get();
    }

    public AtomicLong getTotalDurationInNanosCounter() {
        return totalDurationInNanosCounter;
    }

    public long getVerySlowInvocationThresholdInNanos() {
        return verySlowInvocationThresholdInNanos;
    }

    /**
     * Increment {@link #communicationExceptionCounter}.
     */
    public void incrementBusinessExceptionCount() {
        communicationExceptionCounter.incrementAndGet();
    }

    /**
     * Increment {@link #businessExceptionCounter}.
     */
    public void incrementCommunicationExceptionCount() {
        businessExceptionCounter.incrementAndGet();
    }

    /**
     * <p>
     * Increment {@link #currentActiveCounter}. Pattern :
     * </p>
     * <code><pre>
     * statistics.incrementCurrentActiveCount();
     * try {
     *    ...
     * } finally {
     *    decrementCurrentActiveCount();
     * }
     * <pre></code>
     * 
     * @see #decrementCurrentActiveCount()
     */
    public void incrementCurrentActiveCount() {
        currentActiveCounter.incrementAndGet();
    }

    /**
     * Increment the {@link #communicationExceptionCounter} if the given
     * throwable or one of its cause is an instance of {@link IOException} ;
     * otherwise, increment {@link #otherExceptionCounter}.
     */
    public void incrementExceptionCount(Throwable throwable) {

        if (containsThrowableOfType(throwable, communicationExceptionsTypes)) {
            communicationExceptionCounter.incrementAndGet();
        } else if (containsThrowableOfType(throwable, businessExceptionsTypes)) {
            businessExceptionCounter.incrementAndGet();
        } else {
            otherExceptionCounter.incrementAndGet();
        }
    }

    /**
     * Increment {@link #invocationCounter}.
     */
    public void incrementInvocationCount() {
        invocationCounter.incrementAndGet();
    }

    /**
     * Increment {@link #totalDurationInNanosCounter},
     * {@link #invocationCounter} and, if eligible,
     * {@link #verySlowInvocationCounter} or {@link #slowInvocationCounter}.
     * 
     * @param deltaInNanos
     *            delta in nanos
     */
    public void incrementInvocationCounterAndTotalDurationWithNanos(long deltaInNanos) {

        totalDurationInNanosCounter.addAndGet(deltaInNanos);

        invocationCounter.incrementAndGet();

        if (deltaInNanos >= this.verySlowInvocationThresholdInNanos) {
            this.verySlowInvocationCounter.incrementAndGet();
        } else if (deltaInNanos >= this.slowInvocationThresholdInNanos) {
            this.slowInvocationCounter.incrementAndGet();
        }
    }

    /**
     * Increment {@link #otherExceptionCounter}.
     */
    public void incrementOtherExceptionCount() {
        otherExceptionCounter.incrementAndGet();
    }

    /**
     * Increment {@link #totalDurationInNanosCounter}.
     * 
     * @param deltaInMillis
     *            delta in millis
     */
    public void incrementTotalDurationWithMillis(long deltaInMillis) {
        incrementTotalDurationWithNanos(TimeUnit.NANOSECONDS.convert(deltaInMillis, TimeUnit.MILLISECONDS));
    }

    /**
     * Increment {@link #totalDurationInNanosCounter}.
     * 
     * @param deltaInNanos
     *            delta in nanos
     */
    public void incrementTotalDurationWithNanos(long deltaInNanos) {
        totalDurationInNanosCounter.addAndGet(deltaInNanos);
    }

    public void setSlowInvocationThresholdInNanos(long slowInvocationThresholdInNanos) {
        this.slowInvocationThresholdInNanos = slowInvocationThresholdInNanos;
    }

    public void setVerySlowInvocationThresholdInNanos(long verySlowInvocationThresholdInNanos) {
        this.verySlowInvocationThresholdInNanos = verySlowInvocationThresholdInNanos;
    }

}
