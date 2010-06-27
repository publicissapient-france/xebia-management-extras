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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

/**
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class MessageProducerWrapper implements MessageProducer {

    private final MessageProducer delegate;

    public MessageProducerWrapper(MessageProducer delegate) {
        super();
        this.delegate = delegate;
    }

    public void close() throws JMSException {
        delegate.close();
    }

    protected void beforeSend(Message message) throws JMSException {

    }

    public int getDeliveryMode() throws JMSException {
        return delegate.getDeliveryMode();
    }

    public Destination getDestination() throws JMSException {
        return delegate.getDestination();
    }

    public boolean getDisableMessageID() throws JMSException {
        return delegate.getDisableMessageID();
    }

    public boolean getDisableMessageTimestamp() throws JMSException {
        return delegate.getDisableMessageTimestamp();
    }

    public int getPriority() throws JMSException {
        return delegate.getPriority();
    }

    public long getTimeToLive() throws JMSException {
        return delegate.getTimeToLive();
    }

    public void send(Destination destination, Message message) throws JMSException {
        beforeSend(message);
        delegate.send(destination, message);
    }

    public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        beforeSend(message);
        delegate.send(destination, message, deliveryMode, priority, timeToLive);
    }

    public void send(Message message) throws JMSException {
        beforeSend(message);
        delegate.send(message);
    }

    public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        beforeSend(message);
        delegate.send(message, deliveryMode, priority, timeToLive);
    }

    public void setDeliveryMode(int deliveryMode) throws JMSException {
        delegate.setDeliveryMode(deliveryMode);
    }

    public void setDisableMessageID(boolean value) throws JMSException {
        delegate.setDisableMessageID(value);
    }

    public void setDisableMessageTimestamp(boolean value) throws JMSException {
        delegate.setDisableMessageTimestamp(value);
    }

    public void setPriority(int defaultPriority) throws JMSException {
        delegate.setPriority(defaultPriority);
    }

    public void setTimeToLive(long timeToLive) throws JMSException {
        delegate.setTimeToLive(timeToLive);
    }
}
