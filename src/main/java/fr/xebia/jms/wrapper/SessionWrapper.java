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


import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

/**
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class SessionWrapper implements Session {

    private final Session delegate;

    public void close() throws JMSException {
        delegate.close();
    }

    public void commit() throws JMSException {
        delegate.commit();
    }

    public QueueBrowser createBrowser(Queue queue, String messageSelector) throws JMSException {
        return delegate.createBrowser(queue, messageSelector);
    }

    public QueueBrowser createBrowser(Queue queue) throws JMSException {
        return delegate.createBrowser(queue);
    }

    public BytesMessage createBytesMessage() throws JMSException {
        return delegate.createBytesMessage();
    }

    public MessageConsumer createConsumer(Destination destination, String messageSelector, boolean NoLocal) throws JMSException {
        return delegate.createConsumer(destination, messageSelector, NoLocal);
    }

    public MessageConsumer createConsumer(Destination destination, String messageSelector) throws JMSException {
        return delegate.createConsumer(destination, messageSelector);
    }

    public MessageConsumer createConsumer(Destination destination) throws JMSException {
        return delegate.createConsumer(destination);
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException {
        return delegate.createDurableSubscriber(topic, name, messageSelector, noLocal);
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
        return delegate.createDurableSubscriber(topic, name);
    }

    public MapMessage createMapMessage() throws JMSException {
        return delegate.createMapMessage();
    }

    public Message createMessage() throws JMSException {
        return delegate.createMessage();
    }

    public ObjectMessage createObjectMessage() throws JMSException {
        return delegate.createObjectMessage();
    }

    public ObjectMessage createObjectMessage(Serializable object) throws JMSException {
        return delegate.createObjectMessage(object);
    }

    public MessageProducer createProducer(Destination destination) throws JMSException {
        return delegate.createProducer(destination);
    }

    public Queue createQueue(String queueName) throws JMSException {
        return delegate.createQueue(queueName);
    }

    public StreamMessage createStreamMessage() throws JMSException {
        return delegate.createStreamMessage();
    }

    public TemporaryQueue createTemporaryQueue() throws JMSException {
        return delegate.createTemporaryQueue();
    }

    public TemporaryTopic createTemporaryTopic() throws JMSException {
        return delegate.createTemporaryTopic();
    }

    public TextMessage createTextMessage() throws JMSException {
        return delegate.createTextMessage();
    }

    public TextMessage createTextMessage(String text) throws JMSException {
        return delegate.createTextMessage(text);
    }

    public Topic createTopic(String topicName) throws JMSException {
        return delegate.createTopic(topicName);
    }

    public int getAcknowledgeMode() throws JMSException {
        return delegate.getAcknowledgeMode();
    }

    public MessageListener getMessageListener() throws JMSException {
        return delegate.getMessageListener();
    }

    public boolean getTransacted() throws JMSException {
        return delegate.getTransacted();
    }

    public void recover() throws JMSException {
        delegate.recover();
    }

    public void rollback() throws JMSException {
        delegate.rollback();
    }

    public void run() {
        delegate.run();
    }

    public void setMessageListener(MessageListener listener) throws JMSException {
        delegate.setMessageListener(listener);
    }

    public void unsubscribe(String name) throws JMSException {
        delegate.unsubscribe(name);
    }

    public SessionWrapper(Session delegate) {
        super();
        this.delegate = delegate;
    }

}
