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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import fr.xebia.management.jms.leak.LeakDetectorConnectionFactory;

/**
 * <p>
 * JMX MBean compatible wrapper of a {@link ConnectionFactory}.
 * </p>
 * <p>
 * Expose MBean attributes and methods via a standard xxxMBean interface rather
 * than with spring annotations to support usage without Spring
 * </p>
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class ManagedConnectionFactory implements ConnectionFactory, ManagedConnectionFactoryMBean {

    public static class Statistics {

        private final AtomicInteger closeConnectionCount = new AtomicInteger();

        private final AtomicInteger closeMessageConsumerCount = new AtomicInteger();

        private final AtomicInteger closeMessageProducerCount = new AtomicInteger();

        private final AtomicInteger closeSessionCount = new AtomicInteger();

        private final AtomicInteger createConnectionCount = new AtomicInteger();

        private final AtomicInteger createConnectionExceptionCount = new AtomicInteger();

        private final AtomicInteger createMessageConsumerCount = new AtomicInteger();

        private final AtomicInteger createMessageConsumerExceptionCount = new AtomicInteger();

        private final AtomicInteger createMessageProducerCount = new AtomicInteger();

        private final AtomicInteger createMessageProducerExceptionCount = new AtomicInteger();

        private final AtomicInteger createSessionCount = new AtomicInteger();

        private final AtomicInteger createSessionExceptionCount = new AtomicInteger();

        private final AtomicInteger receiveMessageCount = new AtomicInteger();

        private final AtomicLong receiveMessageDurationInMillis = new AtomicLong();

        private final AtomicInteger receiveMessageExceptionCount = new AtomicInteger();

        private final AtomicInteger sendMessageCount = new AtomicInteger();

        private final AtomicLong sendMessageDurationInMillis = new AtomicLong();

        private final AtomicInteger sendMessageExceptionCount = new AtomicInteger();

        public int getCloseConnectionCount() {
            return closeConnectionCount.get();
        }

        public int getCloseMessageConsumerCount() {
            return closeMessageConsumerCount.get();
        }

        public int getCloseMessageProducerCount() {
            return closeMessageProducerCount.get();
        }

        public int getCloseSessionCount() {
            return closeSessionCount.get();
        }

        public int getCreateConnectionCount() {
            return createConnectionCount.get();
        }

        public int getCreateConnectionExceptionCount() {
            return createConnectionExceptionCount.get();
        }

        public int getCreateMessageConsumerCount() {
            return createMessageConsumerCount.get();
        }

        public int getCreateMessageConsumerExceptionCount() {
            return createMessageConsumerExceptionCount.get();
        }

        public int getCreateMessageProducerCount() {
            return createMessageProducerCount.get();
        }

        public int getCreateMessageProducerExceptionCount() {
            return createMessageProducerExceptionCount.get();
        }

        public int getCreateSessionCount() {
            return createSessionCount.get();
        }

        public int getCreateSessionExceptionCount() {
            return createSessionExceptionCount.get();
        }

        public int getReceiveMessageCount() {
            return receiveMessageCount.get();
        }

        public long getReceiveMessageDurationInMillis() {
            return receiveMessageDurationInMillis.get();
        }

        public int getReceiveMessageExceptionCount() {
            return receiveMessageExceptionCount.get();
        }

        public int getSendMessageCount() {
            return sendMessageCount.get();
        }

        public long getSendMessageDurationInMillis() {
            return sendMessageDurationInMillis.get();
        }

        public int getSendMessageExceptionCount() {
            return sendMessageExceptionCount.get();
        }

        public void incrementCloseConnectionCount() {
            closeConnectionCount.incrementAndGet();
        }

        public void incrementCloseMessageConsumerCount() {
            closeMessageConsumerCount.incrementAndGet();
        }

        public void incrementCloseMessageProducerCount() {
            closeMessageProducerCount.incrementAndGet();
        }

        public void incrementCloseSessionCount() {
            closeSessionCount.incrementAndGet();
        }

        public void incrementCreateConnectionCount() {
            createConnectionCount.incrementAndGet();
        }

        public void incrementCreateConnectionExceptionCount() {
            createConnectionExceptionCount.incrementAndGet();
        }

        public void incrementCreateMessageConsumerCount() {
            createMessageConsumerCount.incrementAndGet();
        }

        public void incrementCreateMessageConsumerExceptionCount() {
            createMessageConsumerExceptionCount.incrementAndGet();
        }

        public void incrementCreateMessageProducerCount() {
            createMessageProducerCount.incrementAndGet();
        }

        public void incrementCreateMessageProducerExceptionCount() {
            createMessageProducerExceptionCount.incrementAndGet();
        }

        public void incrementCreateSessionCount() {
            createSessionCount.incrementAndGet();
        }

        public void incrementCreateSessionExceptionCount() {
            createSessionExceptionCount.incrementAndGet();
        }

        public void incrementReceivedMessageCount() {
            receiveMessageCount.incrementAndGet();
        }

        public void incrementReceivedMessageExceptionCount() {
            receiveMessageExceptionCount.incrementAndGet();
        }

        public void incrementReceiveMessageDurationInMillis(long delta) {
            receiveMessageDurationInMillis.addAndGet(delta);
        }

        public void incrementSendMessageCount() {
            sendMessageCount.incrementAndGet();
        }

        public void incrementSendMessageDurationInMillis(long delta) {
            sendMessageDurationInMillis.addAndGet(delta);
        }

        public void incrementSendMessageExceptionCount() {
            sendMessageExceptionCount.incrementAndGet();
        }

    }

    private ConnectionFactory connectionFactory;

    private final Statistics statistics = new Statistics();

    private boolean trackLeaks = false;

    public ManagedConnectionFactory() {
        super();
    }

    public ManagedConnectionFactory(ConnectionFactory connectionFactory) {
        super();
        this.connectionFactory = connectionFactory;
    }

    public Connection createConnection() throws JMSException {
        try {
            return new ManagedConnection(connectionFactory.createConnection(), statistics);
        } catch (JMSException e) {
            statistics.incrementCreateConnectionExceptionCount();
            throw e;
        } catch (RuntimeException e) {
            statistics.incrementCreateConnectionExceptionCount();
            throw e;
        } finally {
            statistics.incrementCreateConnectionCount();
        }
    }

    public Connection createConnection(String userName, String password) throws JMSException {
        try {
            return new ManagedConnection(connectionFactory.createConnection(userName, password), statistics);
        } catch (JMSException e) {
            statistics.incrementCreateConnectionExceptionCount();
            throw e;
        } catch (RuntimeException e) {
            statistics.incrementCreateConnectionExceptionCount();
            throw e;
        } finally {
            statistics.incrementCreateConnectionCount();
        }
    }

    public int getActiveConnectionCount() {
        return getCreateConnectionCount() - getCloseConnectionCount();
    }

    public int getActiveMessageConsumerCount() {
        return getCreateMessageConsumerCount() - getCloseMessageConsumerCount();
    }

    public int getActiveMessageProducerCount() {
        return getCreateMessageProducerCount() - getCloseMessageProducerCount();
    }

    public int getActiveSessionCount() {
        return getCreateSessionCount() - getCloseSessionCount();
    }

    public int getCloseConnectionCount() {
        return statistics.getCloseConnectionCount();
    }

    public int getCloseMessageConsumerCount() {
        return statistics.getCloseMessageConsumerCount();
    }

    public int getCloseMessageProducerCount() {
        return statistics.getCloseMessageProducerCount();
    }

    public int getCloseSessionCount() {
        return statistics.getCloseSessionCount();
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public int getCreateConnectionCount() {
        return statistics.getCreateConnectionCount();
    }

    public int getCreateConnectionExceptionCount() {
        return statistics.getCreateConnectionExceptionCount();
    }

    public int getCreateMessageConsumerCount() {
        return statistics.getCreateMessageConsumerCount();
    }

    public int getCreateMessageConsumerExceptionCount() {
        return statistics.getCreateMessageConsumerExceptionCount();
    }

    public int getCreateMessageProducerCount() {
        return statistics.getCreateMessageProducerCount();
    }

    public int getCreateMessageProducerExceptionCount() {
        return statistics.getCreateMessageProducerExceptionCount();
    }

    public int getCreateSessionCount() {
        return statistics.getCreateSessionCount();
    }

    public int getCreateSessionExceptionCount() {
        return statistics.getCreateSessionExceptionCount();
    }

    public int getReceiveMessageCount() {
        return statistics.getReceiveMessageCount();
    }

    public long getReceiveMessageDurationInMillis() {
        return statistics.getReceiveMessageDurationInMillis();
    }

    public int getReceiveMessageExceptionCount() {
        return statistics.getReceiveMessageExceptionCount();
    }

    public int getSendMessageCount() {
        return statistics.getSendMessageCount();
    }

    public long getSendMessageDurationInMillis() {
        return statistics.getSendMessageDurationInMillis();
    }

    public int getSendMessageExceptionCount() {
        return statistics.getSendMessageExceptionCount();
    }

    public boolean isTrackLeaks() {
        return trackLeaks;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        if (trackLeaks && !(connectionFactory instanceof LeakDetectorConnectionFactory)) {
            connectionFactory = new LeakDetectorConnectionFactory(connectionFactory);
        }
        this.connectionFactory = connectionFactory;
    }

    /**
     * @deprecated Use {@link #setConnectionFactory(ConnectionFactory)}.
     */
    @Deprecated
    public void setDelegate(ConnectionFactory delegate) {
        this.connectionFactory = delegate;
    }

    public void setTrackLeaks(boolean trackLeaks) {
        this.trackLeaks = trackLeaks;
        if (trackLeaks && connectionFactory != null && !(connectionFactory instanceof LeakDetectorConnectionFactory)) {
            connectionFactory = new LeakDetectorConnectionFactory(connectionFactory);
        }
    }

    public List<String> dumpAllOpenedResources() {
        if (connectionFactory instanceof LeakDetectorConnectionFactory) {
            return ((LeakDetectorConnectionFactory) connectionFactory).dumpAllOpenedResources();
        } else {
            return Collections.emptyList();
        }
    }
}
