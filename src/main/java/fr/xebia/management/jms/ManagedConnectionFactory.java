/*
 * Copyright 2008-2009 Xebia and the original author or authors.
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
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

/**
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class ManagedConnectionFactory implements ConnectionFactory, ManagedConnectionFactoryMBean {

    private ConnectionFactory delegate;

    private final JmsStatistics statistics = new JmsStatistics();

    public Connection createConnection() throws JMSException {
        try {
            return new ManagedConnection(delegate.createConnection(), statistics);
        } catch (JMSException e) {
            statistics.incrementCreateConnectionExceptionCount();
            throw e;
        } catch (RuntimeException e) {
            statistics.incrementCreateConnectionExceptionCount();
            throw e;
        } finally {
            statistics.incrementCreatedConnectionCount();
        }
    }

    public Connection createConnection(String userName, String password) throws JMSException {
        try {
            return new ManagedConnection(delegate.createConnection(userName, password), statistics);
        } catch (JMSException e) {
            statistics.incrementCreateConnectionExceptionCount();
            throw e;
        } catch (RuntimeException e) {
            statistics.incrementCreateConnectionExceptionCount();
            throw e;
        } finally {
            statistics.incrementCreatedConnectionCount();
        }
    }

    public int getCreateConnectionExceptionCount() {
        return statistics.getCreateConnectionExceptionCount();
    }

    public int getCreatedConnectionCount() {
        return statistics.getCreatedConnectionCount();
    }

    public int getCreatedMessageProducerCount() {
        return statistics.getCreatedMessageProducerCount();
    }

    public int getCreatedSessionCount() {
        return statistics.getCreatedSessionCount();
    }

    public int getCreateMessageProducerExceptionCount() {
        return statistics.getCreateMessageProducerExceptionCount();
    }

    public int getCreateSessionExceptionCount() {
        return statistics.getCreateSessionExceptionCount();
    }

    public ConnectionFactory getDelegate() {
        return delegate;
    }

    public int getReceivedMessageCount() {
        return statistics.getReceivedMessageCount();
    }

    public int getReceivedMessageExceptionCount() {
        return statistics.getReceivedMessageExceptionCount();
    }

    public long getReceiveMessageDurationInMillis() {
        return statistics.getReceiveMessageDurationInMillis();
    }

    public long getSendMessageDurationInMillis() {
        return statistics.getSendMessageDurationInMillis();
    }

    public int getSentMessageCount() {
        return statistics.getSentMessageCount();
    }

    public int getSentMessageExceptionCount() {
        return statistics.getSentMessageExceptionCount();
    }

    public void setDelegate(ConnectionFactory delegate) {
        this.delegate = delegate;
    }
}
