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

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.xebia.jms.wrapper.ConnectionWrapper;

public class LeakDetectorConnection extends ConnectionWrapper implements Connection {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final CreationContext creationContext = new CreationContext();

    private final LeakDetectorConnectionFactory connectionFactory;

    private final Set<LeakDetectorSession> openSessions = Collections.newSetFromMap(new ConcurrentHashMap<LeakDetectorSession, Boolean>());

    public LeakDetectorConnection(Connection delegate, LeakDetectorConnectionFactory connectionFactory) {
        super(delegate);
        this.connectionFactory = connectionFactory;
        connectionFactory.registerOpenConnection(this);
    }

    @Override
    public void close() throws JMSException {
        if (!this.openSessions.isEmpty()) {
            logger.warn("connection.close() is called on {} before closing {} sessions:", this, openSessions.size());
            for (LeakDetectorSession session : this.openSessions) {
                logger.warn(session.dumpCreationContext("   "));
            }
        }
        super.close();
        this.connectionFactory.unregisterOpenConnection(this);
    }

    @Override
    public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
        return new LeakDetectorSession(super.createSession(transacted, acknowledgeMode), this);
    }

    public Set<LeakDetectorSession> getOpenSessions() {
        return Collections.unmodifiableSet(openSessions);
    }

    public void registerOpenSession(LeakDetectorSession session) {
        openSessions.add(session);
    }

    public void unregisterOpenSession(LeakDetectorSession session) {
        openSessions.remove(session);
    }

    public String dumpCreationContext(String offest) {
        return offest + delegate().toString() + " - " + creationContext.dumpContext(offest);
    }
}
