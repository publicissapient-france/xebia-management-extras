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
package fr.xebia.management.jms.leak;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.xebia.jms.wrapper.SessionWrapper;

public class LeakDetectorSession extends SessionWrapper implements Session {

    private final CreationContext creationContext = new CreationContext();

    private final LeakDetectorConnection leakDetectorConnection;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private final Set<LeakDetectorMessageConsumer> openMessageConsumers = Collections
            .newSetFromMap(new ConcurrentHashMap<LeakDetectorMessageConsumer, Boolean>());

    private final Set<LeakDetectorMessageProducer> openMessageProducers = Collections
            .newSetFromMap(new ConcurrentHashMap<LeakDetectorMessageProducer, Boolean>());

    public LeakDetectorSession(Session delegate, LeakDetectorConnection leakDetectorConnection) {
        super(delegate);
        this.leakDetectorConnection = leakDetectorConnection;
        leakDetectorConnection.registerOpenSession(this);
    }

    @Override
    public void close() throws JMSException {
        if (!openMessageConsumers.isEmpty()) {
            logger.warn("session.close() is called on {} before closing {} message consumers:", this, openMessageConsumers.size());
            for (LeakDetectorMessageConsumer messageConsumer : this.openMessageConsumers) {
                logger.warn(messageConsumer.dumpCreationContext("   "));
            }
        }

        if (!openMessageProducers.isEmpty()) {
            logger.warn("session.close() is called on {} before closing {} message producers:", this, openMessageProducers.size());
            for (LeakDetectorMessageProducer messageProducer : this.openMessageProducers) {
                logger.warn(messageProducer.dumpCreationContext("   "));
            }
        }
        super.close();
        leakDetectorConnection.unregisterOpenSession(this);
    }

    @Override
    public MessageConsumer createConsumer(Destination destination) throws JMSException {
        return new LeakDetectorMessageConsumer(super.createConsumer(destination), this);
    }

    @Override
    public MessageConsumer createConsumer(Destination destination, String messageSelector) throws JMSException {
        return new LeakDetectorMessageConsumer(super.createConsumer(destination, messageSelector), this);
    }

    @Override
    public MessageConsumer createConsumer(Destination destination, String messageSelector, boolean noLocal) throws JMSException {
        return new LeakDetectorMessageConsumer(super.createConsumer(destination, messageSelector, noLocal), this);
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
        return new LeakDetectorTopicSubscriber(super.createDurableSubscriber(topic, name), this);
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException {
        return new LeakDetectorTopicSubscriber(super.createDurableSubscriber(topic, name, messageSelector, noLocal), this);
    }

    @Override
    public MessageProducer createProducer(Destination destination) throws JMSException {
        return new LeakDetectorMessageProducer(super.createProducer(destination), this);
    }

    public String dumpCreationContext(String offest) {
        return offest + delegate().toString() + " - " + creationContext.dumpContext(offest);
    }

    public Set<LeakDetectorMessageConsumer> getOpenMessageConsumers() {
        return Collections.unmodifiableSet(openMessageConsumers);
    }

    public Set<LeakDetectorMessageProducer> getOpenMessageProducers() {
        return Collections.unmodifiableSet(openMessageProducers);
    }

    public void registerOpenMessageConsumer(LeakDetectorMessageConsumer messageConsumer) {
        this.openMessageConsumers.add(messageConsumer);
    }

    public void registerOpenMessageProducer(LeakDetectorMessageProducer messageProducer) {
        this.openMessageProducers.add(messageProducer);
    }

    public void unregisterOpenMessageConsumer(LeakDetectorMessageConsumer messageConsumer) {
        this.openMessageConsumers.remove(messageConsumer);
    }

    public void unregisterOpenMessageProducer(LeakDetectorMessageProducer messageProducer) {
        this.openMessageProducers.remove(messageProducer);
    }

}
