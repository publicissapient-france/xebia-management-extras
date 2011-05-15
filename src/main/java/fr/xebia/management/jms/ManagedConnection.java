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
package fr.xebia.management.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.springframework.jmx.JmxException;

import fr.xebia.jms.wrapper.ConnectionWrapper;
import fr.xebia.management.jms.ManagedConnectionFactory.Statistics;

/**
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class ManagedConnection extends ConnectionWrapper {

    private final Statistics statistics;

    public ManagedConnection(Connection delegate, Statistics statistics) {
        super(delegate);
        this.statistics = statistics;
    }
    
    @Override
    public void close() throws JMSException {
        try {
           super.close();
        } finally {
            statistics.incrementCloseConnectionCount();
        }
    }

    @Override
    public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
        try {
            return new ManagedSession(super.createSession(transacted, acknowledgeMode), statistics);
        } catch (JmxException e) {
            statistics.incrementCreateSessionExceptionCount();
            throw e;
        } catch (RuntimeException e) {
            statistics.incrementCreateSessionExceptionCount();
            throw e;
        } finally {
            statistics.incrementCreateSessionCount();
        }
    }
}
