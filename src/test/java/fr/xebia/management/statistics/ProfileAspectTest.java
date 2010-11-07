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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:fr/xebia/management/statistics/test-spring-context.xml")
public class ProfileAspectTest {

    public static class TestService {
        @Profiled
        public void doJob() {

        }
    }

    @Autowired
    protected TestService testService;

    @Autowired
    protected ProfileAspect profileAspect;

    @Test
    public void test() throws Exception {
        testService.doJob();

        assertEquals(1, profileAspect.getRegisteredServiceStatisticsCount());
        
        // don't use getClass().getName() because the class is enhanced by CGLib 
        // and its name looks like "...ProfileAspectTest$TestService$$EnhancerByCGLIB$$9b64fd54"
        String name = "fr.xebia.management.statistics.ProfileAspectTest$TestService.doJob";

        ServiceStatistics serviceStatistics = profileAspect.getServiceStatisticsByName().get(name);
        assertNotNull(serviceStatistics);
        assertEquals(1, serviceStatistics.getInvocationCount());
        assertEquals(0, serviceStatistics.getBusinessExceptionCount());
        assertEquals(0, serviceStatistics.getCommunicationExceptionCount());
        assertEquals(0, serviceStatistics.getOtherExceptionCount());
    }
}
