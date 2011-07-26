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

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import static org.junit.Assert.*;

import org.junit.Test;

public class LeakDetectorSessionTest {

    @Test
    public void test_open_message_producers_list() throws Exception {

        Destination destination = mock(Destination.class);
        Session mockSession = mock(Session.class);
        MessageProducer mockMessageProducer = mock(MessageProducer.class);

        when(mockSession.createProducer(destination)).thenReturn(mockMessageProducer);
        LeakDetectorSession session = new LeakDetectorSession(mockSession, mock(LeakDetectorConnection.class));

        List<MessageProducer> createdProducers = new ArrayList<MessageProducer>();

        // create producers
        for (int i = 0; i < 10; i++) {
            MessageProducer producer = session.createProducer(destination);
            assertEquals(i + 1, session.getOpenMessageProducers().size());
            createdProducers.add(producer);
        }

        // close producers
        int closedProducers = 0;
        for (MessageProducer producer : createdProducers) {
            producer.close();
            closedProducers++;
            assertEquals(createdProducers.size() - closedProducers, session.getOpenMessageProducers().size());
        }
        session.close();
    }

    /**
     * test with {@link Session#createConsumer(Destination)}
     * 
     * @throws Exception
     */
    @Test
    public void test_open_message_consumers_list_with_simple_message_consumers() throws Exception {

        Destination destination = mock(Destination.class);
        Session mockSession = mock(Session.class);
        MessageConsumer mockMessageConsumer = mock(MessageConsumer.class);

        when(mockSession.createConsumer(destination)).thenReturn(mockMessageConsumer);
        LeakDetectorSession session = new LeakDetectorSession(mockSession, mock(LeakDetectorConnection.class));

        List<MessageConsumer> createdConsumers = new ArrayList<MessageConsumer>();

        // create consumers
        for (int i = 0; i < 10; i++) {
            MessageConsumer consumer = session.createConsumer(destination);
            assertEquals(i + 1, session.getOpenMessageConsumers().size());
            createdConsumers.add(consumer);
        }

        // close consumers
        int closedConsumers = 0;
        for (MessageConsumer consumer : createdConsumers) {
            consumer.close();
            closedConsumers++;
            assertEquals(createdConsumers.size() - closedConsumers, session.getOpenMessageConsumers().size());
        }
        
        session.close();

    }
    
    /**
     * test with {@link Session#createConsumer(Destination)}
     * 
     * @throws Exception
     */
    @Test
    public void test_open_message_consumers_list_with_selector_based_message_consumers() throws Exception {

        Destination destination = mock(Destination.class);
        Session mockSession = mock(Session.class);
        MessageConsumer mockMessageConsumer = mock(MessageConsumer.class);

        when(mockSession.createConsumer(eq(destination), anyString())).thenReturn(mockMessageConsumer);
        LeakDetectorSession session = new LeakDetectorSession(mockSession, mock(LeakDetectorConnection.class));

        List<MessageConsumer> createdConsumers = new ArrayList<MessageConsumer>();

        // create consumers
        for (int i = 0; i < 10; i++) {
            MessageConsumer consumer = session.createConsumer(destination,"JMSType = 'car' AND color = 'blue' AND weight > 2500");
            assertEquals(i + 1, session.getOpenMessageConsumers().size());
            createdConsumers.add(consumer);
        }

        // close consumers
        int closedConsumers = 0;
        for (MessageConsumer consumer : createdConsumers) {
            consumer.close();
            closedConsumers++;
            assertEquals(createdConsumers.size() - closedConsumers, session.getOpenMessageConsumers().size());
        }

        session.close();

    }
    
    /**
     * test with {@link Session#createConsumer(Destination)}
     * 
     * @throws Exception
     */
    @Test
    public void test_open_message_consumers_list_with_selector_based_no_local_message_consumers() throws Exception {

        Destination destination = mock(Destination.class);
        Session mockSession = mock(Session.class);
        MessageConsumer mockMessageConsumer = mock(MessageConsumer.class);

        when(mockSession.createConsumer(eq(destination), anyString(), eq(true))).thenReturn(mockMessageConsumer);
        LeakDetectorSession session = new LeakDetectorSession(mockSession, mock(LeakDetectorConnection.class));

        List<MessageConsumer> createdConsumers = new ArrayList<MessageConsumer>();

        // create consumers
        for (int i = 0; i < 10; i++) {
            MessageConsumer consumer = session.createConsumer(destination,"JMSType = 'car' AND color = 'blue' AND weight > 2500", true);
            assertEquals(i + 1, session.getOpenMessageConsumers().size());
            createdConsumers.add(consumer);
        }

        // close consumers
        int closedConsumers = 0;
        for (MessageConsumer consumer : createdConsumers) {
            consumer.close();
            closedConsumers++;
            assertEquals(createdConsumers.size() - closedConsumers, session.getOpenMessageConsumers().size());
        }
        
        session.close();

    }

    /**
     * test with {@link Session#createConsumer(Destination)}
     * 
     * @throws Exception
     */
    @Test
    public void test_open_message_consumers_list_with_topic_subscriber() throws Exception {

        Topic topic = mock(Topic.class);
        Session mockSession = mock(Session.class);
        TopicSubscriber mockTopicSubscriber = mock(TopicSubscriber.class);

        when(mockSession.createDurableSubscriber(eq(topic), anyString())).thenReturn(mockTopicSubscriber);
        LeakDetectorSession session = new LeakDetectorSession(mockSession, mock(LeakDetectorConnection.class));

        List<TopicSubscriber> createdTopicSubscribers = new ArrayList<TopicSubscriber>();

        // create consumers
        for (int i = 0; i < 10; i++) {
            TopicSubscriber topicSubscriber = session.createDurableSubscriber(topic, "subscriber-" + 1);
            assertEquals(i + 1, session.getOpenMessageConsumers().size());
            createdTopicSubscribers.add(topicSubscriber);
        }

        // close consumers
        int closedTopicSubscribers = 0;
        for (TopicSubscriber topicSubscriber : createdTopicSubscribers) {
            topicSubscriber.close();
            closedTopicSubscribers++;
            assertEquals(createdTopicSubscribers.size() - closedTopicSubscribers, session.getOpenMessageConsumers().size());
        }

        session.close();

    }

}
