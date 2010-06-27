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
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.jmx.export.naming.SelfNaming;

/**
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class ManagedConnectionFactory implements ConnectionFactory, ManagedConnectionFactoryMBean, SelfNaming, BeanNameAware {

    private ConnectionFactory delegate;

    private final JmsStatistics statistics = new JmsStatistics();

    private String beanName;

    @Override
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

    @Override
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

    @Override
    public int getCreateConnectionExceptionCount() {
        return statistics.getCreateConnectionExceptionCount();
    }

    @Override
    public int getCreatedConnectionCount() {
        return statistics.getCreatedConnectionCount();
    }

    @Override
    public int getCreatedSessionCount() {
        return statistics.getCreatedSessionCount();
    }

    @Override
    public int getCreateSessionExceptionCount() {
        return statistics.getCreateSessionExceptionCount();
    }

    public ConnectionFactory getDelegate() {
        return delegate;
    }

    @Override
    public int getReceivedMessageCount() {
        return statistics.getReceivedMessageCount();
    }

    @Override
    public int getReceivedMessageExceptionCount() {
        return statistics.getReceivedMessageExceptionCount();
    }

    @Override
    public long getReceiveMessageDurationInMillis() {
        return statistics.getReceiveMessageDurationInMillis();
    }

    @Override
    public long getSendMessageDurationInMillis() {
        return statistics.getSendMessageDurationInMillis();
    }

    @Override
    public int getSentMessageCount() {
        return statistics.getSentMessageCount();
    }

    @Override
    public int getSentMessageExceptionCount() {
        return statistics.getSentMessageExceptionCount();
    }

    public void setDelegate(ConnectionFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public ObjectName getObjectName() throws MalformedObjectNameException {
        return ObjectName.getInstance("javax.jms:type=ConnectionFactory,name=" + beanName);
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

}
