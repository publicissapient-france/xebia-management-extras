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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.naming.SelfNaming;
import org.springframework.util.StringUtils;

@ManagedResource
@Aspect
public class ProfileAspect implements BeanNameAware, SelfNaming {

    private MBeanExporter mbeanExporter;

    private String name;

    private ObjectName objectName;

    private ConcurrentMap<String, ServiceStatistics> serviceStatisticsByName = new ConcurrentHashMap<String, ServiceStatistics>();

    public MBeanExporter getMbeanExporter() {
        return mbeanExporter;
    }

    public ObjectName getObjectName() throws MalformedObjectNameException {
        if (objectName == null) {
            String objectNameAsString = "fr.xebia:type=ProfileAspect";
            if (StringUtils.hasLength(name)) {
                objectNameAsString += ",name=" + ObjectName.quote(name);
            }
            objectName = new ObjectName(objectNameAsString);
        }
        return objectName;
    }

    @ManagedAttribute
    public int getRegisteredServiceStatisticsCount() {
        return this.serviceStatisticsByName.size();
    }

    /**
     * Visible for tests
     */
    protected ConcurrentMap<String, ServiceStatistics> getServiceStatisticsByName() {
        return serviceStatisticsByName;
    }

    @Around(value = "execution(* *(..)) && @annotation(profiled)", argNames = "pjp,profiled")
    public Object profileInvocation(ProceedingJoinPoint pjp, Profiled profiled) throws Throwable {

        String name;
        if (StringUtils.hasLength(profiled.name())) {
            name = profiled.name();
        } else {
            String methodName = pjp.getStaticPart().getSignature().getName();
            String className = pjp.getStaticPart().getSignature().getDeclaringTypeName();
            name = className + "." + methodName;
        }
        ServiceStatistics serviceStatistics = serviceStatisticsByName.get(name);
        if (serviceStatistics == null) {

            ServiceStatistics newServiceStatistics = new ServiceStatistics(name, profiled.businessExceptionsTypes(),
                    profiled.communicationExceptionsTypes());
            newServiceStatistics.setSlowInvocationThresholdInNanos(profiled.slowInvocationThresholdInMillis());
            newServiceStatistics.setVerySlowInvocationThresholdInNanos(profiled.veryInvocationThresholdInMillis());

            ServiceStatistics previousServiceStatistics = serviceStatisticsByName.putIfAbsent(name, newServiceStatistics);

            if (previousServiceStatistics == null) {
                serviceStatistics = newServiceStatistics;
                /*
                 * Don't call {@link
                 * MBeanExporter#registerManagedResource(Object)} method which
                 * by default appends a unique identifier attribute to the
                 * ObjectName.
                 */
                mbeanExporter.registerManagedResource(serviceStatistics, serviceStatistics.getObjectName());
            } else {
                serviceStatistics = previousServiceStatistics;
            }
        }

        long nanosBefore = System.nanoTime();
        serviceStatistics.incrementCurrentActiveCount();
        try {
            Object returned = pjp.proceed();
            return returned;
        } catch (Throwable t) {
            serviceStatistics.incrementExceptionCount(t);
            throw t;
        } finally {
            serviceStatistics.decrementCurrentActiveCount();
            serviceStatistics.incrementInvocationCounterAndTotalDurationWithNanos(System.nanoTime() - nanosBefore);
        }
    }

    public void setBeanName(String name) {
        this.name = name;
    }

    public void setMbeanExporter(MBeanExporter mbeanExporter) {
        this.mbeanExporter = mbeanExporter;
    }
}
