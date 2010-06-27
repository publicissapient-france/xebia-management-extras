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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import fr.xebia.jms.wrapper.SessionWrapper;

/**
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class ManagedSession extends SessionWrapper {

    private final JmsStatistics statistics;

    public ManagedSession(Session delegate, JmsStatistics statistics) {
        super(delegate);
        this.statistics = statistics;
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
        return decorate(super.createDurableSubscriber(topic, name));
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException {
        return decorate(super.createDurableSubscriber(topic, name, messageSelector, noLocal));
    }

    @Override
    public MessageConsumer createConsumer(Destination destination) throws JMSException {
        return decorate(super.createConsumer(destination));
    }

    @Override
    public MessageConsumer createConsumer(Destination destination, String messageSelector) throws JMSException {
        return decorate(super.createConsumer(destination, messageSelector));
    }

    @Override
    public MessageConsumer createConsumer(Destination destination, String messageSelector, boolean NoLocal) throws JMSException {
        return decorate(super.createConsumer(destination, messageSelector, NoLocal));
    }

    @Override
    public MessageProducer createProducer(Destination destination) throws JMSException {
        return decorate(super.createProducer(destination));
    }

    protected MessageConsumer decorate(MessageConsumer messageConsumer) {
        return new ManagedMessageConsumer(messageConsumer, statistics);
    }

    protected TopicSubscriber decorate(TopicSubscriber topicSubscriber) {
        return new ManagedTopicSubscriber(topicSubscriber, statistics);
    }

    protected MessageProducer decorate(MessageProducer messageProducer) {
        return new ManagedMessageProducer(messageProducer, statistics);
    }

}
