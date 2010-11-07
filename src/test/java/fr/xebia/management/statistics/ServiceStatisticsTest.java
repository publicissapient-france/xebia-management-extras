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

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;

import org.junit.Test;

public class ServiceStatisticsTest {

    public static class FirstTestingBusinessException extends Exception {

        private static final long serialVersionUID = 1L;

        public FirstTestingBusinessException() {
            super();
        }

        public FirstTestingBusinessException(Throwable cause) {
            super(cause);
        }
    }

    public static class SecondTestingBusinessException extends Exception {

        private static final long serialVersionUID = 1L;

        public SecondTestingBusinessException() {
            super();
        }

        public SecondTestingBusinessException(Throwable cause) {
            super(cause);
        }
    }

    @Test
    public void testContainsThrowableOfTypeFalse() {
        Exception aChildException = new SQLException("a child exception");
        Exception theParentException = new Exception("the parent exception", aChildException);
        boolean actual = ServiceStatistics.containsThrowableOfType(theParentException, RuntimeException.class, IOException.class);
        assertEquals(false, actual);
    }

    /**
     * one exception has a parent exception as cause
     */
    @Test
    public void testContainsThrowableOfTypeFalseWithInfiniteStack() {
        Exception aChildException = new SQLException("a child exception");
        Exception theParentException = new Exception("the parent exception", aChildException);

        aChildException.initCause(theParentException);

        boolean actual = ServiceStatistics.containsThrowableOfType(theParentException, RuntimeException.class, IOException.class);
        assertEquals(false, actual);
    }

    @Test
    public void testContainsThrowableOfTypeTrue() {
        Exception theSearchedException = new RuntimeException("the searched exception");
        Exception theParentException = new Exception("the parent exception", theSearchedException);
        boolean actual = ServiceStatistics.containsThrowableOfType(theParentException, RuntimeException.class, IOException.class);
        assertEquals(true, actual);
    }
    /**
     * one exception has a parent exception as cause
     */
    @Test
    public void testContainsThrowableOfTypeTrueWithInfiniteStack() {
        Exception theSearchedException = new RuntimeException("the searched exception");
        Exception theParentException = new Exception("the parent exception", theSearchedException);
        theSearchedException.initCause(theParentException);

        boolean actual = ServiceStatistics.containsThrowableOfType(theParentException, RuntimeException.class, IOException.class);
        assertEquals(true, actual);
    }

    @Test
    public void testIncrementExceptionCount() {
        ServiceStatistics serviceStatistics = new ServiceStatistics( //
                "test", //
                new Class[] { FirstTestingBusinessException.class, SecondTestingBusinessException.class }, //
                new Class[] { IOException.class });

        // two business exceptions
        serviceStatistics.incrementExceptionCount(new FirstTestingBusinessException());
        serviceStatistics.incrementExceptionCount(new SecondTestingBusinessException());

        // one communication exceptions
        serviceStatistics.incrementExceptionCount(new SocketTimeoutException());

        // three other exception
        serviceStatistics.incrementExceptionCount(new SQLException());
        serviceStatistics.incrementExceptionCount(new RuntimeException());
        serviceStatistics.incrementExceptionCount(new Exception());

        assertEquals(2, serviceStatistics.getBusinessExceptionCount());
        assertEquals(1, serviceStatistics.getCommunicationExceptionCount());
        assertEquals(3, serviceStatistics.getOtherExceptionCount());

    }

    @Test
    public void testIncrementExceptionCountWithOneBusinessExceptionCausedByaCommunicationException() {
        ServiceStatistics serviceStatistics = new ServiceStatistics( //
                "test", //
                new Class[] { FirstTestingBusinessException.class, SecondTestingBusinessException.class }, //
                new Class[] { IOException.class });

        serviceStatistics.incrementExceptionCount(new FirstTestingBusinessException(new SocketTimeoutException()));


        assertEquals(0, serviceStatistics.getBusinessExceptionCount());
        assertEquals(1, serviceStatistics.getCommunicationExceptionCount());
        assertEquals(0, serviceStatistics.getOtherExceptionCount());

    }
}
