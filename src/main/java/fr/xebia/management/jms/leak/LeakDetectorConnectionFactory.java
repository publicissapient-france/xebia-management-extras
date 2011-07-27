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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import fr.xebia.jms.wrapper.ConnectionFactoryWrapper;

/**
 * <p>
 * {@link ConnectionFactory} wrapper
 * </p>
 * <p>
 * Keeps track of all the opened connection, session, message producer, message
 * consumer and topic subscribers.
 * </p>
 * <p>
 * If a resource is closed (i.e. call to <code>.close()</code>) before all its
 * sub resources are closed (e.g. session and message producers|consumers), a
 * warn message is emmitted with the details of the open reosurces
 * </p>
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class LeakDetectorConnectionFactory extends ConnectionFactoryWrapper implements ConnectionFactory {

    private final Set<LeakDetectorConnection> openConnections = Collections
            .newSetFromMap(new ConcurrentHashMap<LeakDetectorConnection, Boolean>());

    public LeakDetectorConnectionFactory(ConnectionFactory delegate) {
        super(delegate);
    }

    public Connection createConnection() throws JMSException {
        return new LeakDetectorConnection(super.createConnection(), this);
    }

    public Connection createConnection(String userName, String password) throws JMSException {
        return new LeakDetectorConnection(super.createConnection(userName, password), this);
    }

    public void registerOpenConnection(LeakDetectorConnection connection) {
        this.openConnections.add(connection);
    }

    public void unregisterOpenConnection(LeakDetectorConnection connection) {
        this.openConnections.remove(connection);
    }

    public Set<LeakDetectorConnection> getOpenConnections() {
        return Collections.unmodifiableSet(openConnections);
    }

    /**
     * List all the currently opened connection, session, message producer,
     * message consumer
     * 
     * @return
     */
    public List<String> dumpAllOpenedResources() {
        List<String> dumps = new ArrayList<String>();
        for (LeakDetectorConnection connection : openConnections) {
            dumps.add(connection.dumpCreationContext(""));
            for (LeakDetectorSession session : connection.getOpenSessions()) {
                dumps.add(session.dumpCreationContext("   "));
                for (LeakDetectorMessageProducer producer : session.getOpenMessageProducers()) {
                    dumps.add(producer.dumpCreationContext("      "));
                }
                for (LeakDetectorMessageConsumer consumer : session.getOpenMessageConsumers()) {
                    dumps.add(consumer.dumpCreationContext("      "));
                }
            }
        }
        return dumps;
    }
}
