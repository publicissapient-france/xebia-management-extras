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
package fr.xebia.jms.wrapper;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;

/**
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class ConnectionWrapper implements Connection {

    private final Connection delegate;

    public void close() throws JMSException {
        delegate.close();
    }

    public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector, ServerSessionPool sessionPool,
            int maxMessages) throws JMSException {
        return delegate.createConnectionConsumer(destination, messageSelector, sessionPool, maxMessages);
    }

    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector,
            ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        return delegate.createDurableConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool, maxMessages);
    }

    public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
        return delegate.createSession(transacted, acknowledgeMode);
    }

    public String getClientID() throws JMSException {
        return delegate.getClientID();
    }

    public ExceptionListener getExceptionListener() throws JMSException {
        return delegate.getExceptionListener();
    }

    public ConnectionMetaData getMetaData() throws JMSException {
        return delegate.getMetaData();
    }

    public void setClientID(String clientID) throws JMSException {
        delegate.setClientID(clientID);
    }

    public void setExceptionListener(ExceptionListener listener) throws JMSException {
        delegate.setExceptionListener(listener);
    }

    public void start() throws JMSException {
        delegate.start();
    }

    public void stop() throws JMSException {
        delegate.stop();
    }

    public ConnectionWrapper(Connection delegate) {
        super();
        this.delegate = delegate;
    }
}
