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

import static junit.framework.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.xebia.management.statistics.ProfileAspect.ClassNameStyle;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:fr/xebia/management/statistics/test-spring-context.xml")
public class ProfileAspectTest {

    public static class TestService {
        @Profiled(slowInvocationThresholdInMillis = 100, verySlowInvocationThresholdInMillis = 200)
        public void doJobWithDefaultName() {

        }

        @Profiled(name = "my-name")
        public void doJobWithStaticName() {

        }

        @Profiled(name = "my-name(#{args[0]}-#{args[1]})")
        public void doJobWithElName(String arg1, String arg2) {

        }

        @Profiled(name = "test-slow-invocation-threshold", slowInvocationThresholdInMillis = 50, verySlowInvocationThresholdInMillis = 100)
        public void testSlowInvocationThreshold() throws InterruptedException {
            Thread.sleep(75);
        }

        @Profiled(name = "test-very-slow-invocation-threshold", slowInvocationThresholdInMillis = 25, verySlowInvocationThresholdInMillis = 50)
        public void testVerySlowInvocationThreshold() throws InterruptedException {
            Thread.sleep(75);
        }
    }

    @Autowired
    protected TestService testService;

    @Autowired
    protected ProfileAspect profileAspect;

    @Test
    public void testProfiledAnnotationWithDefaultName() throws Exception {

        // test
        testService.doJobWithDefaultName();

        // verify

        // don't use getClass().getName() because the class is enhanced by CGLib
        // and its name looks like
        // "...ProfileAspectTest$TestService$$EnhancerByCGLIB$$9b64fd54"
        String name = "fr.xebia.management.statistics.ProfileAspectTest$TestService.doJobWithDefaultName";
        ServiceStatistics serviceStatistics = profileAspect.serviceStatisticsByName.get(name);
        assertNotNull(serviceStatistics);
        assertEquals(1, serviceStatistics.getInvocationCount());
        assertEquals(0, serviceStatistics.getSlowInvocationCount());
        assertEquals(0, serviceStatistics.getVerySlowInvocationCount());
        assertEquals(0, serviceStatistics.getBusinessExceptionCount());
        assertEquals(0, serviceStatistics.getCommunicationExceptionCount());
        assertEquals(0, serviceStatistics.getOtherExceptionCount());
    }

    @Test
    public void testProfiledAnnotationWithStaticName() throws Exception {

        // test
        testService.doJobWithStaticName();

        // verify
        String name = "my-name";
        ServiceStatistics serviceStatistics = profileAspect.serviceStatisticsByName.get(name);
        assertNotNull(serviceStatistics);
        assertEquals(1, serviceStatistics.getInvocationCount());
        assertEquals(0, serviceStatistics.getSlowInvocationCount());
        assertEquals(0, serviceStatistics.getVerySlowInvocationCount());
        assertEquals(0, serviceStatistics.getBusinessExceptionCount());
        assertEquals(0, serviceStatistics.getCommunicationExceptionCount());
        assertEquals(0, serviceStatistics.getOtherExceptionCount());
    }

    @Test
    public void testProfiledAnnotationWithElName() throws Exception {

        // test
        testService.doJobWithElName("foo", "bar");

        // verify

        String name = "my-name(foo-bar)";
        ServiceStatistics serviceStatistics = profileAspect.serviceStatisticsByName.get(name);
        assertNotNull(serviceStatistics);
        assertEquals(1, serviceStatistics.getInvocationCount());
        assertEquals(0, serviceStatistics.getSlowInvocationCount());
        assertEquals(0, serviceStatistics.getVerySlowInvocationCount());
        assertEquals(0, serviceStatistics.getBusinessExceptionCount());
        assertEquals(0, serviceStatistics.getCommunicationExceptionCount());
        assertEquals(0, serviceStatistics.getOtherExceptionCount());
    }

    @Test
    public void testSlowInvocationThreshold() throws Exception {

        // test
        testService.testSlowInvocationThreshold();

        // verify
        String name = "test-slow-invocation-threshold";
        ServiceStatistics serviceStatistics = profileAspect.serviceStatisticsByName.get(name);
        assertNotNull(serviceStatistics);
        assertNotNull(serviceStatistics);
        assertEquals(1, serviceStatistics.getInvocationCount());
        assertEquals(1, serviceStatistics.getSlowInvocationCount());
        assertEquals(0, serviceStatistics.getVerySlowInvocationCount());
        assertEquals(0, serviceStatistics.getBusinessExceptionCount());
        assertEquals(0, serviceStatistics.getCommunicationExceptionCount());
        assertEquals(0, serviceStatistics.getOtherExceptionCount());
    }

    @Test
    public void testVerySlowInvocationThreshold() throws Exception {

        // test
        testService.testVerySlowInvocationThreshold();

        // verify
        String name = "test-very-slow-invocation-threshold";
        ServiceStatistics serviceStatistics = profileAspect.serviceStatisticsByName.get(name);
        assertNotNull(serviceStatistics);
        assertNotNull(serviceStatistics);
        assertEquals(1, serviceStatistics.getInvocationCount());
        assertEquals(0, serviceStatistics.getSlowInvocationCount());
        assertEquals(1, serviceStatistics.getVerySlowInvocationCount());
        assertEquals(0, serviceStatistics.getBusinessExceptionCount());
        assertEquals(0, serviceStatistics.getCommunicationExceptionCount());
        assertEquals(0, serviceStatistics.getOtherExceptionCount());
    }

    @Test
    public void testGetClassNameCompactFullyQualifiedName() {
        String actual = ProfileAspect.getClassName("java.lang.String", ClassNameStyle.COMPACT_FULLY_QUALIFIED_NAME);
        assertEquals("j.l.String", actual);
    }

    @Test
    public void testGetClassNameFullyQualifiedName() {
        String actual = ProfileAspect.getClassName("java.lang.String", ClassNameStyle.FULLY_QUALIFIED_NAME);
        assertEquals("java.lang.String", actual);
    }

    @Test
    public void testGetClassNameShortName() {
        String actual = ProfileAspect.getClassName("java.lang.String", ClassNameStyle.SHORT_NAME);
        assertEquals("String", actual);
    }
}
