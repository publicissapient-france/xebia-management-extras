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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class JmsStatistics implements ManagedConnectionFactoryMBean {

    private final AtomicInteger createConnectionExceptionCount = new AtomicInteger();
    
    private final AtomicInteger createdConnectionCount = new AtomicInteger();

    private final AtomicInteger createdMessageProducerCount = new AtomicInteger();

    private final AtomicInteger createdSessionCount = new AtomicInteger();

    private final AtomicInteger createMessageProducerExceptionCount = new AtomicInteger();

    private final AtomicInteger createSessionExceptionCount = new AtomicInteger();

    private final AtomicInteger receivedMessageCount = new AtomicInteger();

    private final AtomicInteger receivedMessageExceptionCount = new AtomicInteger();

    private final AtomicLong receiveMessageDurationInMillis = new AtomicLong();
    private final AtomicLong sendMessageDurationInMillis = new AtomicLong();

    private final AtomicInteger sentMessageCount = new AtomicInteger();

    private final AtomicInteger sentMessageExceptionCount = new AtomicInteger();

    public int getCreateConnectionExceptionCount() {
        return createConnectionExceptionCount.get();
    }

    public int getCreatedConnectionCount() {
        return createdConnectionCount.get();
    }

    public int getCreatedMessageProducerCount() {
        return createdMessageProducerCount.get();
    }

    public int getCreatedSessionCount() {
        return createdSessionCount.get();
    }

    public int getCreateMessageProducerExceptionCount() {
        return createMessageProducerExceptionCount.get();
    }

    public int getCreateSessionExceptionCount() {
        return createSessionExceptionCount.get();
    }

    public int getReceivedMessageCount() {
        return receivedMessageCount.get();
    }

    public int getReceivedMessageExceptionCount() {
        return receivedMessageExceptionCount.get();
    }

    public long getReceiveMessageDurationInMillis() {
        return receiveMessageDurationInMillis.get();
    }

    public long getSendMessageDurationInMillis() {
        return sendMessageDurationInMillis.get();
    }

    public int getSentMessageCount() {
        return sentMessageCount.get();
    }

    public int getSentMessageExceptionCount() {
        return sentMessageExceptionCount.get();
    }

    public void incrementCreateConnectionExceptionCount() {
        createConnectionExceptionCount.incrementAndGet();
    }

    public void incrementCreatedConnectionCount() {
        createdConnectionCount.incrementAndGet();
    }

    public void incrementCreatedMessageProducerCount() {
        createdMessageProducerCount.incrementAndGet();
    }

    public void incrementCreatedSessionCount() {
        createdSessionCount.incrementAndGet();
    }

    public void incrementCreateMessageProducerExceptionCount() {
        createMessageProducerExceptionCount.incrementAndGet();
    }

    public void incrementCreateSessionExceptionCount() {
        createSessionExceptionCount.incrementAndGet();
    }

    public void incrementReceivedMessageCount() {
        receivedMessageCount.incrementAndGet();
    }

    public void incrementReceivedMessageExceptionCount() {
        receivedMessageExceptionCount.incrementAndGet();
    }

    public void incrementReceiveMessageDurationInMillis(long delta) {
        receiveMessageDurationInMillis.addAndGet(delta);
    }

    public void incrementSendMessageDurationInMillis(long delta) {
        sendMessageDurationInMillis.addAndGet(delta);
    }

    public void incrementSentMessageCount() {
        sentMessageCount.incrementAndGet();
    }

    public void incrementSentMessageExceptionCount() {
        sentMessageExceptionCount.incrementAndGet();
    }

}
