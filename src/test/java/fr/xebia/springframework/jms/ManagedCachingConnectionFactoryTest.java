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
package fr.xebia.springframework.jms;

import static junit.framework.Assert.*;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.springframework.jms.support.JmsUtils;

import fr.xebia.management.jms.ManagedConnectionFactory;

public class ManagedCachingConnectionFactoryTest {

    @Test
    public void testMessageConsumer() throws Exception {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(
                "vm://localhost?broker.persistent=false&broker.useJmx=true");
        ManagedConnectionFactory connectionFactory = new ManagedConnectionFactory(activeMQConnectionFactory);
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        try {
            connection = connectionFactory.createConnection();
            assertEquals(1, connectionFactory.getActiveConnectionCount());
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            assertEquals(1, connectionFactory.getActiveSessionCount());
            Destination myQueue = session.createQueue("test-queue");
            consumer = session.createConsumer(myQueue);

            assertEquals(1, connectionFactory.getActiveMessageConsumerCount());

            consumer.receiveNoWait();

            assertEquals(0, connectionFactory.getActiveMessageProducerCount());
        } finally {
            JmsUtils.closeMessageConsumer(consumer);
            assertEquals(0, connectionFactory.getActiveMessageConsumerCount());
            JmsUtils.closeSession(session);
            assertEquals(0, connectionFactory.getActiveSessionCount());
            JmsUtils.closeConnection(connection);
            assertEquals(0, connectionFactory.getActiveConnectionCount());
        }
    }
    @Test
    public void testMessageProducer() throws Exception {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(
                "vm://localhost?broker.persistent=false&broker.useJmx=true");
        ManagedConnectionFactory connectionFactory = new ManagedConnectionFactory(activeMQConnectionFactory);
        Connection connection = null;
        Session session = null;
        MessageProducer messageProducer = null;
        try {
            connection = connectionFactory.createConnection();
            assertEquals(1, connectionFactory.getActiveConnectionCount());
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            assertEquals(1, connectionFactory.getActiveSessionCount());
            Destination myQueue = session.createQueue("test-queue");
            messageProducer = session.createProducer(myQueue);

            assertEquals(1, connectionFactory.getActiveMessageProducerCount());

            messageProducer.send(myQueue, session.createTextMessage("test"));

            assertEquals(0, connectionFactory.getActiveMessageConsumerCount());
        } finally {
            JmsUtils.closeMessageProducer(messageProducer);
            assertEquals(0, connectionFactory.getActiveMessageProducerCount());
            JmsUtils.closeSession(session);
            assertEquals(0, connectionFactory.getActiveSessionCount());
            JmsUtils.closeConnection(connection);
            assertEquals(0, connectionFactory.getActiveConnectionCount());
        }
    }

}
