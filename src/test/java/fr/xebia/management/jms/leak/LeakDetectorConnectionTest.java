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

import javax.jms.Connection;
import javax.jms.Session;

import static org.junit.Assert.*;

import org.junit.Test;

public class LeakDetectorConnectionTest {

    @Test
    public void test_open_session_list() throws Exception {
        Connection mockConnection = mock(Connection.class);
        LeakDetectorConnection connection = new LeakDetectorConnection(mockConnection, mock(LeakDetectorConnectionFactory.class));
        Session mockSession = mock(Session.class);
        when(mockConnection.createSession(anyBoolean(), anyInt())).thenReturn(mockSession);
        
        List<Session> openSessions = new ArrayList<Session>();
        for(int i = 0;i<10;i++) {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            openSessions.add(session);
            assertEquals(i+1, connection.getOpenSessions().size());
        }
        
        int closedSessions = 0;
        for(Session session: openSessions) {
            session.close();
            closedSessions++;
            assertEquals(openSessions.size() - closedSessions, connection.getOpenSessions().size());
        }
        
        connection.close();
    }

}
